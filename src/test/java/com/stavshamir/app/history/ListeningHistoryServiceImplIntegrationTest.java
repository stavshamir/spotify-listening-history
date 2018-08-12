package com.stavshamir.app.history;

import com.stavshamir.app.authorization.AuthTokens;
import com.stavshamir.app.authorization.AuthTokensService;
import com.stavshamir.app.spotify.SpotifyClient;
import com.stavshamir.app.track.TrackDataService;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ListeningHistoryServiceImplIntegrationTest {

    private static final String TESTER_USER_ID = "spotify:user:9nvqz07deqbr93zrcdeab48rk";
    private static final String TESTER_REFRESH_TOKEN = "AQCvy3mxztjtvBbhU8myT9e1mPlUkATsq0rKb1OjLpEauEOvSIpHhVzXmlgp_GxdrxPY2c-UDVF_Cc7hFLcClKlsZSRJ_pO8vL1T15yG4_eM1SA1HILJdqxXV0mHi55gmYY";

    private static final String MOCK_USER_ID = "mock_user_id";

    private static final int MAX_LIMIT = 50;

    private ListeningHistoryService listeningHistoryService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SpotifyClient spotifyClient;

    @Autowired
    private AuthTokensService authTokensService;

    @Autowired
    private ListeningHistoryRepository listeningHistoryRepository;

    @Autowired
    private MostRecentlyPlayedAtRepository mostRecentlyPlayedAtRepository;

    @Autowired
    private TrackDataService trackDataService;

    @Before
    public void setUp() {
        listeningHistoryService = new ListeningHistoryServiceImpl(spotifyClient, authTokensService, listeningHistoryRepository, mostRecentlyPlayedAtRepository, trackDataService);
    }

    @Test
    @Transactional
    public void getCurrentUsersRecentlyPlayedTracksRequest_new_user() {
        AuthTokens authTokens = new AuthTokens(MOCK_USER_ID, "not relevant", "not relevant");
        entityManager.persist(authTokens);
        entityManager.flush();

        String expectedRequest = spotifyClient
                .getSpotifyApiWithAccessToken(authTokensService.getAccessToken(MOCK_USER_ID))
                .getCurrentUsersRecentlyPlayedTracks()
                .after(new Timestamp(0))
                .limit(MAX_LIMIT)
                .build()
                .getUri()
                .getQuery();

        assertThat(expectedRequest)
                .as("A new user request 'after' field should be Timestamp(0)")
                .isEqualTo(listeningHistoryService.getCurrentUsersRecentlyPlayedTracksRequest(MOCK_USER_ID).getUri().getQuery());
    }

    @Test
    @Transactional
    public void getCurrentUsersRecentlyPlayedTracksRequest_existing_user() {
        AuthTokens authTokens = new AuthTokens(TESTER_USER_ID, "not relevant", TESTER_REFRESH_TOKEN);
        MostRecentlyPlayedAt mostRecentlyPlayedAt = new MostRecentlyPlayedAt(TESTER_USER_ID, new Timestamp(1000));
        persistEntities(Lists.newArrayList(authTokens, mostRecentlyPlayedAt));

        String expectedRequest = spotifyClient
                .getSpotifyApiWithAccessToken(authTokensService.getAccessToken(TESTER_USER_ID))
                .getCurrentUsersRecentlyPlayedTracks()
                .after(new Timestamp(1000))
                .limit(MAX_LIMIT)
                .build()
                .getUri()
                .getQuery();

        assertThat(expectedRequest)
                .as("An existing user request 'after' field should match that in most_recently_played_at table")
                .isEqualTo(listeningHistoryService.getCurrentUsersRecentlyPlayedTracksRequest(TESTER_USER_ID).getUri().getQuery());
    }

    @Test
    @Transactional
    public void persistListeningHistory_only_history_after_most_recently_played_is_persisted() throws IOException, SpotifyWebApiException {
        AuthTokens authTokens = new AuthTokens(TESTER_USER_ID, "not relevant", TESTER_REFRESH_TOKEN);
        MostRecentlyPlayedAt mostRecentlyPlayedAt = new MostRecentlyPlayedAt(TESTER_USER_ID, Timestamp.valueOf("2018-08-08 09:12:37"));
        persistEntities(Lists.newArrayList(authTokens, mostRecentlyPlayedAt));

        listeningHistoryService.persistListeningHistory();
        List<String> listeningHistoryTrackUris = Lists.newArrayList(listeningHistoryRepository.findAll())
                .stream()
                .map(ListeningHistory::getUri)
                .collect(toList());

        String trackAfterMostRecentlyPlayed  = "spotify:track:0D2NQGump2lZJpXMyGKE84";
        String trackAtMostRecentlyPlayed     = "spotify:track:4t3iREL6SRks2dfrVxoI1E";
        String trackBeforeMostRecentlyPlayed = "spotify:track:7vjxo27ux20F8mxCM3zICr";

        assertThat(listeningHistoryTrackUris)
                .as("History should contain track played after the last most recently played, and not tracks at or before")
                .contains(trackAfterMostRecentlyPlayed)
                .doesNotContain(trackAtMostRecentlyPlayed)
                .doesNotContain(trackBeforeMostRecentlyPlayed);

        Timestamp updatedMostRecentlyPlayedAt = mostRecentlyPlayedAtRepository
                .findByUserId(TESTER_USER_ID)
                .map(MostRecentlyPlayedAt::getPlayedAt)
                .orElseThrow(() -> new RuntimeException("test user id not present in in-memory database"));

        assertThat(updatedMostRecentlyPlayedAt)
                .as("Most recently updated value should update to be after the last most recently played")
                .isAfter(Timestamp.valueOf("2018-08-08 09:12:37"));
    }

    private void persistEntities(List<Object> entities) {
        entities.forEach(entityManager::persist);
        entityManager.flush();
    }

}
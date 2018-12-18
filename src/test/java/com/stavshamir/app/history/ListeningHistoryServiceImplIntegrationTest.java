package com.stavshamir.app.history;

import com.stavshamir.app.authorization.AuthTokens;
import com.stavshamir.app.authorization.AuthTokensRepository;
import com.stavshamir.app.authorization.AuthTokensService;
import com.stavshamir.app.spotify.SpotifyClient;
import com.stavshamir.app.track.TrackData;
import com.stavshamir.app.track.TrackDataService;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.IRequest;
import com.wrapper.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ListeningHistoryServiceImplIntegrationTest {

    private static final String TESTER_USER_ID = "spotify:user:9nvqz07deqbr93zrcdeab48rk";
    private static final String TESTER_REFRESH_TOKEN = "AQCvy3mxztjtvBbhU8myT9e1mPlUkATsq0rKb1OjLpEauEOvSIpHhVzXmlgp_GxdrxPY2c-UDVF_Cc7hFLcClKlsZSRJ_pO8vL1T15yG4_eM1SA1HILJdqxXV0mHi55gmYY";

    private static final String MOCK_USER_ID = "mock_user_id";

    private static final int MAX_LIMIT = 50;

    @Autowired
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
    private TrackDataService trackDataService;

    @MockBean
    private MostRecentlyPlayedAtRepository mostRecentlyPlayedAtRepository;

    @MockBean
    private AuthTokensRepository authTokensRepository;

    @Before
    public void setUp() {
        when(authTokensRepository.findByUserId(MOCK_USER_ID))
                .thenReturn(Optional.of(new AuthTokens(MOCK_USER_ID, "not relevant", "not relevant")));

        when(authTokensRepository.findByUserId(TESTER_USER_ID))
                .thenReturn(Optional.of(new AuthTokens(TESTER_USER_ID, "not relevant", TESTER_REFRESH_TOKEN)));
    }

    @Test
    @Transactional
    public void getCurrentUsersRecentlyPlayedTracksRequest_new_user() {
        when(mostRecentlyPlayedAtRepository.findByUserId(MOCK_USER_ID))
                .thenReturn(Optional.empty());

        GetCurrentUsersRecentlyPlayedTracksRequest request = listeningHistoryService.buildGetCurrentUsersRecentlyPlayedTracksRequest(MOCK_USER_ID);

        assertThat(getRequestParam(request, "after"))
                .as("A new user request 'after' field should be Timestamp(0)")
                .isEqualTo(SpotifyApi.formatDefaultDate(new Timestamp(0)));
    }

    @Test
    @Transactional
    public void getCurrentUsersRecentlyPlayedTracksRequest_existing_user() {
        Timestamp mostRecentPlayedAt = new Timestamp(1000);

        when(mostRecentlyPlayedAtRepository.findByUserId(TESTER_USER_ID))
                .thenReturn(Optional.of(new MostRecentlyPlayedAt(TESTER_USER_ID, mostRecentPlayedAt)));

        GetCurrentUsersRecentlyPlayedTracksRequest request = listeningHistoryService
                .buildGetCurrentUsersRecentlyPlayedTracksRequest(TESTER_USER_ID);

        assertThat(getRequestParam(request, "after"))
                .as("An existing user request 'after' field should match that in most_recently_played_at table")
                .isEqualTo(SpotifyApi.formatDefaultDate(mostRecentPlayedAt));
    }

    private String getRequestParam(IRequest request, String paramName) {
        String requestUri = request
                .getUri()
                .toString();

        String decoded;
        try {
            decoded = URLDecoder.decode(requestUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return UriComponentsBuilder
                .fromUriString(decoded)
                .build()
                .getQueryParams()
                .get(paramName).get(0);
    }

    @Test
    @Transactional
    public void persistListeningHistoryForUser_only_history_after_most_recently_played_is_persisted()
            throws IOException, SpotifyWebApiException {
        MostRecentlyPlayedAt mostRecentlyPlayedAt = new MostRecentlyPlayedAt(TESTER_USER_ID, Timestamp.valueOf("2017-08-08 09:12:37"));
        when(mostRecentlyPlayedAtRepository.findByUserId(TESTER_USER_ID))
                .thenReturn(Optional.of(mostRecentlyPlayedAt));

        listeningHistoryService.persistListeningHistoryForUser(TESTER_USER_ID);
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

    @Test
    @Transactional
    public void getListeningHistory() throws IOException, SpotifyWebApiException {
        persistEntities(Lists.newArrayList(
                // ListeningHistory
                new ListeningHistory(TESTER_USER_ID, "1", new Timestamp(1000)),
                new ListeningHistory(TESTER_USER_ID, "2", new Timestamp(2000)),
                new ListeningHistory(TESTER_USER_ID, "3", new Timestamp(3000)),

                // TrackData
                new TrackData("1", "foo1", null, "", ""),
                new TrackData("2", "foo2", null, "", ""),
                new TrackData("3", "foo3", null, "", "")
        ));

        assertThat(getHistoryTrackUris(0, 4000000000000L))
                .as("History after 0 should contain all track uris")
                .contains("1")
                .contains("2")
                .contains("3");

        assertThat(getHistoryTrackUris(2000, 4000000000000L))
                .as("History should contain only tracks uris at and after specified time")
                .doesNotContain("1")
                .contains("2")
                .contains("3");

        assertThat(getHistoryTrackUris(4000, 4000000000000L))
                .as("History after the last recorded date should be an empty list")
                .isEmpty();

        assertThat(getHistoryTrackUris(0, 3000))
                .as("History should contain only tracks uris before specified time")
                .contains("1")
                .contains("2")
                .doesNotContain("3");

        assertThat(getHistoryTrackUris(2000, 3000))
                .as("History should contain only tracks uris between specified times")
                .doesNotContain("1")
                .contains("2")
                .doesNotContain("3");
    }

    private List<String> getHistoryTrackUris(long after, long before) throws IOException, SpotifyWebApiException {
        return listeningHistoryService
                .getListeningHistory(TESTER_USER_ID, new Timestamp(after), new Timestamp(before), PageRequest.of(0, 3))
                .stream()
                .map(i -> i.getTrackData().getUri())
                .collect(toList());
    }

    @Test
    @Transactional
    public void getMostPlayed_fromYear_and_toYear() throws IOException, SpotifyWebApiException {
        final TrackData track1 = new TrackData("1", "foo1", null, "", "");
        final TrackData track2 = new TrackData("2", "foo2", null, "", "");
        final TrackData track3 = new TrackData("3", "foo3", null, "", "");

        persistEntities(Lists.newArrayList(
                // ListeningHistory
                new ListeningHistory(TESTER_USER_ID, "1", Timestamp.valueOf("2015-01-01 00:00:00")),
                new ListeningHistory(TESTER_USER_ID, "1", Timestamp.valueOf("2017-01-01 00:00:00")),
                new ListeningHistory(TESTER_USER_ID, "1", Timestamp.valueOf("2018-01-01 00:00:00")),
                new ListeningHistory(TESTER_USER_ID, "2", Timestamp.valueOf("2018-01-01 00:00:00")),
                new ListeningHistory(TESTER_USER_ID, "2", Timestamp.valueOf("2018-01-01 00:00:00")),
                new ListeningHistory(TESTER_USER_ID, "3", Timestamp.valueOf("2017-01-01 00:00:00")),

                // TrackData
                track1,
                track2,
                track3
        ));

        final PageRequest pageable = PageRequest.of(0, 3);

        GetMostPlayedQuery query = GetMostPlayedQuery.builder(TESTER_USER_ID)
                .fromYear(2017)
                .build();

        assertThat(listeningHistoryService.getMostPlayed(query))
                .as("From year")
                .hasSize(3)
                .contains(new TrackDataWithPlayCount(track1, 2))
                .contains(new TrackDataWithPlayCount(track2, 2))
                .contains(new TrackDataWithPlayCount(track3, 1));

        query = GetMostPlayedQuery.builder(TESTER_USER_ID)
                .toYear(2017)
                .build();

        assertThat(listeningHistoryService.getMostPlayed(query))
                .as("To year")
                .hasSize(1)
                .contains(new TrackDataWithPlayCount(track1, 1));

        query = GetMostPlayedQuery.builder(TESTER_USER_ID)
                .fromYear(2016)
                .toYear(2019)
                .build();

        assertThat(listeningHistoryService.getMostPlayed(query))
                .as("Between years")
                .hasSize(3)
                .contains(new TrackDataWithPlayCount(track1, 2))
                .contains(new TrackDataWithPlayCount(track2, 2))
                .contains(new TrackDataWithPlayCount(track3, 1));
    }

    @Test
    @Transactional
    public void getMostPlayed_fromMonth_and_toMonth() throws IOException, SpotifyWebApiException {
        final TrackData track1 = new TrackData("1", "foo1", null, "", "");
        final TrackData track2 = new TrackData("2", "foo2", null, "", "");
        final TrackData track3 = new TrackData("3", "foo3", null, "", "");

        persistEntities(Lists.newArrayList(
                // ListeningHistory
                new ListeningHistory(TESTER_USER_ID, "1", Timestamp.valueOf("2015-01-01 00:00:00")),
                new ListeningHistory(TESTER_USER_ID, "1", Timestamp.valueOf("2017-02-01 00:00:00")),
                new ListeningHistory(TESTER_USER_ID, "1", Timestamp.valueOf("2018-03-01 00:00:00")),
                new ListeningHistory(TESTER_USER_ID, "2", Timestamp.valueOf("2018-01-01 00:00:00")),
                new ListeningHistory(TESTER_USER_ID, "2", Timestamp.valueOf("2018-02-01 00:00:00")),
                new ListeningHistory(TESTER_USER_ID, "3", Timestamp.valueOf("2017-03-01 00:00:00")),

                // TrackData
                track1,
                track2,
                track3
        ));

        final PageRequest pageable = PageRequest.of(0, 3);

        GetMostPlayedQuery query = GetMostPlayedQuery.builder(TESTER_USER_ID)
                .fromMonth(2)
                .build();

        assertThat(listeningHistoryService.getMostPlayed(query))
                .as("From month")
                .hasSize(3)
                .contains(new TrackDataWithPlayCount(track1, 2))
                .contains(new TrackDataWithPlayCount(track2, 1))
                .contains(new TrackDataWithPlayCount(track3, 1));

        query = GetMostPlayedQuery.builder(TESTER_USER_ID)
                .toMonth(2)
                .build();

        assertThat(listeningHistoryService.getMostPlayed(query))
                .as("To month")
                .hasSize(2)
                .contains(new TrackDataWithPlayCount(track1, 1))
                .contains(new TrackDataWithPlayCount(track2, 1));

        query = GetMostPlayedQuery.builder(TESTER_USER_ID)
                .fromMonth(2)
                .toMonth(4)
                .build();

        assertThat(listeningHistoryService.getMostPlayed(query))
                .as("Between months")
                .hasSize(3)
                .contains(new TrackDataWithPlayCount(track1, 2))
                .contains(new TrackDataWithPlayCount(track2, 1))
                .contains(new TrackDataWithPlayCount(track3, 1));
    }

    @Test
    @Transactional
    public void getMostPlayed_fromHour_and_toHour() throws IOException, SpotifyWebApiException {
        final TrackData track1 = new TrackData("1", "foo1", null, "", "");
        final TrackData track2 = new TrackData("2", "foo2", null, "", "");
        final TrackData track3 = new TrackData("3", "foo3", null, "", "");

        persistEntities(Lists.newArrayList(
                // ListeningHistory
                new ListeningHistory(TESTER_USER_ID, "1", Timestamp.valueOf("2015-01-01 02:00:00")),
                new ListeningHistory(TESTER_USER_ID, "1", Timestamp.valueOf("2017-02-01 01:00:00")),
                new ListeningHistory(TESTER_USER_ID, "1", Timestamp.valueOf("2018-03-01 05:00:00")),
                new ListeningHistory(TESTER_USER_ID, "2", Timestamp.valueOf("2018-01-01 02:30:00")),
                new ListeningHistory(TESTER_USER_ID, "2", Timestamp.valueOf("2018-02-01 03:00:00")),
                new ListeningHistory(TESTER_USER_ID, "3", Timestamp.valueOf("2017-03-01 07:00:00")),

                // TrackData
                track1,
                track2,
                track3
        ));

        GetMostPlayedQuery query = GetMostPlayedQuery.builder(TESTER_USER_ID)
                .fromHour(2)
                .build();

        assertThat(listeningHistoryService.getMostPlayed(query))
                .as("From hour")
                .hasSize(3)
                .contains(new TrackDataWithPlayCount(track1, 2))
                .contains(new TrackDataWithPlayCount(track2, 2))
                .contains(new TrackDataWithPlayCount(track3, 1));

        query = GetMostPlayedQuery.builder(TESTER_USER_ID)
                .toHour(5)
                .build();

        assertThat(listeningHistoryService.getMostPlayed(query))
                .as("To hour")
                .hasSize(2)
                .contains(new TrackDataWithPlayCount(track1, 2))
                .contains(new TrackDataWithPlayCount(track2, 2));

        query = GetMostPlayedQuery.builder(TESTER_USER_ID)
                .fromHour(3)
                .toHour(12)
                .build();

        assertThat(listeningHistoryService.getMostPlayed(query))
                .as("Between hours")
                .hasSize(3)
                .contains(new TrackDataWithPlayCount(track1, 1))
                .contains(new TrackDataWithPlayCount(track2, 1))
                .contains(new TrackDataWithPlayCount(track3, 1));
    }

    private void persistEntities(List<Object> entities) {
        entities.forEach(entityManager::persist);
        entityManager.flush();
    }

}

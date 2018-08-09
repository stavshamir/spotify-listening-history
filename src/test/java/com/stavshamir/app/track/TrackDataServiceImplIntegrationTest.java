package com.stavshamir.app.track;

import com.stavshamir.app.authorization.AuthTokens;
import com.stavshamir.app.authorization.AuthTokensService;
import com.stavshamir.app.spotify.SpotifyClient;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TrackDataServiceImplIntegrationTest {


    private static final String TESTER_USER_ID = "spotify:user:9nvqz07deqbr93zrcdeab48rk";
    private static final String TESTER_REFRESH_TOKEN = "AQCvy3mxztjtvBbhU8myT9e1mPlUkATsq0rKb1OjLpEauEOvSIpHhVzXmlgp_GxdrxPY2c-UDVF_Cc7hFLcClKlsZSRJ_pO8vL1T15yG4_eM1SA1HILJdqxXV0mHi55gmYY";

    private TrackDataService trackDataService;

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private SpotifyClient spotifyClient;

    @Autowired
    private TrackDataRepository trackDataRepository;

    @Autowired
    private AuthTokensService authTokensService;

    @Mock
    private SpotifyClient spotifyClientMock;

    @Test
    @Transactional
    public void getTrackData_data_is_not_in_db() throws IOException, SpotifyWebApiException {
        trackDataService = new TrackDataServiceImpl(spotifyClient, trackDataRepository, authTokensService);
        entityManager.persist(new AuthTokens(TESTER_USER_ID, "not relevant", TESTER_REFRESH_TOKEN));
        entityManager.flush();

        String testTrackUri = "spotify:track:7DsVUde9c0lVje0FtuIYoY";
        TrackData expected = new TrackData(testTrackUri, "", new String[1], "", "");

        assertThat(trackDataService.getTrackData(testTrackUri, TESTER_USER_ID))
                .isEqualTo(expected);

        assertThat(trackDataRepository.findById(testTrackUri))
                .as("The track should be persisted after it was retrieved")
                .isPresent();
    }

    @Test
    @Transactional
    public void getTrackData_data_is_in_db() throws IOException, SpotifyWebApiException {
        MockitoAnnotations.initMocks(this);
        when(spotifyClientMock.getSpotifyApi())
                .thenThrow(new RuntimeException("No request to Spotify should be made in case the track data is already stored"));
        entityManager.persist(new TrackData("uri", "", new String[1], "", ""));
        entityManager.flush();

        trackDataService = new TrackDataServiceImpl(spotifyClientMock, trackDataRepository, authTokensService);
        assertThat(trackDataService.getTrackData("uri", TESTER_USER_ID))
                .isEqualTo(new TrackData("uri", "", new String[1], "", ""));
    }

}
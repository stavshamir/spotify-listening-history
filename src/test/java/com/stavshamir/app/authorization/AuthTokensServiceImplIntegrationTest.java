package com.stavshamir.app.authorization;

import com.stavshamir.app.spotify.SpotifyClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SpotifyClient.class })
public class AuthTokensServiceImplIntegrationTest {

    private static final String TESTER_USER_ID = "spotify:user:9nvqz07deqbr93zrcdeab48rk";
    private static final String TESTER_REFRESH_TOKEN = "AQCvy3mxztjtvBbhU8myT9e1mPlUkATsq0rKb1OjLpEauEOvSIpHhVzXmlgp_GxdrxPY2c-UDVF_Cc7hFLcClKlsZSRJ_pO8vL1T15yG4_eM1SA1HILJdqxXV0mHi55gmYY";

    private AuthTokensService authTokensService;

    @Autowired
    private SpotifyClient spotifyClient;

    @Mock
    private AuthTokensRepository authTokensRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        authTokensService = new AuthTokensServiceImpl(spotifyClient, authTokensRepository);
    }

    private void setUp_getAccessToken() {
        when(authTokensRepository.findByUserId(TESTER_USER_ID))
                .thenReturn(Optional.of(new AuthTokens(TESTER_USER_ID, "not relevant", TESTER_REFRESH_TOKEN)));
    }

    @Test
    public void getAccessToken() {
        setUp_getAccessToken();

        String firstAccessToken = authTokensService.getAccessToken(TESTER_USER_ID);
        String secondAccessToken = authTokensService.getAccessToken(TESTER_USER_ID);

        assertThat(secondAccessToken)
                .as("Each call to getAccessToken must return a new access token")
                .isNotEqualTo(firstAccessToken);
    }

}
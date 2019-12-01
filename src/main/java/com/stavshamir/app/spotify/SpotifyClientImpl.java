package com.stavshamir.app.spotify;

import com.stavshamir.app.spotify.types.UserCredentials;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.PlayHistory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;

@Component
public class SpotifyClientImpl implements SpotifyClient {

    private static final String CLIENT_ID = getEnvSafely("CLIENT_ID");
    private static final String CLIENT_SECRET = getEnvSafely("CLIENT_SECRET");
    private static final String REDIRECT_URI = getEnvSafely("REDIRECT_URI");

    private static final int HISTORY_MAX_LIMIT = 50;

    private static String getEnvSafely(String envVariableName) {
        return Optional
                .ofNullable(System.getenv(envVariableName))
                .orElseThrow(() -> new IllegalStateException("No " + envVariableName + " environment variable"));
    }

    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setClientSecret(CLIENT_SECRET)
            .setRedirectUri(SpotifyHttpManager.makeUri(REDIRECT_URI))
            .build();

    @Override
    public SpotifyApi getSpotifyApi() {
        spotifyApi.setAccessToken(null);
        return spotifyApi;
    }

    @Override
    public SpotifyApi getSpotifyApiWithAccessToken(String accessToken) {
        spotifyApi.setAccessToken(accessToken);
        return spotifyApi;
    }

    @Override
    public UserCredentials requestCredentials(String code) throws IOException, SpotifyWebApiException {
        AuthorizationCodeCredentials credentials = getSpotifyApi()
                .authorizationCode(code)
                .build()
                .execute();

        return new UserCredentials(credentials.getAccessToken(), credentials.getRefreshToken());
    }

    @Override
    public String requestUserId(String accessToken) throws IOException, SpotifyWebApiException {
        spotifyApi.setAccessToken(accessToken);
        return spotifyApi
                .getCurrentUsersProfile()
                .build()
                .execute()
                .getUri();
    }

    @Override
    public UserCredentials refreshCredentials(String refreshToken) throws IOException, SpotifyWebApiException {
        spotifyApi.setRefreshToken(refreshToken);

        AuthorizationCodeCredentials credentials = spotifyApi
                .authorizationCodeRefresh()
                .build()
                .execute();

        return new UserCredentials(credentials.getAccessToken(), credentials.getRefreshToken());
    }

    @Override
    public String requestAuthorization(String scope) {
        return getSpotifyApi()
                .authorizationCodeUri()
                .scope(scope)
                .build()
                .execute()
                .toString();
    }

    @Override
    public PlayHistory[] getListeningHistory(String accessToken, Timestamp after) throws IOException, SpotifyWebApiException {
        return getSpotifyApiWithAccessToken(accessToken)
                .getCurrentUsersRecentlyPlayedTracks()
                .after(after)
                .limit(HISTORY_MAX_LIMIT)
                .build()
                .execute()
                .getItems();
    }
}

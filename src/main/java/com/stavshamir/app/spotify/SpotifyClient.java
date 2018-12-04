package com.stavshamir.app.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class SpotifyClient {

    private static final String CLIENT_ID     = getEnvSafely("CLIENT_ID");
    private static final String CLIENT_SECRET = getEnvSafely("CLIENT_SECRET");
    private static final String REDIRECT_URI  = getEnvSafely("REDIRECT_URI");

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

    public SpotifyApi getSpotifyApi() {
        spotifyApi.setAccessToken(null);
        return spotifyApi;
    }

    public SpotifyApi getSpotifyApiWithAccessToken(String accessToken) {
        spotifyApi.setAccessToken(accessToken);
        return spotifyApi;
    }
}

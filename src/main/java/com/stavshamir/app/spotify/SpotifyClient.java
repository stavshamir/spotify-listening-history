package com.stavshamir.app.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class SpotifyClient {

    private static final String CLIENT_ID     = System.getenv("CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    private static final URI    REDIRECT_URI  = SpotifyHttpManager.makeUri(System.getenv("REDIRECT_URI"));

    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(CLIENT_ID)
                .setClientSecret(CLIENT_SECRET)
                .setRedirectUri(REDIRECT_URI)
                .build();

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    public SpotifyApi getSpotifyApiWithAccessToken(String accessToken) {
        spotifyApi.setAccessToken(accessToken);
        return spotifyApi;
    }
}

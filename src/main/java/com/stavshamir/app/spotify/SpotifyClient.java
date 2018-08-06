package com.stavshamir.app.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class SpotifyClient {
    private final String CLIENT_ID = "94002a67e5704e2294bd4a5874cddec6";
    private final String CLIENT_SECRET = "f57133b0d830479ca8e17e4c9b5bf76d";
    private final URI REDIRECT_URI = SpotifyHttpManager.makeUri("http://localhost:5000/callback");
//    private final URI REDIRECT_URI = SpotifyHttpManager.makeUri("https://guarded-bayou-27287.herokuapp.com/callback");

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

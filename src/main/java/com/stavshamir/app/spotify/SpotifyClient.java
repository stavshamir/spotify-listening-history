package com.stavshamir.app.spotify;

import com.stavshamir.app.spotify.types.UserCredentials;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PlayHistory;

import java.io.IOException;
import java.sql.Timestamp;

public interface SpotifyClient {
    SpotifyApi getSpotifyApi();

    SpotifyApi getSpotifyApiWithAccessToken(String accessToken);

    UserCredentials requestCredentials(String code) throws IOException, SpotifyWebApiException;

    String requestUserId(String accessToken) throws IOException, SpotifyWebApiException;

    UserCredentials refreshCredentials(String refreshToken) throws IOException, SpotifyWebApiException;

    String requestAuthorization(String scope);

    PlayHistory[] getListeningHistory(String accessToken, Timestamp after) throws IOException, SpotifyWebApiException;
}

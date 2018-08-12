package com.stavshamir.app.authorization;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.util.List;

public interface AuthTokensService {

    AuthorizationCodeUriRequest getAuthorizationCodeUriRequest(String scope);

    /**
     * Swap the given code for access and refresh tokens from SpotifyApi, persist them and return the uri of the current user.
     * @param code received by the authorization endpoint callback.
     * @return the uri of the current user.
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    String retrieveAndPersistTokens(String code) throws IOException, SpotifyWebApiException;

    /**
     * Return an access token for the user with this userId.
     * The returned token is guaranteed to be active for the next hour.
     *
     * @param userId The id (uri) of this user.
     * @return an access token for the user with this userId.
     */
    String getAccessToken(String userId);

    List<String> getAllUserIds();

}

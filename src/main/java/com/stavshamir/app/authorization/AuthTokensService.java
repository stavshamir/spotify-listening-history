package com.stavshamir.app.authorization;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;

public interface AuthTokensService {

    AuthorizationCodeUriRequest getAuthorizationCodeUriRequest(String scope);
    void retrieveAndPersistTokens(String code) throws IOException, SpotifyWebApiException;

}

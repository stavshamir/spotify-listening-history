package com.stavshamir.app.authorization;

import com.stavshamir.app.spotify.SpotifyClient;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AuthTokensServiceImpl implements AuthTokensService {

    private Logger logger = LoggerFactory.getLogger(AuthTokensServiceImpl.class);

    private final SpotifyClient spotifyClient;
    private final AuthTokensRepository authTokensRepository;

    @Autowired
    public AuthTokensServiceImpl(SpotifyClient spotifyClient, AuthTokensRepository authTokensRepository) {
        this.spotifyClient = spotifyClient;
        this.authTokensRepository = authTokensRepository;
    }

    @Override
    public AuthorizationCodeUriRequest getAuthorizationCodeUriRequest(String scope) {
        return spotifyClient
                .getSpotifyApi()
                .authorizationCodeUri()
                .scope(scope)
                .build();
    }

    @Override
    public String retrieveAndPersistTokens(String code) throws IOException, SpotifyWebApiException {
        AuthorizationCodeCredentials credentials = getAuthorizationCodeRequest(code).execute();

        String userId = spotifyClient.getSpotifyApiWithAccessToken(credentials.getAccessToken())
                .getCurrentUsersProfile()
                .build()
                .execute()
                .getUri();

        persistTokens(userId, credentials);
        return userId;
    }

    private AuthorizationCodeRequest getAuthorizationCodeRequest(String code) {
        return spotifyClient
                .getSpotifyApi()
                .authorizationCode(code)
                .build();
    }

    private void persistTokens(String userId, AuthorizationCodeCredentials credentials) {
        String accessToken = credentials.getAccessToken();
        String refreshToken = credentials.getRefreshToken();

        insertOrUpdateIfExists(userId, accessToken, refreshToken);
    }

    private void insertOrUpdateIfExists(String userId, String accessToken, String refreshToken) {
        AuthTokens auth = authTokensRepository
                .findByUserId(userId)
                .orElse(new AuthTokens(userId, accessToken, refreshToken));

        auth.setAccessToken(accessToken);
        authTokensRepository.save(auth);
    }

    @Override
    public String getAccessToken(String userId) {
        refreshAuthorization(userId);

        return authTokensRepository
                .findByUserId(userId)
                .map(AuthTokens::getAccessToken)
                .orElseThrow(NoAuthTokensProvidedException::new);
    }

    private void refreshAuthorization(String userId) {
        spotifyClient.getSpotifyApi()
                .setRefreshToken(getRefreshToken(userId));

        AuthorizationCodeRefreshRequest refreshRequest = spotifyClient
                .getSpotifyApi()
                .authorizationCodeRefresh()
                .build();

        try {
            persistTokens(userId, refreshRequest.execute());
        } catch (IOException | SpotifyWebApiException e) {
            logger.error("Failed to retrieve authorization credentials from Spotify API: " + e.getMessage());
        }
    }

    private String getRefreshToken(String userId) {
        return authTokensRepository
                .findByUserId(userId)
                .map(AuthTokens::getRefreshToken)
                .orElseThrow(NoAuthTokensProvidedException::new);
    }

    @Override
    public List<String> getAllUserIds() {
        return authTokensRepository.findAll()
                .stream()
                .map(AuthTokens::getUserId)
                .collect(toList());
    }

}

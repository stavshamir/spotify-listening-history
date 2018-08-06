package com.stavshamir.app.authorization;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AuthTokens {

    @Id
    private String userId;

    private String accessToken;
    private String refreshToken;

    public AuthTokens() {}

    public AuthTokens(String userId, String accessToken, String refreshToken) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

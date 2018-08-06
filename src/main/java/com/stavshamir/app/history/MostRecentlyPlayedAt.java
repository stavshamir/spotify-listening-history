package com.stavshamir.app.history;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class MostRecentlyPlayedAt {

    @Id
    private String userId;

    private Timestamp playedAt;

    public MostRecentlyPlayedAt() {
    }

    public MostRecentlyPlayedAt(String userId, Timestamp playedAt) {
        this.userId = userId;
        this.playedAt = playedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Timestamp playedAt) {
        this.playedAt = playedAt;
    }
}

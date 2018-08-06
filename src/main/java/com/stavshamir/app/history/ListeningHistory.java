package com.stavshamir.app.history;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class ListeningHistory implements Comparable<ListeningHistory> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String userId;
    private String uri;
    private Timestamp playedAt;

    public ListeningHistory() {}

    public ListeningHistory(String userId, String uri, Timestamp playedAt) {
        this.userId = userId;
        this.uri = uri;
        this.playedAt = playedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Timestamp getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Timestamp playedAt) {
        this.playedAt = playedAt;
    }

    @Override
    public int compareTo(ListeningHistory other) {
        return (int)(this.getPlayedAt().getTime() - other.getPlayedAt().getTime());
    }

}

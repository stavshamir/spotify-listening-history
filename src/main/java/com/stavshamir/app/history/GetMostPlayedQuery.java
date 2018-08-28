package com.stavshamir.app.history;

import java.sql.Timestamp;

public class GetMostPlayedQuery {

    private static final int DEFAULT_SIZE = 25;
    private static final Timestamp DEFAULT_AFTER = new Timestamp(0);
    private static final Timestamp DEFAULT_BEFORE = Timestamp.valueOf("2100-01-01 00:00:00");

    private String userUri;
    private int size;
    private Timestamp after;
    private Timestamp before;

    private GetMostPlayedQuery(String userUri, int size, Timestamp after, Timestamp before) {
        this.userUri = userUri;
        this.size = size;
        this.after = after;
        this.before = before;
    }

    String getUserUri() {
        return userUri;
    }

    int getSize() {
        return size;
    }

    Timestamp getAfter() {
        return after;
    }

    Timestamp getBefore() {
        return before;
    }

    public static Builder builder(String userUri) {
        return new Builder(userUri);
    }

    public static class Builder {

        private String userUri;
        private int size = DEFAULT_SIZE;
        private Timestamp after = DEFAULT_AFTER;
        private Timestamp before = DEFAULT_BEFORE;

        private Builder(String userUri) {
            this.userUri = userUri;
        }

        public Builder size(Integer size) {
            this.size = size == null ? DEFAULT_SIZE : size;
            return this;
        }

        public Builder after(Timestamp after) {
            this.after = after;
            return this;
        }

        public Builder before(Timestamp before) {
            this.before = before;
            return this;
        }

        public Builder after(Long afterEpoch) {
            return after(afterEpoch == null ? DEFAULT_AFTER : new Timestamp(afterEpoch));
        }

        public Builder before(Long beforeEpoch) {
            return before(beforeEpoch == null ? DEFAULT_BEFORE : new Timestamp(beforeEpoch));
        }

        public GetMostPlayedQuery build() {
            return new GetMostPlayedQuery(userUri, size, after, before);
        }

    }

}

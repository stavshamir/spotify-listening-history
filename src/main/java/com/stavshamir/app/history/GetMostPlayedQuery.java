package com.stavshamir.app.history;

import java.sql.Timestamp;

public class GetMostPlayedQuery {

    private static final Timestamp DEFAULT_AFTER = new Timestamp(0);
    private static final Timestamp DEFAULT_BEFORE = Timestamp.valueOf("2100-01-01 00:00:00");
    private static final int DEFAULT_FROM_YEAR = 0;
    private static final int DEFAULT_TO_YEAR = 2100;
    private static final int DEFAULT_FROM_MONTH = 1;
    private static final int DEFAULT_TO_MONTH = 13;
    private static final int DEFAULT_FROM_HOUR = 0;
    private static final int DEFAULT_TO_HOUR = 24;

    private String userUri;
    private Timestamp after;
    private Timestamp before;
    private int fromYear;
    private int toYear;
    private int fromMonth;
    private int toMonth;
    private int fromHour;
    private int toHour;

    private GetMostPlayedQuery(String userUri, Timestamp after, Timestamp before, int fromYear, int toYear, int fromMonth, int toMonth, int fromHour, int toHour) {
        this.userUri = userUri;
        this.after = after;
        this.before = before;
        this.fromYear = fromYear;
        this.toYear = toYear;
        this.fromMonth = fromMonth;
        this.toMonth = toMonth;
        this.fromHour = fromHour;
        this.toHour = toHour;
    }

    String getUserUri() {
        return userUri;
    }

    Timestamp getAfter() {
        return after;
    }

    Timestamp getBefore() {
        return before;
    }

    int getFromYear() {
        return fromYear;
    }

    int getToYear() {
        return toYear;
    }

    public int getFromMonth() {
        return fromMonth;
    }

    public int getToMonth() {
        return toMonth;
    }

    public int getFromHour() {
        return fromHour;
    }

    public int getToHour() {
        return toHour;
    }

    public static Builder builder(String userUri) {
        return new Builder(userUri);
    }

    public static class Builder {

        private String userUri;
        private Timestamp after = DEFAULT_AFTER;
        private Timestamp before = DEFAULT_BEFORE;
        private int fromYear = DEFAULT_FROM_YEAR;
        private int toYear = DEFAULT_TO_YEAR;
        private int fromMonth = DEFAULT_FROM_MONTH;
        private int toMonth = DEFAULT_TO_MONTH;
        private int fromHour = DEFAULT_FROM_HOUR;
        private int toHour = DEFAULT_TO_HOUR;

        private Builder(String userUri) {
            this.userUri = userUri;
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

        public Builder fromYear(Integer year) {
            this.fromYear = year == null ? DEFAULT_FROM_YEAR : year;
            return this;
        }

        public Builder toYear(Integer year) {
            this.toYear = year == null ? DEFAULT_TO_YEAR : year;
            return this;
        }

        public Builder fromMonth(Integer month) {
            this.fromMonth = month == null ? DEFAULT_FROM_MONTH : month;
            return this;
        }

        public Builder toMonth(Integer month) {
            this.toMonth = month == null ? DEFAULT_TO_MONTH : month;
            return this;
        }

        public Builder fromHour(Integer hour) {
            this.fromHour = hour == null ? DEFAULT_FROM_HOUR : hour;
            return this;
        }

        public Builder toHour(Integer hour) {
            this.toHour = hour == null ? DEFAULT_TO_HOUR : hour;
            return this;
        }

        public GetMostPlayedQuery build() {
            return new GetMostPlayedQuery(userUri, after, before, fromYear, toYear, fromMonth, toMonth, fromHour, toHour);
        }

    }

}

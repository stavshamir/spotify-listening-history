package com.stavshamir.app.history;

import com.stavshamir.app.track.TrackData;

import java.sql.Timestamp;

public class TrackDataWithPlayedAt {

    private final TrackData trackData;
    private final Timestamp playedAt;

    public TrackDataWithPlayedAt(TrackData trackData, Timestamp playedAt) {
        this.trackData = trackData;
        this.playedAt = playedAt;
    }

    public TrackData getTrackData() {
        return trackData;
    }

    public Timestamp getPlayedAt() {
        return playedAt;
    }

}

package com.stavshamir.app.history;

import com.stavshamir.app.track.TrackData;

import java.util.Objects;

public class TrackDataWithPlayCount {

    private final TrackData trackData;
    private final int count;

    public TrackDataWithPlayCount(TrackData trackData, int count) {
        this.trackData = trackData;
        this.count = count;
    }

    public TrackData getTrackData() {
        return trackData;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackDataWithPlayCount that = (TrackDataWithPlayCount) o;
        return count == that.count &&
                Objects.equals(trackData, that.trackData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackData, count);
    }

}

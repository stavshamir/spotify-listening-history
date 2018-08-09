package com.stavshamir.app.track;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

public interface TrackDataService {

    TrackData getTrackData(String uri, String userUri) throws IOException, SpotifyWebApiException;

}

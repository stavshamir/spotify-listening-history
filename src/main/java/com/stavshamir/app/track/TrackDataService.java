package com.stavshamir.app.track;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

public interface TrackDataService {

    TrackData getTrackData(String trackUri, String userUri) throws IOException, SpotifyWebApiException;

}

package com.stavshamir.app.history;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;

import java.io.IOException;

public interface ListeningHistoryService {
    GetCurrentUsersRecentlyPlayedTracksRequest getCurrentUsersRecentlyPlayedTracksRequest(String userId);
    void persistListeningHistory() throws IOException, SpotifyWebApiException;
}

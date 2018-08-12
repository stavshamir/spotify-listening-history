package com.stavshamir.app.history;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.sql.Timestamp;

public interface ListeningHistoryService {
    GetCurrentUsersRecentlyPlayedTracksRequest getCurrentUsersRecentlyPlayedTracksRequest(String userId);

    void persistListeningHistory() throws IOException, SpotifyWebApiException;

    /**
     * Persist the listening history of this user (after most recently played at), and return the persisted history as requested
     * @param userUri   the user's Spotify uri of format spotify::user::foo
     * @param after     only tracks played after the data and time specified by this variable will be returned
     * @param pageable
     * @return  A paginated listening history, including track data
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    Page<TrackDataWithPlayedAt> getListeningHistory(String userUri, Timestamp after, Pageable pageable) throws IOException, SpotifyWebApiException;
}

package com.stavshamir.app.history;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public interface ListeningHistoryService {

    GetCurrentUsersRecentlyPlayedTracksRequest buildGetCurrentUsersRecentlyPlayedTracksRequest(String userId);

    /**
     * Persist the listening history for all registered user ids.
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    void persistListeningHistory() throws IOException, SpotifyWebApiException;

    /**
     * Persist the listening history for the specified user id.
     * @param userId Spotify user id to persist the listening history for.
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    void persistListeningHistoryForUser(String userId) throws IOException, SpotifyWebApiException;

    /**
     * Return the persisted history after (inclusive) and after (not inclusive) specified time.
     * @param userUri   the user's Spotify uri of format spotify::user::foo
     * @param after     only tracks played at and after the data and time specified by this variable will be returned
     * @param before    only tracks played before the data and time specified by this variable will be returned
     * @param pageable
     * @return  A paginated listening history, including track data
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    Page<TrackDataWithPlayedAt> getListeningHistory(String userUri, Timestamp after, Timestamp before, Pageable pageable) throws IOException, SpotifyWebApiException;

    /**
     * Return the (at most 50) most played tracks' data and play count.
     * @param getMostPlayedQuery
     * @return  the most played tracks' data and play count.
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    List<TrackDataWithPlayCount> getMostPlayed(GetMostPlayedQuery getMostPlayedQuery) throws IOException, SpotifyWebApiException;

}

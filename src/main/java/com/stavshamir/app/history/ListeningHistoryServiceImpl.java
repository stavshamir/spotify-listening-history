package com.stavshamir.app.history;

import com.stavshamir.app.authorization.AuthTokensService;
import com.stavshamir.app.spotify.SpotifyClient;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PagingCursorbased;
import com.wrapper.spotify.model_objects.specification.PlayHistory;
import com.wrapper.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ListeningHistoryServiceImpl implements ListeningHistoryService {

    private Logger logger = LoggerFactory.getLogger(ListeningHistoryServiceImpl.class);

    private static final int MAX_LIMIT = 50;

    private final SpotifyClient spotifyClient;
    private final AuthTokensService authTokensService;
    private final ListeningHistoryRepository listeningHistoryRepository;

    @Autowired
    public ListeningHistoryServiceImpl(SpotifyClient spotifyClient, AuthTokensService authTokensService, ListeningHistoryRepository listeningHistoryRepository) {
        this.spotifyClient = spotifyClient;
        this.authTokensService = authTokensService;
        this.listeningHistoryRepository = listeningHistoryRepository;
    }

    @Override
    public GetCurrentUsersRecentlyPlayedTracksRequest getCurrentUsersRecentlyPlayedTracksRequest(String userId) {
        return spotifyClient
                .getSpotifyApiWithAccessToken(authTokensService.getAccessToken(userId))
                .getCurrentUsersRecentlyPlayedTracks()
                .limit(MAX_LIMIT)
                .build();
    }

    @Override
    public void persistListeningHistory() throws IOException, SpotifyWebApiException {
        for (String userId : authTokensService.getAllUserIds()) {
            persistListeningHistoryForUser(userId);
        }
    }

    private void persistListeningHistoryForUser(String userId) throws IOException, SpotifyWebApiException {
        PagingCursorbased<PlayHistory> tracks = getCurrentUsersRecentlyPlayedTracksRequest(userId).execute();

        List<ListeningHistory> history = Arrays.stream(tracks.getItems())
                .map(item -> fromPlayHistoryItem(userId, item))
                .collect(toList());

        listeningHistoryRepository.saveAll(history);
    }

    private static ListeningHistory fromPlayHistoryItem(String userId, PlayHistory item) {
        return new ListeningHistory(
                userId,
                item.getTrack().getUri(),
                new Timestamp(item.getPlayedAt().getTime())
        );
    }
}

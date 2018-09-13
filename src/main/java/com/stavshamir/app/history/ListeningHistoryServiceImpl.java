package com.stavshamir.app.history;

import com.stavshamir.app.authorization.AuthTokensService;
import com.stavshamir.app.spotify.SpotifyClient;
import com.stavshamir.app.track.TrackData;
import com.stavshamir.app.track.TrackDataService;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PagingCursorbased;
import com.wrapper.spotify.model_objects.specification.PlayHistory;
import com.wrapper.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class ListeningHistoryServiceImpl implements ListeningHistoryService {

    private Logger logger = LoggerFactory.getLogger(ListeningHistoryServiceImpl.class);

    private static final int MAX_LIMIT = 50;

    private final SpotifyClient spotifyClient;
    private final AuthTokensService authTokensService;
    private final ListeningHistoryRepository listeningHistoryRepository;
    private final MostRecentlyPlayedAtRepository mostRecentlyPlayedAtRepository;
    private final TrackDataService trackDataService;

    private final Map<String, Object> usersLocks = new ConcurrentReferenceHashMap<>();

    @Autowired
    public ListeningHistoryServiceImpl(SpotifyClient spotifyClient, AuthTokensService authTokensService, ListeningHistoryRepository listeningHistoryRepository, MostRecentlyPlayedAtRepository mostRecentlyPlayedAtRepository, TrackDataService trackDataService) {
        this.spotifyClient = spotifyClient;
        this.authTokensService = authTokensService;
        this.listeningHistoryRepository = listeningHistoryRepository;
        this.mostRecentlyPlayedAtRepository = mostRecentlyPlayedAtRepository;
        this.trackDataService = trackDataService;
    }

    @Override
    public GetCurrentUsersRecentlyPlayedTracksRequest buildGetCurrentUsersRecentlyPlayedTracksRequest(String userId) {
        Timestamp mostRecentlyPlayedAt = mostRecentlyPlayedAtRepository
                .findByUserId(userId)
                .map(MostRecentlyPlayedAt::getPlayedAt)
                .orElse(new Timestamp(0));

        return spotifyClient
                .getSpotifyApiWithAccessToken(authTokensService.getAccessToken(userId))
                .getCurrentUsersRecentlyPlayedTracks()
                .after(mostRecentlyPlayedAt)
                .limit(MAX_LIMIT)
                .build();
    }

    @Override
    public void persistListeningHistory() throws IOException, SpotifyWebApiException {
        for (String userId : authTokensService.getAllUserIds()) {
            persistListeningHistoryForUser(userId);
        }
    }

    @Override
    public void persistListeningHistoryForUser(String userId) throws IOException, SpotifyWebApiException {
        logger.info("Pulling listening history from Spotify for " + userId);
        List<ListeningHistory> history;

        usersLocks.putIfAbsent(userId, new Object());
        synchronized(usersLocks.get(userId)) {
            history = getListeningHistoryFromSpotify(userId);
            updateMostRecentlyPlayedAt(userId, history);
        }

        listeningHistoryRepository.saveAll(history);
        logger.info(history.size() + " tracks persisted");
    }

    private List<ListeningHistory> getListeningHistoryFromSpotify(String userId) throws IOException, SpotifyWebApiException {
        PagingCursorbased<PlayHistory> tracks = buildGetCurrentUsersRecentlyPlayedTracksRequest(userId).execute();
        return Arrays.stream(tracks.getItems())
                .map(item -> fromPlayHistoryItem(userId, item))
                .collect(toList());
    }

    private void updateMostRecentlyPlayedAt(String userId, List<ListeningHistory> history) {
        if (!history.isEmpty()) {
            Timestamp mostRecentlyPlayedAt = history.get(0)
                    .getPlayedAt();

            persistMostRecentlyPlayedAt(userId, mostRecentlyPlayedAt);
        }
    }

    private static ListeningHistory fromPlayHistoryItem(String userId, PlayHistory item) {
        return new ListeningHistory(
                userId,
                item.getTrack().getUri(),
                new Timestamp(item.getPlayedAt().getTime())
        );
    }

    private void persistMostRecentlyPlayedAt(String userId, Date mostRecentlyPlayedAt) {
        final Timestamp playedAt = new Timestamp(mostRecentlyPlayedAt.getTime() + 1001);

        MostRecentlyPlayedAt time = mostRecentlyPlayedAtRepository
                .findByUserId(userId)
                .orElse(new MostRecentlyPlayedAt(userId, playedAt));

        time.setPlayedAt(playedAt);
        mostRecentlyPlayedAtRepository.save(time);
    }

    @Override
    public Page<TrackDataWithPlayedAt> getListeningHistory(String userUri, Timestamp after, Timestamp before, Pageable pageable)
            throws IOException, SpotifyWebApiException {
        Page<ListeningHistory> listeningHistoryPage = listeningHistoryRepository
                .findAllByUserIdAndPlayedAtGreaterThanEqualAndPlayedAtBeforeOrderByPlayedAtDesc(userUri, after, before, pageable);
        List<TrackDataWithPlayedAt> tracks = new ArrayList<>();

        for (ListeningHistory lh : listeningHistoryPage.getContent()) {
            TrackData trackData = trackDataService.getTrackData(lh.getUri(), userUri);
            tracks.add(new TrackDataWithPlayedAt(trackData, lh.getPlayedAt()));
        }

        return new PageImpl<>(tracks, pageable, listeningHistoryPage.getTotalElements());
    }

    @Override
    public Page<TrackDataWithPlayCount> getMostPlayed(GetMostPlayedQuery query, Pageable pageable) throws IOException, SpotifyWebApiException {
        List<TrackDataWithPlayCount> tracks = new ArrayList<>();

        Page<Object[]> page = listeningHistoryRepository.findMostPlayed(
                query.getUserUri(),
                query.getAfter(), query.getBefore(),
                query.getFromYear(), query.getToYear(),
                query.getFromMonth(), query.getToMonth(),
                query.getFromHour(), query.getToHour(),
                pageable
        );

        for (Object[] o : page) {
            String uri = (String)o[0];
            int count = ((BigInteger)o[1]).intValue();

            TrackData trackData = trackDataService.getTrackData(uri, query.getUserUri());
            tracks.add(new TrackDataWithPlayCount(trackData, count));
        }

        return new PageImpl<>(tracks, pageable, page.getTotalElements());
    }
}

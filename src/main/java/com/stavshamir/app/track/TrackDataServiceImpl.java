package com.stavshamir.app.track;

import com.stavshamir.app.authorization.AuthTokensService;
import com.stavshamir.app.spotify.SpotifyClient;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Service
public class TrackDataServiceImpl implements TrackDataService {

    private final SpotifyClient spotifyClient;
    private final TrackDataRepository trackDataRepository;
    private final AuthTokensService authTokensService;

    @Autowired
    public TrackDataServiceImpl(SpotifyClient spotifyClient, TrackDataRepository trackDataRepository, AuthTokensService authTokensService) {
        this.spotifyClient = spotifyClient;
        this.trackDataRepository = trackDataRepository;
        this.authTokensService = authTokensService;
    }

    @Override
    public TrackData getTrackData(String trackUri, String userUri) throws IOException, SpotifyWebApiException {
        Optional<TrackData > trackDataFromDB = trackDataRepository.findById(trackUri);
        if (trackDataFromDB.isPresent()) {
            return trackDataFromDB.get();
        }

        Track spotifyTrack = buildGetTrackRequest(trackUri, userUri).execute();
        TrackData trackData = trackDataFromSpotifyTrack(spotifyTrack);
        trackDataRepository.save(trackData);
        return trackData;
    }

    private GetTrackRequest buildGetTrackRequest(String trackUri, String userUri) {
        final int START_OF_TRACK_ID = 14;

        return spotifyClient
                .getSpotifyApiWithAccessToken(authTokensService.getAccessToken(userUri))
                .getTrack(trackUri.substring(START_OF_TRACK_ID))
                .build();
    }

    private static TrackData trackDataFromSpotifyTrack(Track spotifyTrack) {
        String[] artists = Arrays.stream(spotifyTrack.getArtists())
                .map(ArtistSimplified::getName)
                .toArray(String[]::new);

        return TrackData.builder()
                .withUri(spotifyTrack.getUri())
                .withName(spotifyTrack.getName())
                .withArtists(artists)
                .withAlbumName(spotifyTrack.getAlbum().getName())
                .withAlbumImageUrl(spotifyTrack.getAlbum().getImages()[1].getUrl())
                .build();
    }

}

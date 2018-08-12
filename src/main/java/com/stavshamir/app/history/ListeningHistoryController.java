package com.stavshamir.app.history;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/listening-history")
public class ListeningHistoryController {

    private final ListeningHistoryService listeningHistoryService;

    @Autowired
    public ListeningHistoryController(ListeningHistoryService listeningHistoryService) {
        this.listeningHistoryService = listeningHistoryService;
    }

    @RequestMapping("/persist")
    public String persist() {
        try {
            listeningHistoryService.persistListeningHistory();
        } catch (IOException | SpotifyWebApiException e) {
            return "Failed to retrieve listening history: " + e.getMessage();
        }

        return "Listening history persisted";
    }

    @RequestMapping("/get")
    public List<TrackDataWithPlayedAt> get(
            @CookieValue("spotify-user-uri") String userUri,
            @RequestParam(required = false, defaultValue = "0") long after
    ) throws IOException, SpotifyWebApiException {
        return listeningHistoryService.getListeningHistory(userUri, new Timestamp(after));
    }

}

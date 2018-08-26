package com.stavshamir.app.history;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
    public Page<TrackDataWithPlayedAt> get(
            Pageable pageable,
            @CookieValue("spotify-user-uri") String userUri,
            @RequestParam(required = false, defaultValue = "0") long after,
            @RequestParam(required = false, defaultValue = "4000000000000") long before,
            HttpServletResponse response
    ) throws IOException, SpotifyWebApiException {
        response.addHeader("Access-Control-Allow-Credentials","true");

        listeningHistoryService.persistListeningHistoryForUser(userUri);
        return listeningHistoryService.getListeningHistory(userUri, new Timestamp(after), new Timestamp(before), pageable);
    }

    @RequestMapping("/most-played")
    public List<TrackDataWithPlayCount> mostPlayed(
            @CookieValue("spotify-user-uri") String userUri,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "0") long after,
            @RequestParam(required = false, defaultValue = "4000000000000") long before,
            HttpServletResponse response
    ) throws IOException, SpotifyWebApiException {
        response.addHeader("Access-Control-Allow-Credentials","true");

        listeningHistoryService.persistListeningHistoryForUser(userUri);
        return listeningHistoryService.getMostPlayed(userUri, Math.min(size, 25), new Timestamp(after), new Timestamp(before));
    }

}

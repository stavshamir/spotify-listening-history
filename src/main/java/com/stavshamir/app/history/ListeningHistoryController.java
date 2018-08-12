package com.stavshamir.app.history;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/listening-history")
public class ListeningHistoryController {

    private final ListeningHistoryService listeningHistoryService;

    @Autowired
    public ListeningHistoryController(ListeningHistoryService listeningHistoryService) {
        this.listeningHistoryService = listeningHistoryService;
    }

    @RequestMapping("/persist")
    public String persistListeningHistory() {
        try {
            listeningHistoryService.persistListeningHistory();
        } catch (IOException | SpotifyWebApiException e) {
            return "Failed to retrieve listening history: " + e.getMessage();
        }

        return "Listening history persisted";
    }

}

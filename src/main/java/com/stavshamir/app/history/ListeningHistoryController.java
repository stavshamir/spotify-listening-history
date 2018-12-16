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
            @RequestHeader("spotify-user-uri") String userUri,
            @RequestParam(required = false, defaultValue = "0") long after,
            @RequestParam(required = false, defaultValue = "4000000000000") long before,
            @RequestParam(required = false, defaultValue = "false") boolean update,
            HttpServletResponse response
    ) throws IOException, SpotifyWebApiException {
        response.addHeader("Access-Control-Allow-Credentials","true");

        if (update) {
            listeningHistoryService.persistListeningHistoryForUser(userUri);
        }

        return listeningHistoryService.getListeningHistory(userUri, new Timestamp(after), new Timestamp(before), pageable);
    }

    @RequestMapping("/most-played")
    public Page<TrackDataWithPlayCount> mostPlayed(
            Pageable pageable,
            @CookieValue("spotify-user-uri") String userUri,
            @RequestParam(required = false) Long after,        @RequestParam(required = false) Long before,
            @RequestParam(required = false) Integer fromYear,  @RequestParam(required = false) Integer toYear,
            @RequestParam(required = false) Integer fromMonth, @RequestParam(required = false) Integer toMonth,
            @RequestParam(required = false) Integer fromHour,  @RequestParam(required = false) Integer toHour,
            @RequestParam(required = false, defaultValue = "false") boolean update,
            HttpServletResponse response
    ) throws IOException, SpotifyWebApiException {
        response.addHeader("Access-Control-Allow-Credentials","true");

        if (update) {
            listeningHistoryService.persistListeningHistoryForUser(userUri);
        }

        GetMostPlayedQuery query = GetMostPlayedQuery.builder(userUri)
                .after(after).before(before)
                .fromYear(fromYear).toYear(toYear)
                .fromMonth(fromMonth).toMonth(toMonth)
                .fromHour(fromHour).toHour(toHour)
                .build();

        return listeningHistoryService.getMostPlayed(query, pageable);
    }

}

package com.stavshamir.app.authorization;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AuthTokensController {

    private final AuthTokensService authTokensService;

    @Autowired
    public AuthTokensController(AuthTokensService authTokensService) {
        this.authTokensService = authTokensService;
    }

    @RequestMapping("/authorize")
    public RedirectView authorize() {
        String url = authTokensService
                .getAuthorizationCodeUriRequest("user-read-recently-played")
                .execute()
                .toString();

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return redirectView;
    }

    @RequestMapping("/callback")
    public String callback(HttpServletResponse response, @RequestParam("code") String code) {
        try {
            String userId = authTokensService.retrieveAndPersistTokens(code);
            response.addCookie(new Cookie("spotify-user-uri", userId));
        } catch (IOException | SpotifyWebApiException e) {
            return "Failed to retrieve authorization credentials from Spotify API: " + e.getMessage();
        }

        return "Authorization credentials stored successfully";
    }

}

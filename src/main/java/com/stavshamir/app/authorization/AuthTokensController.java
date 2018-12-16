package com.stavshamir.app.authorization;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/authorize")
public class AuthTokensController {

    private final AuthTokensService authTokensService;

    @Autowired
    public AuthTokensController(AuthTokensService authTokensService) {
        this.authTokensService = authTokensService;
    }

    @GetMapping
    public RedirectView authorize(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Credentials","true");
        
        String url = authTokensService
                .getAuthorizationCodeUriRequest("user-read-recently-played")
                .execute()
                .toString();

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return redirectView;
    }


    @PostMapping("/code")
    public String code(HttpServletResponse response, @RequestBody String code) throws IOException, SpotifyWebApiException {
        response.addHeader("Access-Control-Allow-Credentials","true");
        return authTokensService.retrieveAndPersistTokens(code);
    }
    
}

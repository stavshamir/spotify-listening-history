package com.stavshamir.app;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @RequestMapping("/health")
    public String health() {
        return "healthy";
    }

}

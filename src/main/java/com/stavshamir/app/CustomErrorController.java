package com.stavshamir.app;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleErrors(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Credentials","true");
        String errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE).toString();

        return errorMessage;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

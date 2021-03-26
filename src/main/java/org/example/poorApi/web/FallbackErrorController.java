package org.example.poorApi.web;

import org.example.poorApi.contract.ErrorResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class FallbackErrorController implements ErrorController {
    private static final String PATH = "/error";

    @RequestMapping(PATH)
    ErrorResponse error(HttpServletResponse response) {
        return ErrorResponse.UNKNOWN;
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}

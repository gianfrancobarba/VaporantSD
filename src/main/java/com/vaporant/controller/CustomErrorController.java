package com.vaporant.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    private static final String ERROR_STATUS_CODE = "error.status.code";
    private static final String ERROR_MESSAGE = "error.message";

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            request.setAttribute(ERROR_STATUS_CODE, statusCode);
            
            if(statusCode == 404) {
                request.setAttribute(ERROR_MESSAGE, "Pagina non trovata");
            }
            else if(statusCode == 500) {
                request.setAttribute(ERROR_MESSAGE, "Errore interno del server");
            }
            else {
                request.setAttribute(ERROR_MESSAGE, "Si è verificato un errore imprevisto");
            }
        }
        return "error";
    }
}

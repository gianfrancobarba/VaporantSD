package com.vaporant.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            request.setAttribute("errorCode", statusCode);
            
            if(statusCode == 404) {
                request.setAttribute("errorMessage", "Pagina non trovata");
            }
            else if(statusCode == 500) {
                request.setAttribute("errorMessage", "Errore interno del server");
            }
            else {
                request.setAttribute("errorMessage", "Si è verificato un errore imprevisto");
            }
        }
        return "error";
    }
}

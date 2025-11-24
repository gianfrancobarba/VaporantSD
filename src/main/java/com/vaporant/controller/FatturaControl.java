package com.vaporant.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FatturaControl {

    @RequestMapping(value = "/fattura", method = RequestMethod.GET)
    public String showFattura(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("order") == null || session.getAttribute("user") == null) {
            return "redirect:loginForm.jsp";
        }
        return "fattura"; // Resolves to fattura.jsp
    }
}

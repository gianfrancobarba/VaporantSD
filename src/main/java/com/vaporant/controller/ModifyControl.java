package com.vaporant.controller;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.vaporant.model.UserBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaporant.repository.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ModifyControl {

    private static final Logger logger = LoggerFactory.getLogger(ModifyControl.class);
    private static final String contentType = "application/json";

    @Autowired
    private UserDAO need;

    @RequestMapping(value = "/modify", method = { RequestMethod.GET, RequestMethod.POST })

    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String action = request.getParameter("action");
        UserBean user = (UserBean) request.getSession().getAttribute("user");

        if (action == null || user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action) {
            case "modificaEmail":
                String nuovaMail = request.getParameter("nuovaEmail");
                try {
                    need.modifyMail(user, nuovaMail);
                    user = need.findById(user.getId());
                    /* Sincronizzazione sessione dopo modifica email */
                    request.getSession().setAttribute("user", user);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType(contentType);
                    PrintWriter out = response.getWriter();
                    out.print("{ \"email\": \"" + user.getEmail() + "\" }");
                    out.flush();
                } catch (SQLException e) {
                    logger.error("Error modifying email: {}", e.getMessage(), e);
                }
                break;
            case "modificaTelefono":
                String nuovoTelefono = request.getParameter("nuovoTelefono");
                try {
                    need.modifyTelefono(user, nuovoTelefono);
                    user = need.findById(user.getId());
                    /* Sincronizzazione sessione dopo modifica telefono */
                    request.getSession().setAttribute("user", user);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType(contentType);
                    PrintWriter out = response.getWriter();
                    out.print("{ \"numTelefono\": \"" + user.getNumTelefono() + "\" }");
                    out.flush();
                } catch (SQLException e) {
                    logger.error("Error modifying phone: {}", e.getMessage(), e);
                }
                break;
            case "modificaPassword":
                String nuovaPsw = request.getParameter("nuovaPassword");
                String vecchiaPsw = request.getParameter("vecchiaPassword");

                try {
                    boolean success = true;
                    if (need.modifyPsw(nuovaPsw, vecchiaPsw, user) == 0) {
                        success = false;
                    } else {
                        /* Sincronizzazione sessione dopo modifica password */
                        user = need.findById(user.getId());
                        request.getSession().setAttribute("user", user);
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                    String jsonResponse = "{\"success\": " + success + "}";

                    // Impostazione dei corretti header della risposta JSON
                    response.setContentType(contentType);
                    response.setCharacterEncoding("UTF-8");

                    // Scrittura del JSON come risposta
                    try (PrintWriter out = response.getWriter()) {
                        out.print(jsonResponse);
                    }
                } catch (SQLException e) {
                    logger.error("Error modifying password: {}", e.getMessage(), e);
                }
                break;
            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
    }
}

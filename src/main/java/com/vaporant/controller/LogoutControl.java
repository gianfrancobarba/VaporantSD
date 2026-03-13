package com.vaporant.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutControl {

	@GetMapping("/logout")
	public String execute(HttpServletRequest req, HttpServletResponse resp) {

		req.getSession().invalidate();
		HttpSession currentSession = req.getSession();
		currentSession.setAttribute("user", null);
		currentSession.setAttribute("tipo", null);
		currentSession.setAttribute("cart", null);

		return "redirect:ProductView.jsp";

	}

}

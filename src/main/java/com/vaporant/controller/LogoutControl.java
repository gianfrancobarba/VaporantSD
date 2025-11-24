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
public class LogoutControl {



	@RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
	public String execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		
			req.getSession().invalidate();
			HttpSession currentSession = req.getSession();
			currentSession.setAttribute("user", null);
			currentSession.setAttribute("tipo", null);
			currentSession.setAttribute("cart", null);
		
			return "redirect:ProductView.jsp";

	}




}

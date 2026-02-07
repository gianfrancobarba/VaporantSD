package com.vaporant.controller;

import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.vaporant.model.Cart;
import com.vaporant.model.UserBean;
import com.vaporant.repository.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginControl {

	private static final Logger logger = LoggerFactory.getLogger(LoginControl.class);
	private final UserDAO userDao;

	@Autowired
	public LoginControl(UserDAO userDao) {
		this.userDao = userDao;
	}

	@RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST })
	public String login(HttpServletRequest req, HttpServletResponse resp) {

		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String action = (String) req.getSession().getAttribute("action");
		Cart cart = (Cart) req.getSession().getAttribute("cart");

		UserBean user = null;

		try {
			user = userDao.findByCred(email, password);

		} catch (SQLException e) {
			logger.error("Database error during login: {}", e.getMessage(), e);
		}

		if (user != null) {

			HttpSession session = req.getSession(false);
			if (session != null) {
				session.invalidate();
			}

			HttpSession currentSession = req.getSession();
			currentSession.setAttribute("user", user);
			currentSession.setAttribute("tipo", user.getTipo());
			currentSession.setAttribute("cart", cart);
			/*
			 * Migrazione dell'attributo action per preservare il flusso
			 */
			currentSession.setAttribute("action", action);

			if (action != null && action.equalsIgnoreCase("checkout"))
				return "redirect:checkout.jsp";
			else if (user.getTipo().equalsIgnoreCase("admin"))
				return "redirect:ProductViewAdmin.jsp";
			else
				return "redirect:ProductView.jsp";

		} else {
			return "redirect:loginForm.jsp";
		}
	}
}

package com.vaporant.controller;

import java.sql.SQLException;
import java.time.LocalDate;

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
public class SignControl {

	private static final Logger logger = LoggerFactory.getLogger(SignControl.class);
	private UserDAO userDao;

	@Autowired
	public SignControl(UserDAO userDao) {
		this.userDao = userDao;
	}

	@RequestMapping(value = "/SignControl", method = { RequestMethod.GET, RequestMethod.POST })
	public String execute(HttpServletRequest request, HttpServletResponse response) {

		UserBean user = new UserBean();

		user.setNome(request.getParameter("nome"));
		user.setCognome(request.getParameter("cognome"));
		user.setDataNascita(LocalDate.parse(request.getParameter("data_nascita")));
		user.setCodF(request.getParameter("codice_fiscale"));
		user.setNumTelefono(request.getParameter("telefono"));

		// Email validation BEFORE setting to bean
		String email = request.getParameter("email");
		if (!isValidEmail(email)) {
			return "redirect:SignForm.jsp"; // Email invalida, ritorna a form senza chiamare DAO
		}

		user.setEmail(email);
		user.setPassword(request.getParameter("password"));

		int result = 0;

		try {
			result = userDao.saveUser(user);
		} catch (SQLException e) {
			logger.error("Error saving user: {}", e.getMessage(), e);
		}

		if (result > 0)
			return "redirect:loginForm.jsp";
		else
			return "redirect:SignForm.jsp";
	}

	/**
	 * Validates email format using regex pattern
	 * 
	 * @param email Email address to validate
	 * @return true if email is valid, false otherwise
	 */
	private boolean isValidEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		// Regex email validation
		// Pattern: local-part@domain.tld
		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
		return email.matches(emailRegex);
	}

}

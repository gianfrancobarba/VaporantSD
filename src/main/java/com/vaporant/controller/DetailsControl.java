package com.vaporant.controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaporant.repository.ProductModel;

@Controller
public class DetailsControl {

	private static final Logger logger = LoggerFactory.getLogger(DetailsControl.class);
	private final ProductModel model;

	@Autowired
	public DetailsControl(ProductModel model) {
		this.model = model;
	}

	@RequestMapping(value = "/details", method = { RequestMethod.GET, RequestMethod.POST })
	public String execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");

		try {
			if (action != null) {
				if (action.equalsIgnoreCase("read")) {
					int id = Integer.parseInt(request.getParameter("id"));
					request.removeAttribute("product");
					request.getSession().setAttribute("product", model.doRetrieveByKey(id));
				}
			}
		} catch (NumberFormatException e) {
			logger.error("Invalid ID format: {}", e.getMessage());
		} catch (SQLException e) {
			logger.error("Database error in details: {}", e.getMessage(), e);
		}

		return "redirect:DetailsView.jsp";

	}

}

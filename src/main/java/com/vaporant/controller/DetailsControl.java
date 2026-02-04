package com.vaporant.controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaporant.repository.ProductModel;

@Controller
public class DetailsControl {

	@Autowired
	private ProductModel model;

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
			System.out.println("error: Invalid ID format - " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("error:" + e.getMessage());
		}

		return "redirect:DetailsView.jsp";

	}

}

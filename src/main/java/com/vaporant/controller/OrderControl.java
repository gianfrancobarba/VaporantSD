package com.vaporant.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.vaporant.model.Cart;
import com.vaporant.model.UserBean;
import com.vaporant.model.OrderBean;
import com.vaporant.model.ProductBean;
import com.vaporant.model.ContenutoBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaporant.repository.ContenutoDAO;
import com.vaporant.repository.OrderDAO;

@Controller
public class OrderControl {

	private static final Logger logger = LoggerFactory.getLogger(OrderControl.class);

	private final OrderDAO orderDao;
	private final ContenutoDAO contDao;
	private final com.vaporant.repository.ProductModel productDao;

	@Autowired
	public OrderControl(OrderDAO orderDao, ContenutoDAO contDao, com.vaporant.repository.ProductModel productDao) {
		this.orderDao = orderDao;
		this.contDao = contDao;
		this.productDao = productDao;
	}

	@PostMapping("/Ordine")
	public String execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		HttpSession session = req.getSession();

		Cart cart = (Cart) session.getAttribute("cart");
		UserBean user = (UserBean) session.getAttribute("user");

		if (cart == null || user == null) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing session data");
			return null;
		}

		int idUtente = user.getId();
		logger.info("Creating order for user ID: {}", user.getId());

		String payment = req.getParameter("payment");
		String addressParam = req.getParameter("addressDropdown");
		if (payment == null || addressParam == null) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return null;
		}

		int idIndirizzo;
		try {
			idIndirizzo = Integer.parseInt(addressParam);
		} catch (NumberFormatException e) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid address ID");
			return null;
		}

		OrderBean order = new OrderBean(idUtente, idIndirizzo, cart.getPrezzoTotale(), LocalDate.now(), payment);

		try {
			orderDao.saveOrder(order);
		} catch (SQLException e) {
			logger.error("Error saving order: {}", e.getMessage(), e);
		}

		int idOrdine = -1;

		try {
			idOrdine = orderDao.getIdfromDB();
		} catch (SQLException e) {
			logger.error("Error retrieving order ID: {}", e.getMessage(), e);
		}

		int i = 0;
		for (ProductBean prod : cart.getProducts()) {
			try {
				contDao.saveContenuto(
						new ContenutoBean(idOrdine, prod.getCode(), prod.getQuantity(), 22, prod.getPrice()));
				if (logger.isDebugEnabled()) {
					logger.debug("Added product {} to order: {}", i, prod.getName());
				}
				i++;

				// Decrement stock in storage
				int newQuantity = prod.getQuantityStorage() - prod.getQuantity();
				productDao.updateQuantityStorage(prod, newQuantity);

				if (logger.isDebugEnabled()) {
					logger.debug("Saved content for product {} - {}", i, prod);
				}
				i++;
			} catch (SQLException e) {
				logger.error("Error saving order content: {}", e.getMessage(), e);
			}
		}

		session.setAttribute("order", order);
		session.setAttribute("user", user);
		session.setAttribute("listaProd", cart.getProducts());

		return "redirect:ordine.jsp";

	}

};

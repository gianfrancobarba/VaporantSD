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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaporant.repository.ContenutoDAO;
import com.vaporant.repository.OrderDAO;

@Controller
public class OrderControl {

	private static final Logger logger = LoggerFactory.getLogger(OrderControl.class);

	@Autowired
	private OrderDAO orderDao;
	@Autowired
	private ContenutoDAO contDao;
	@Autowired
	private com.vaporant.repository.ProductModel productDao;

	@RequestMapping(value = "/Ordine", method = { RequestMethod.GET, RequestMethod.POST })
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
			e.printStackTrace();
		}

		int idOrdine = -1;

		try {
			idOrdine = orderDao.getIdfromDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		int i = 0;
		for (ProductBean prod : cart.getProducts()) {
			try {
				contDao.saveContenuto(
						new ContenutoBean(idOrdine, prod.getCode(), prod.getQuantity(), 22, prod.getPrice()));
				logger.debug("Added product {} to order: {}", i++, prod.getName());
				productDao.updateQuantityStorage(prod, prod.getQuantityStorage() - prod.getQuantity());

				// Decrement stock in storage
				int newQuantity = prod.getQuantityStorage() - prod.getQuantity();
				productDao.updateQuantityStorage(prod, newQuantity);

				logger.debug("Saved content for product {} - {}", i++, prod.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		session.setAttribute("order", order);
		session.setAttribute("user", user);
		session.setAttribute("listaProd", cart.getProducts());

		return "redirect:ordine.jsp";

	}

};

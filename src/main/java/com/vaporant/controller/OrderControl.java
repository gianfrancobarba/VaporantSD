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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaporant.repository.ContenutoDAO;
import com.vaporant.repository.OrderDAO;

@Controller
public class OrderControl {

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

		int idUtente = user.getId();
		System.out.println("order " + user.getId());

		String payment = req.getParameter("payment");
		int idIndirizzo = Integer.parseInt(req.getParameter("addressDropdown"));

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

				// Decrement stock in storage
				int newQuantity = prod.getQuantityStorage() - prod.getQuantity();
				productDao.updateQuantityStorage(prod, newQuantity);

				System.out.println("prodotto " + i++ + prod.toString());
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

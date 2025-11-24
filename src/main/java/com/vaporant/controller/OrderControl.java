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

import com.vaporant.repository.AddressDAO;
import com.vaporant.repository.ContenutoDAO;
import com.vaporant.repository.OrderDAO;
import com.vaporant.repository.ProductModel;
import com.vaporant.repository.UserDAO;

@Controller
public class OrderControl {

	@Autowired
	private OrderDAO orderDao;
	@Autowired
	private ContenutoDAO contDao;
	@Autowired
	private UserDAO userDao;
	@Autowired
	private AddressDAO addressDao;
	@Autowired
	private ProductModel productDao;


	@RequestMapping(value = "/Ordine", method = {RequestMethod.GET, RequestMethod.POST})
	public String execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

	
		HttpSession session = req.getSession();
		
		Cart cart = (Cart) session.getAttribute("cart");
		UserBean user = (UserBean) session.getAttribute("user");
		
		int idUtente = user.getId();
		System.out.println("order " + user.getId());
		
		String payment = req.getParameter("payment");
		int idIndirizzo = Integer.parseInt(req.getParameter("addressDropdown"));
		int idIndirizzoFatt = Integer.parseInt(req.getParameter("addressDropdown2"));
		
		
		
		String indirizzoFatt = null;
		try {
			indirizzoFatt = addressDao.findAddressByID(idIndirizzoFatt).toStringScript();
		} catch (SQLException e2) {

			e2.printStackTrace();
		}
		
		try {
			userDao.updateAddress(indirizzoFatt, user);
		} catch (SQLException e1) {

			e1.printStackTrace();
		}
		
		OrderBean order = new OrderBean(idUtente,idIndirizzo, cart.getPrezzoTotale(), LocalDate.now(), payment);
		
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
		for(ProductBean prod : cart.getProducts())
		{
			try {
				contDao.saveContenuto(new ContenutoBean(idOrdine,prod.getCode(),prod.getQuantity(),22,prod.getPrice()));
				System.out.println("prodotto " +  i++ + prod.toString());
				productDao.updateQuantityStorage(prod, prod.getQuantityStorage() - prod.getQuantity());
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		session.setAttribute("order", order);
		session.setAttribute("user", user);
		session.setAttribute("listaProd", cart.getProducts());
		
		return "redirect:ordine.jsp";

		
	}



}
;

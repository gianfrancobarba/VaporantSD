package com.vaporant.controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.vaporant.model.Cart;
import com.vaporant.model.ProductBean;
import com.vaporant.model.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaporant.repository.ProductModel;

@Controller
public class CartControl {

	private static final Logger logger = LoggerFactory.getLogger(CartControl.class);
	private final ProductModel model;

	@Autowired
	public CartControl(ProductModel model) {
		this.model = model;
	}

	@RequestMapping(value = "/cart", method = {RequestMethod.GET, RequestMethod.POST})
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		Cart cart = (Cart)request.getSession().getAttribute("cart");
		if(cart == null) 
		{
			cart = new Cart();
			request.getSession().setAttribute("cart", cart);
		}
		
		

		String action = request.getParameter("action");
		UserBean user = (UserBean) request.getSession().getAttribute("user");
		Boolean checkout = false;
		
		try {
			if (action != null) {
				if (action.equalsIgnoreCase("addC")) 
				{
					int id = Integer.parseInt(request.getParameter("id"));
					ProductBean prod = model.doRetrieveByKey(id);
					cart.addProduct(prod);
										
				} else if (action.equalsIgnoreCase("deleteC")) 
					{
						int id = Integer.parseInt(request.getParameter("id"));
						cart.deleteProduct(model.doRetrieveByKey(id));
					}
					else if(action.equalsIgnoreCase("aggiorna"))
						{
							int id = Integer.parseInt(request.getParameter("id"));
							int quantita = Integer.parseInt(request.getParameter("quantita"));
							cart.aggiorna(model.doRetrieveByKey(id),quantita);
						}
						else if(action.equalsIgnoreCase("aggiornaCheck"))
							{
								int id = Integer.parseInt(request.getParameter("id"));
								int quantita = Integer.parseInt(request.getParameter("quantita"));
								cart.aggiorna(model.doRetrieveByKey(id),quantita);
								checkout = true;
								
							}
			}
		} catch (SQLException e) {
			logger.error("Error in cart operation: {}", e.getMessage(), e);
		}
		

		request.getSession().setAttribute("user", user);
		request.getSession().setAttribute("cart", cart);
		
		if(checkout) 
		{
			return "redirect:checkout.jsp";
			
		}
		else
			if(action != null && action.equalsIgnoreCase("checkout"))
				return "redirect:checkout.jsp";
			else
				return "redirect:CartView.jsp";
		}

	 
	


}

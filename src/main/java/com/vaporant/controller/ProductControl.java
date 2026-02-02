package com.vaporant.controller;

import java.io.IOException; 
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.vaporant.repository.ProductModel;
import com.vaporant.model.ProductBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class ProductControl {

	private static final Logger logger = LoggerFactory.getLogger(ProductControl.class);
	private final ProductModel model;

	@Autowired
	public ProductControl(ProductModel model) {
		this.model = model;
	}

	
	@RequestMapping(value = "/product", method = {RequestMethod.GET, RequestMethod.POST})
	public String execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        String action = request.getParameter("action");

        try {
            if (action != null) {

                if (action.equalsIgnoreCase("delete")) 
                {

                    int id = Integer.parseInt(request.getParameter("id"));
                    model.doDelete(id);
                }
                else if(action.equalsIgnoreCase("insert"))
                {
                    String name = request.getParameter("name");
                    String description = request.getParameter("description");
                    int price = Integer.parseInt(request.getParameter("price"));
                    int quantity = Integer.parseInt(request.getParameter("quantity"));

                    ProductBean bean = new ProductBean();
                    bean.setName(name);
                    bean.setDescription(description);
                    bean.setPrice(price);
                    bean.setQuantityStorage(quantity);
                    model.doSave(bean);
                }
            }

        } catch (SQLException e) {
                logger.error("Error in product operation: {}", e.getMessage(), e);
          }

        String sort = request.getParameter("sort");

        try {
        	
            request.getSession().removeAttribute("products");
            request.getSession().setAttribute("products", model.doRetrieveAll(sort));
        
        } catch (SQLException e) {
            logger.error("Error retrieving products: {}", e.getMessage(), e);
        }
        
        if(request.getSession().getAttribute("tipo").equals("admin")) 
        	return "redirect:ProductViewAdmin.jsp";
        else
        	return "redirect:ProductView.jsp";
	}

	


}

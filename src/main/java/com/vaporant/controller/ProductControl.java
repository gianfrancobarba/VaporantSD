package com.vaporant.controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.vaporant.repository.ProductModel;
import com.vaporant.model.ProductBean;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class ProductControl {

    @Autowired
    private ProductModel model;

    @RequestMapping(value = "/product", method = { RequestMethod.GET, RequestMethod.POST })
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action != null) {

                if (action.equalsIgnoreCase("delete")) {
                    String idParam = request.getParameter("id");
                    if (idParam != null) {
                        try {
                            int id = Integer.parseInt(idParam);
                            model.doDelete(id);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid ID format: " + idParam);
                        }
                    }
                } else if (action.equalsIgnoreCase("insert")) {
                    String name = request.getParameter("name");
                    String description = request.getParameter("description");
                    String priceParam = request.getParameter("price");
                    String quantityParam = request.getParameter("quantity");

                    if (name != null && priceParam != null && quantityParam != null) {
                        try {
                            int price = Integer.parseInt(priceParam);
                            int quantity = Integer.parseInt(quantityParam);

                            ProductBean bean = new ProductBean();
                            bean.setName(name);
                            bean.setDescription(description);
                            bean.setPrice(price);
                            bean.setQuantityStorage(quantity);
                            model.doSave(bean);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format in product insert");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }

        String sort = request.getParameter("sort");

        try {

            request.getSession().removeAttribute("products");
            request.getSession().setAttribute("products", model.doRetrieveAll(sort));

        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }

        String tipo = (String) request.getSession().getAttribute("tipo");
        if (tipo != null && tipo.equals("admin"))
            return "redirect:ProductViewAdmin.jsp";
        else
            return "redirect:ProductView.jsp";
    }

}

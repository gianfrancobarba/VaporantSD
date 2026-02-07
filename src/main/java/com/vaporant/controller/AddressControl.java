package com.vaporant.controller;

import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.vaporant.model.UserBean;

import com.vaporant.model.AddressBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaporant.repository.AddressDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AddressControl {

	private static final Logger logger = LoggerFactory.getLogger(AddressControl.class);
	private final AddressDAO addressDao;

	@Autowired
	public AddressControl(AddressDAO addressDao) {
		this.addressDao = addressDao;
	}

	@RequestMapping(value = "/AddressControl", method = { RequestMethod.GET, RequestMethod.POST })
	public String execute(HttpServletRequest request, HttpServletResponse response) {

		AddressBean address = new AddressBean();

		UserBean user = (UserBean) request.getSession().getAttribute("user");

		if (user != null) {
			String citta = request.getParameter("citta");
			String prov = request.getParameter("provincia");
			String via = request.getParameter("via");
			address.setCap(request.getParameter("cap"));
			address.setCitta(citta);
			address.setId_utente(user.getId());
			address.setNumCivico(request.getParameter("numero_civico"));
			address.setProvincia(prov);
			address.setStato(request.getParameter("stato"));
			address.setVia(via);
			try {
				addressDao.saveAddress(address);
				return "redirect:Utente.jsp";

			} catch (SQLException e) {
				logger.error("Error saving address: {}", e.getMessage(), e);
			}
		}

		return "redirect:Utente.jsp"; // Default redirect if user is null or after processing
	}

}

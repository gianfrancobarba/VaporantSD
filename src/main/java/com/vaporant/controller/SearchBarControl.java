package com.vaporant.controller;


import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class SearchBarControl {

	private static final String TABLE_NAME = "prodotto";
	
	@Autowired
	private DataSource ds;


	@RequestMapping(value = "/SearchBar", method = {RequestMethod.GET, RequestMethod.POST})
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String nome = req.getParameter("nome");

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE nome LIKE ?";
        try (Connection connection = ds.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + nome + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            // Lista da far passare in json
            List<Map<String, Object>> results = new ArrayList<>();
            // Per prendere nomi delle colonne e oggetti delle colonne per ogni rigo della tabella
            ResultSetMetaData metaData = resultSet.getMetaData();
            int colonne = metaData.getColumnCount();

            // Per riempire la Lista
            while (resultSet.next()) {
                // Un oggetto Map per ogni valore delle colonne
                Map<String, Object> oggetto = new HashMap<>();
                for (int i = 1; i <= colonne; i++) {
                    String nomeColonna = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    oggetto.put(nomeColonna,value);
                }
                results.add(oggetto);
            }

            String lista = new Gson().toJson(results);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(lista);

        } catch (SQLException e) {
        	System.out.println("Error:" + e.getMessage());
        }
    }


}

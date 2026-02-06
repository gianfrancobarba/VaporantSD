package com.vaporant.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SearchBarControl.class)
class SearchBarControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSource dataSource;

    @MockBean
    private Connection connection;

    @MockBean
    private PreparedStatement preparedStatement;

    @MockBean
    private ResultSet resultSet;

    @MockBean
    private ResultSetMetaData metaData;

    @Test
    @DisplayName("SearchBar - Ricerca prodotto con successo - Restituisce JSON con risultati")
    void testSearchSuccess() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(7);
        when(metaData.getColumnName(1)).thenReturn("ID");
        when(metaData.getColumnName(2)).thenReturn("nome");
        when(metaData.getColumnName(3)).thenReturn("descrizione");
        when(metaData.getColumnName(4)).thenReturn("quantita");
        when(metaData.getColumnName(5)).thenReturn("prezzoAttuale");
        when(metaData.getColumnName(6)).thenReturn("tipo");
        when(metaData.getColumnName(7)).thenReturn("colore");

        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getObject(1)).thenReturn(1);
        when(resultSet.getObject(2)).thenReturn("Product 1");
        when(resultSet.getObject(3)).thenReturn("Description");
        when(resultSet.getObject(4)).thenReturn(10);
        when(resultSet.getObject(5)).thenReturn(19.99);
        when(resultSet.getObject(6)).thenReturn("Svapo");
        when(resultSet.getObject(7)).thenReturn("Argento");

        mockMvc.perform(get("/SearchBar")
                .param("nome", "Product"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{\"ID\":1,\"nome\":\"Product 1\",\"descrizione\":\"Description\",\"quantita\":10,\"prezzoAttuale\":19.99,\"tipo\":\"Svapo\",\"colore\":\"Argento\"}]"));
    }

    @Test
    @DisplayName("SearchBar - SQLException durante ricerca - Gestione errore gracefully")
    void testSearchException() throws Exception {
        when(dataSource.getConnection()).thenThrow(new SQLException("DB Error"));

        mockMvc.perform(get("/SearchBar")
                .param("nome", "Product"))
                .andExpect(status().isOk()); // Catches exception and does nothing
    }
}

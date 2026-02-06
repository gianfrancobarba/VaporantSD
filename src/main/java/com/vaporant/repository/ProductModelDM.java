package com.vaporant.repository;

import com.vaporant.model.*;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.Collection;
import java.util.LinkedList;

@Repository
public class ProductModelDM implements ProductModel {

	private static final String TABLE_NAME = "prodotto";

	private DataSource ds;

	private Connection getConnection() throws SQLException {
		if (ds != null) {
			return ds.getConnection();
		}
		DataSource staticDs = com.vaporant.util.DataSourceUtil.getDataSource();
		if (staticDs != null) {
			return staticDs.getConnection();
		}
		throw new SQLException("DataSource is null");
	}

	@Override
	public synchronized void doSave(ProductBean product) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertSQL = "INSERT INTO " + ProductModelDM.TABLE_NAME
				+ " (nome, descrizione, quantita, prezzoAttuale, tipo, colore) VALUES (?, ?, ?, ?, ?, ?)";

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(insertSQL);
			preparedStatement.setString(1, product.getName());
			preparedStatement.setString(2, product.getDescription());
			preparedStatement.setInt(3, product.getQuantityStorage());
			preparedStatement.setFloat(4, product.getPrice());
			preparedStatement.setString(5, product.getTipo());
			preparedStatement.setString(6, product.getColore());
			preparedStatement.executeUpdate();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}
	}

	@Override
	public synchronized ProductBean doRetrieveByKey(int id) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		ProductBean bean = new ProductBean();

		String selectSQL = "SELECT * FROM " + ProductModelDM.TABLE_NAME + " WHERE ID = ?";

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, id);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				bean.setCode(rs.getInt("ID"));
				bean.setName(rs.getString("nome"));
				bean.setDescription(rs.getString("descrizione"));
				bean.setPrice(rs.getFloat("prezzoAttuale"));
				bean.setQuantityStorage(rs.getInt("quantita"));
				bean.setTipo(rs.getString("tipo"));
				bean.setColore(rs.getString("colore"));
			}

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		return bean;
	}

	@Override
	public synchronized boolean doDelete(int id) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;

		String deleteSQL = "DELETE FROM " + ProductModelDM.TABLE_NAME + " WHERE ID = ?";

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setInt(1, id);

			result = preparedStatement.executeUpdate();

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		return (result != 0);
	}

	@Override
	public synchronized Collection<ProductBean> doRetrieveAll(String order) throws SQLException {

		Collection<ProductBean> products = new LinkedList<>();

		String selectSQL = "SELECT * FROM " + ProductModelDM.TABLE_NAME;

		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order;
		}

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				ProductBean bean = new ProductBean();

				bean.setCode(rs.getInt("ID"));
				bean.setName(rs.getString("nome"));
				bean.setDescription(rs.getString("descrizione"));
				bean.setPrice(rs.getFloat("prezzoAttuale"));
				bean.setQuantityStorage(rs.getInt("quantita"));
				bean.setTipo(rs.getString("tipo"));
				bean.setColore(rs.getString("colore"));

				products.add(bean);
			}

		}
		return products;
	}

	@Override
	public void updateQuantityStorage(ProductBean prod, int quantita) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String updateSQL = "UPDATE " + ProductModelDM.TABLE_NAME + " SET quantita = ? WHERE ID = ?";

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(updateSQL);
			preparedStatement.setInt(1, quantita);
			preparedStatement.setInt(2, prod.getCode());
			preparedStatement.executeUpdate();

			// connection.commit();

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}
	}

}

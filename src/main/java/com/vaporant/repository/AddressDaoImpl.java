package com.vaporant.repository;

import com.vaporant.model.*;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class AddressDaoImpl implements AddressDAO {
	
	private static final String TABLE = "Indirizzo";
	
	@Autowired
	private DataSource ds;
	
	@Override
	public int saveAddress(AddressBean address) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		int result = 0;
		
		String insertSQL = "INSERT INTO " + TABLE + 
			" (Via, numCivico, citta, CAP, provincia) VALUES (?, ?, ?, ?, ?)";
			
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertSQL);
			
			preparedStatement.setString(1, address.getVia());
			preparedStatement.setString(2, address.getNumCivico());
			preparedStatement.setString(3, address.getCitta());
			preparedStatement.setString(4, address.getCap());
			preparedStatement.setString(5, address.getProvincia());
			
			result = preparedStatement.executeUpdate();
			
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if(connection != null) {
					connection.close();
				}
			}
		}
		
		return result;
	}
	
	@Override
	public int deleteAddress(AddressBean address) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		int result = 0;
		
		String deleteSQL = "DELETE FROM " + TABLE + " WHERE ID = ?";
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setInt(1, address.getId());
			result = preparedStatement.executeUpdate();
			
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if(connection != null) {
					connection.close();
				}
			}
		}
		
		return result;
	}
	
	@Override
	public AddressBean findByCred(String cap, String via, String numCivico) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		String selectSQL = "SELECT * FROM " + TABLE + " WHERE CAP = ? AND Via = ? AND numCivico = ?";
		AddressBean address = null;
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			
			preparedStatement.setString(1, cap);
			preparedStatement.setString(2, via);
			preparedStatement.setString(3, numCivico);
			
			ResultSet rs = preparedStatement.executeQuery();
			if(!rs.isBeforeFirst()) return null;
			
			address = new AddressBean();
			
			while (rs.next()) {
				address.setId(rs.getInt("ID"));
				address.setVia(rs.getString("Via"));
				address.setNumCivico(rs.getString("numCivico"));
				address.setCitta(rs.getString("citta"));
				address.setCap(rs.getString("CAP"));
				address.setProvincia(rs.getString("provincia"));
			}
			
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if(connection != null) {
					connection.close();
				}
			}
		}
		
		return address;
	}
	
	@Override
	public ArrayList<AddressBean> findByID(int id) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ArrayList<AddressBean> addresses = new ArrayList<AddressBean>();
		
		String selectSQL = "SELECT * FROM " + TABLE + " WHERE ID = ?";
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, id);
			
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next()) {
				AddressBean address = new AddressBean();
				address.setId(rs.getInt("ID"));
				address.setVia(rs.getString("Via"));
				address.setNumCivico(rs.getString("numCivico"));
				address.setCitta(rs.getString("citta"));
				address.setCap(rs.getString("CAP"));
				address.setProvincia(rs.getString("provincia"));
				addresses.add(address);
			}
			
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if(connection != null) {
					connection.close();
				}
			}
		}
		
		return addresses;
	}
	
	@Override
	public AddressBean findAddressByID(int id) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		AddressBean address = null;
		
		String selectSQL = "SELECT * FROM " + TABLE + " WHERE ID = ?";
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, id);
			
			ResultSet rs = preparedStatement.executeQuery();
			if(!rs.isBeforeFirst()) return null;
			
			address = new AddressBean();
			
			while (rs.next()) {
				address.setId(rs.getInt("ID"));
				address.setVia(rs.getString("Via"));
				address.setNumCivico(rs.getString("numCivico"));
				address.setCitta(rs.getString("citta"));
				address.setCap(rs.getString("CAP"));
				address.setProvincia(rs.getString("provincia"));
			}
			
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if(connection != null) {
					connection.close();
				}
			}
		}
		
		return address;
	}
}

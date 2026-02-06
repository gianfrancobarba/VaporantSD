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

	private static final String TABLE = "indirizzo";

	// SQL fragments
	private static final String WHERE_ID = " WHERE ID = ?";
	private static final String SELECT_FROM = "SELECT * FROM ";

	// Column names
	private static final String COL_NUM_CIVICO = "numCivico";
	private static final String COL_CITTA = "citta";
	private static final String COL_PROVINCIA = "provincia";
	private static final String COL_ID_UTENTE = "ID_Utente";
	private static final String COL_STATO = "stato";

	private DataSource ds;

	@Autowired
	public AddressDaoImpl(DataSource ds) {
		this.ds = ds;
	}

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
	public int saveAddress(AddressBean address) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		int result = 0;

		String insertSQL = "INSERT INTO " + TABLE +
				" (ID_Utente, stato, via, numCivico, citta, CAP, provincia) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(insertSQL);

			preparedStatement.setInt(1, address.getId_utente());
			preparedStatement.setString(2, address.getStato());
			preparedStatement.setString(3, address.getVia());
			preparedStatement.setString(4, address.getNumCivico());
			preparedStatement.setString(5, address.getCitta());
			preparedStatement.setString(6, address.getCap());
			preparedStatement.setString(7, address.getProvincia());

			result = preparedStatement.executeUpdate();

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null) {
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

		String deleteSQL = "DELETE FROM " + TABLE + WHERE_ID;

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setInt(1, address.getId());
			result = preparedStatement.executeUpdate();

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null) {
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

		String selectSQL = SELECT_FROM + TABLE + " WHERE CAP = ? AND via = ? AND " + COL_NUM_CIVICO + " = ?";
		AddressBean address = null;

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);

			preparedStatement.setString(1, cap);
			preparedStatement.setString(2, via);
			preparedStatement.setString(3, numCivico);

			ResultSet rs = preparedStatement.executeQuery();
			if (!rs.isBeforeFirst())
				return null;

			address = new AddressBean();

			while (rs.next()) {
				address.setId(rs.getInt("ID"));
				address.setVia(rs.getString("via"));
				address.setNumCivico(rs.getString(COL_NUM_CIVICO));
				address.setCitta(rs.getString(COL_CITTA));
				address.setCap(rs.getString("CAP"));
				address.setProvincia(rs.getString(COL_PROVINCIA));
				address.setId_utente(rs.getInt(COL_ID_UTENTE));
				address.setStato(rs.getString(COL_STATO));
			}

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null) {
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

		String selectSQL = SELECT_FROM + TABLE + WHERE_ID;

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, id);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				AddressBean address = new AddressBean();
				address.setId(rs.getInt("ID"));
				address.setVia(rs.getString("via"));
				address.setNumCivico(rs.getString(COL_NUM_CIVICO));
				address.setCitta(rs.getString(COL_CITTA));
				address.setCap(rs.getString("CAP"));
				address.setProvincia(rs.getString(COL_PROVINCIA));
				address.setId_utente(rs.getInt(COL_ID_UTENTE));
				address.setStato(rs.getString(COL_STATO));
				addresses.add(address);
			}

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null) {
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

		String selectSQL = SELECT_FROM + TABLE + WHERE_ID;

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, id);

			ResultSet rs = preparedStatement.executeQuery();
			if (!rs.isBeforeFirst())
				return null;

			address = new AddressBean();

			while (rs.next()) {
				address.setId(rs.getInt("ID"));
				address.setVia(rs.getString("via"));
				address.setNumCivico(rs.getString(COL_NUM_CIVICO));
				address.setCitta(rs.getString(COL_CITTA));
				address.setCap(rs.getString("CAP"));
				address.setProvincia(rs.getString(COL_PROVINCIA));
				address.setId_utente(rs.getInt(COL_ID_UTENTE));
				address.setStato(rs.getString(COL_STATO));
			}

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		}

		return address;
	}
}

package com.vaporant.repository;

import com.vaporant.model.*;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class UserDaoImpl implements UserDAO {

	private static final String TABLE = "utente";
	private static final String WHERE_ID = " WHERE ID = ?";

	private DataSource ds;

	@Autowired
	public UserDaoImpl(DataSource ds) {
		this.ds = ds;
	}

	private Connection getConnection() throws SQLException {
		if (ds != null) {
			Connection conn = ds.getConnection();
			if (conn != null) {
				return conn;
			}
		}
		DataSource staticDs = com.vaporant.util.DataSourceUtil.getDataSource();
		if (staticDs != null) {
			Connection conn = staticDs.getConnection();
			if (conn != null) {
				return conn;
			}
		}
		throw new SQLException("DataSource is null");
	}

	@Override
	public int saveUser(UserBean user) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		int result = 0;

		String insertSQL = "INSERT INTO " + TABLE +
				" (nome, cognome, email, psw, CF, numTelefono, dataNascita, tipo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(insertSQL);

			preparedStatement.setString(1, user.getNome());
			preparedStatement.setString(2, user.getCognome());
			preparedStatement.setString(3, user.getEmail());
			preparedStatement.setString(4, user.getPassword());
			preparedStatement.setString(5, user.getCodF());
			preparedStatement.setString(6, user.getNumTelefono());
			preparedStatement.setString(7, user.getDataNascita().toString());

			String tipo = user.getTipo();
			if (tipo == null || tipo.isEmpty()) {
				tipo = "user";
			}
			preparedStatement.setString(8, tipo);

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
	public int deleteUser(UserBean user) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		int result = 0;

		String deleteSQL = "DELETE FROM " + TABLE + WHERE_ID;

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setInt(1, user.getId());
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
	public UserBean findByCred(String email, String password) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + TABLE + " WHERE email = ? AND psw = ?";
		UserBean user = null;

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);

			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);

			ResultSet rs = preparedStatement.executeQuery();
			if (!rs.isBeforeFirst())
				return null;

			user = new UserBean();

			while (rs.next()) {
				user.setEmail(rs.getString("email"));
				user.setCodF(rs.getString("CF"));
				user.setNome(rs.getString("nome"));
				user.setCognome(rs.getString("cognome"));
				user.setNumTelefono(rs.getString("numTelefono"));
				user.setId(rs.getInt("ID"));
				user.setPassword(rs.getString("psw"));
				user.setTipo(rs.getString("tipo"));
				user.setDataNascita(LocalDate.parse(rs.getDate("dataNascita").toString()));

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

		return user;
	}

	@Override
	public UserBean findById(int ID) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + TABLE + WHERE_ID;
		UserBean user = null;

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, ID);

			ResultSet rs = preparedStatement.executeQuery();
			if (!rs.isBeforeFirst())
				return null;

			user = new UserBean();

			while (rs.next()) {
				user.setEmail(rs.getString("email"));
				user.setCodF(rs.getString("CF"));
				user.setNome(rs.getString("nome"));
				user.setCognome(rs.getString("cognome"));
				user.setNumTelefono(rs.getString("numTelefono"));
				user.setId(rs.getInt("ID"));
				user.setPassword(rs.getString("psw"));
				user.setTipo(rs.getString("tipo"));
				user.setDataNascita(LocalDate.parse(rs.getDate("dataNascita").toString()));
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

		return user;
	}

	@Override
	public void modifyMail(UserBean user, String email) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		String modify = "UPDATE utente SET email = ?" + WHERE_ID;

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(modify);
			preparedStatement.setString(1, email);
			preparedStatement.setInt(2, user.getId());
			preparedStatement.executeUpdate();

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
	}

	@Override
	public void modifyTelefono(UserBean user, String cell) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		String modify = "UPDATE utente SET numTelefono = ?" + WHERE_ID;

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(modify);
			preparedStatement.setString(1, cell);
			preparedStatement.setInt(2, user.getId());
			preparedStatement.executeUpdate();

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
	}

	@Override
	public int modifyPsw(String newPsw, String oldPsw, UserBean user) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		if (oldPsw.compareTo(user.getPassword()) != 0) {
			return 0;
		}
		String modify = "UPDATE utente SET psw = ? " + " WHERE ID = ? AND psw = ?";

		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(modify);
			preparedStatement.setString(1, newPsw);
			preparedStatement.setInt(2, user.getId());
			preparedStatement.setString(3, oldPsw);
			preparedStatement.executeUpdate();

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
		return 1;
	}

}

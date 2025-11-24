package com.vaporant.repository;
import com.vaporant.model.*;

import java.sql.SQLException;

public interface UserDAO {
	
	public int saveUser(UserBean user) throws SQLException; // salva utente
	
	public int deleteUser(UserBean user) throws SQLException; // delete utente
	
	public UserBean findByCred(String email, String password) throws SQLException; // utente con le credenziali	
	
	public UserBean findById(int ID) throws SQLException;

	public void modifyMail(UserBean user, String email) throws SQLException;
	
	public void modifyTelefono(UserBean user, String cell) throws SQLException;

	public void updateAddress(String address, UserBean user) throws SQLException;

	public int modifyPsw(String newPsw, String oldPsw, UserBean user) throws SQLException;
}

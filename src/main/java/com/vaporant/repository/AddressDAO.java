package com.vaporant.repository;
import com.vaporant.model.*;

import java.sql.SQLException;
import java.util.ArrayList;

public interface AddressDAO {
	
	public int saveAddress(AddressBean address) throws SQLException; // salva indirizzo
	
	public int deleteAddress(AddressBean address) throws SQLException; // delete indirizzo
	
	public AddressBean findByCred(String cap, String via, String numCivico) throws SQLException; // ricerca utente per indirizzo (no id)

	ArrayList<AddressBean> findByID(int id) throws SQLException;
	
	public AddressBean findAddressByID(int id) throws SQLException;
	
}

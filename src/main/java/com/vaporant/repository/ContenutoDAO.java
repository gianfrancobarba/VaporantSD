package com.vaporant.repository;
import com.vaporant.model.*;
import java.sql.SQLException;

public interface ContenutoDAO {
	
	public int saveContenuto(ContenutoBean contenutoOrdine) throws SQLException; // salva contenuto ordine
	
	public int deleteContenuto(ContenutoBean contenutoOrdine) throws SQLException; // delete contenuto ordine
	
	public ContenutoBean findByKey(int id_ordine, int id_prodotto) throws SQLException; // ricerca contenuto ordine per id ordine e prodotto


}

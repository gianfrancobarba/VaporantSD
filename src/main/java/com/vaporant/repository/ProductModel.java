package com.vaporant.repository;
import com.vaporant.model.*;

import java.sql.SQLException;
import java.util.Collection;

public interface ProductModel {
	public void doSave(ProductBean product) throws SQLException;

	public boolean doDelete(int id) throws SQLException;

	public ProductBean doRetrieveByKey(int id) throws SQLException;
	
	public Collection<ProductBean> doRetrieveAll(String order) throws SQLException;

	void updateQuantityStorage(ProductBean prod, int quantita) throws SQLException;

}

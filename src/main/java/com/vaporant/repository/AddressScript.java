package com.vaporant.repository;
import com.vaporant.model.*;

public class AddressScript {
	
	private int id;
	private String indirizzo;
	
	public AddressScript(AddressBean address) {
		
		id = address.getId();
		indirizzo = address.toStringScript();
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	
	public String toString() {
		
		return id + " " + indirizzo;
		
	}
}

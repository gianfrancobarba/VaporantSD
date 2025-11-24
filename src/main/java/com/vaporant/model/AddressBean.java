package com.vaporant.model;

public class AddressBean {
	private int id, id_utente;
	private String stato, citta, via, numCivico, cap, provincia;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId_utente() {
		return id_utente;
	}
	
	public void setId_utente(int id_utente) {
		this.id_utente = id_utente;
	}
	
	public String getStato() {
		return stato;
	}
	
	public void setStato(String stato) {
		this.stato = stato;
	}
	
	public String getCitta() {
		return citta;
	}
	
	public void setCitta(String citta) {
		this.citta = citta;
	}
	
	public String getVia() {
		return via;
	}
	
	public void setVia(String via) {
		this.via = via;
	}
	
	public String getNumCivico() {
		return numCivico;
	}
	
	public void setNumCivico(String numCivico) {
		this.numCivico = numCivico;
	}
	
	public String getCap() {
		return cap;
	}
	
	public void setCap(String cap) {
		this.cap = cap;
	}
	
	public String getProvincia() {
		return provincia;
	}
	
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	
	public String toString() {
			
			String s = "";
			
			s += id;
			s += " - "; 
			
		    s += id_utente;
			s += " - "; 
	
			s += stato;
			s += " - "; 
			
		    s += citta;
			s += " - "; 
			
			s += via;
			s += " - "; 
			
		    s += numCivico;
			s += " - "; 
			
			s += cap;
			s += " - "; 
			
		    s += provincia;
			s += "\n"; 
			
			return s;
		}
	
	public String toStringScript() {
		
		return via + ", " + numCivico + " " + citta + ", " + provincia + " " + cap + " " + stato; 
	}
}

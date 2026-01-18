package com.vaporant.model;

public class AddressBean {
	/*@ spec_public @*/ private int id, id_utente;
	/*@ spec_public @*/ private String stato, citta, via, numCivico, cap, provincia;

	/*@ 
	  @ public invariant stato != null;
	  @ public invariant citta != null;
	  @ public invariant via != null;
	  @ public invariant numCivico != null;
	  @ public invariant cap != null && cap.length() == 5;
	  @ public invariant provincia != null;
	  @*/

	/*@ 
	  @ public normal_behavior
	  @   ensures stato != null && citta != null && via != null && numCivico != null;
	  @   ensures cap != null && cap.equals("00000") && provincia != null;
	  @*/
	public AddressBean() {
		this.stato = "";
		this.citta = "";
		this.via = "";
		this.numCivico = "";
		this.cap = "00000";
		this.provincia = "";
	}

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

	/*@ 
	  @ requires stato != null;
	  @ ensures this.stato == stato;
	  @*/
	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getCitta() {
		return citta;
	}

	/*@ 
	  @ requires citta != null;
	  @ ensures this.citta == citta;
	  @*/
	public void setCitta(String citta) {
		this.citta = citta;
	}

	public String getVia() {
		return via;
	}

	/*@ 
	  @ requires via != null;
	  @ ensures this.via == via;
	  @*/
	public void setVia(String via) {
		this.via = via;
	}

	public String getNumCivico() {
		return numCivico;
	}

	/*@ 
	  @ requires numCivico != null;
	  @ ensures this.numCivico == numCivico;
	  @*/
	public void setNumCivico(String numCivico) {
		this.numCivico = numCivico;
	}

	public String getCap() {
		return cap;
	}

	/*@ 
	  @ requires cap != null && cap.length() == 5;
	  @ ensures this.cap == cap;
	  @*/
	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getProvincia() {
		return provincia;
	}

	/*@ 
	  @ requires provincia != null;
	  @ ensures this.provincia == provincia;
	  @*/
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	/*@ skipesc @*/
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

	/*@ skipesc @*/
	public String toStringScript() {

		return via + ", " + numCivico + " " + citta + ", " + provincia + " " + cap + " " + stato;
	}
}

package com.vaporant.model;

import java.time.LocalDate;

public class OrderBean {
	/*@ spec_public @*/ private static int cont = 1;
	/*@ spec_public @*/ private int id_ordine, id_utente, id_indirizzo;
	/*@ spec_public @*/ private double prezzoTot;
	/*@ spec_public @*/ private java.time.LocalDate dataAcquisto;
	/*@ spec_public @*/ private String metodoPagamento;

	/*@ 
	  @ public invariant prezzoTot >= 0;
	  @ public invariant dataAcquisto != null;
	  @ public static invariant cont >= 1;
	  @*/

	/*@ skipesc @*/
	public OrderBean() {
		this.dataAcquisto = java.time.LocalDate.now();
		this.metodoPagamento = "Not Specified";
	}

	/*@ 
	  @ requires pTot >= 0;
	  @ requires dataAcq != null;
	  @ requires payment != null;
	  @ ensures id_utente == idUtente;
	  @ ensures id_indirizzo == idIndirizzo;
	  @ ensures prezzoTot == pTot;
	  @ ensures dataAcquisto == dataAcq;
	  @ ensures metodoPagamento == payment;
	  @*/
	/*@ skipesc @*/
	public OrderBean(int idUtente, int idIndirizzo, double pTot, java.time.LocalDate dataAcq, String payment) {
		this.id_utente = idUtente;
		this.id_indirizzo = idIndirizzo;
		this.prezzoTot = pTot;
		this.dataAcquisto = dataAcq;
		this.metodoPagamento = payment;
		this.id_ordine = cont;
		cont += 1;
	}

	public int getId_ordine() {
		return id_ordine;
	}

	public void setId_ordine(int id_ordine) {
		this.id_ordine = id_ordine;
	}

	public int getId_utente() {
		return id_utente;
	}

	public void setId_utente(int id_utente) {
		this.id_utente = id_utente;
	}

	public int getId_indirizzo() {
		return id_indirizzo;
	}

	public void setId_indirizzo(int id_indirizzo) {
		this.id_indirizzo = id_indirizzo;
	}

	public double getPrezzoTot() {
		return prezzoTot;
	}

	/*@ 
	  @ requires prezzoTot >= 0;
	  @ ensures this.prezzoTot == prezzoTot;
	  @*/
	public void setPrezzoTot(double prezzoTot) {
		this.prezzoTot = prezzoTot;
	}

	public LocalDate getDataAcquisto() {
		return dataAcquisto;
	}

	/*@ 
	  @ requires dataAcquisto != null;
	  @ requires dataAcquisto.isBefore(java.time.LocalDate.now().plusDays(1));
	  @ ensures this.dataAcquisto == dataAcquisto;
	  @*/
	public void setDataAcquisto(LocalDate dataAcquisto) {
		this.dataAcquisto = dataAcquisto;
	}

	public String getMetodoPagamento() {
		return metodoPagamento;
	}

	/*@ 
	  @ requires metodoPagamento != null;
	  @ ensures this.metodoPagamento == metodoPagamento;
	  @*/
	public void setMetodoPagamento(String metodoPagamento) {
		this.metodoPagamento = metodoPagamento;
	}

	/*@ skipesc @*/
	public String toString() {

		String s = "";

		s += id_ordine;
		s += " - ";

		s += id_utente;
		s += " - ";

		s += id_indirizzo;
		s += " - ";

		s += prezzoTot;
		s += " - ";

		s += dataAcquisto;
		s += " - ";

		s += metodoPagamento;
		s += "\n";

		return s;
	}

}

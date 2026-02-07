package com.vaporant.model;

import java.io.Serializable;

public class ContenutoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id_ordine, id_prodotto, quantita, ivaAcquisto;
	private float prezzoAcquisto;

	public ContenutoBean() {

	}

	public ContenutoBean(int ord, int prod, int quant, int iva, float prezzo) {
		id_ordine = ord;
		id_prodotto = prod;
		quantita = quant;
		ivaAcquisto = iva;
		prezzoAcquisto = prezzo;
	}

	public int getId_ordine() {
		return id_ordine;
	}

	public void setId_ordine(int id_ordine) {
		this.id_ordine = id_ordine;
	}

	public int getId_prodotto() {
		return id_prodotto;
	}

	public void setId_prodotto(int id_prodotto) {
		this.id_prodotto = id_prodotto;
	}

	public int getQuantita() {
		return quantita;
	}

	public void setQuantita(int quantita) {
		this.quantita = quantita;
	}

	public int getIvaAcquisto() {
		return ivaAcquisto;
	}

	public void setIvaAcquisto(int ivaAcquisto) {
		this.ivaAcquisto = ivaAcquisto;
	}

	public float getPrezzoAcquisto() {
		return prezzoAcquisto;
	}

	public void setPrezzoAcquisto(float prezzoAcquisto) {
		this.prezzoAcquisto = prezzoAcquisto;
	}

	public String toString() {

		String s = "";

		s += id_ordine;
		s += " - ";

		s += id_prodotto;
		s += " - ";

		s += quantita;
		s += " - ";

		s += prezzoAcquisto;
		s += " - ";

		s += ivaAcquisto;
		s += "\n";

		return s;
	}
}

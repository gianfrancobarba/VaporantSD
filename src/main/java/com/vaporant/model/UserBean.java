package com.vaporant.model;

import java.io.Serializable;

public class UserBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nome, cognome, numTelefono, email, codF, password;

	private String tipo;
	private java.time.LocalDate dataNascita;
	private int id;

	public UserBean() {
		this.nome = "";
		this.cognome = "";
		this.email = "";
		this.password = "";
		this.tipo = "user";
		this.dataNascita = java.time.LocalDate.now().minusYears(18);
		this.codF = "";
		this.numTelefono = "";

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getNumTelefono() {
		return numTelefono;
	}

	public void setNumTelefono(String numTelefono) {
		this.numTelefono = numTelefono;
	}

	public String getCodF() {
		return codF;
	}

	public void setCodF(String codF) {
		this.codF = codF;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password != null && password.length() > 30) {
			throw new IllegalArgumentException("Password can be max 30 chars");
		}
		this.password = password;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		if (tipo == null || (!tipo.equals("user") && !tipo.equals("admin"))) {
			throw new IllegalArgumentException("Tipo must be 'user' or 'admin'");
		}
		this.tipo = tipo;
	}

	public java.time.LocalDate getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(java.time.LocalDate dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String toString() {

		String s = "";

		s += id;
		s += " - ";

		s += nome;
		s += " - ";

		s += cognome;
		s += " - ";

		s += dataNascita;
		s += " - ";

		s += codF;
		s += " - ";

		s += numTelefono;
		s += " - ";

		s += email;
		s += " - ";

		s += password;
		s += " - ";

		s += tipo;
		s += "\n";

		return s;
	}

}

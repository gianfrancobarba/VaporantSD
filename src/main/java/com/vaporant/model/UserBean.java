package com.vaporant.model;

public class UserBean {

	/* @ spec_public @ */ private String nome, cognome, numTelefono, email, codF, password;

	/* @ spec_public @ */ private String tipo;
	/* @ spec_public @ */ private java.time.LocalDate dataNascita;
	/* @ spec_public @ */ private int id;

	/*
	 * @
	 * 
	 * @ public invariant nome != null;
	 * 
	 * @ public invariant cognome != null;
	 * 
	 * @ public invariant email != null;
	 * 
	 * @ public invariant password != null;
	 * 
	 * @ public invariant tipo != null && (tipo.equals("admin") ||
	 * tipo.equals("user"));
	 * 
	 * @ public invariant dataNascita != null;
	 * 
	 * @
	 */
	/*
	 * @
	 * 
	 * @ public normal_behavior
	 * 
	 * @ ensures nome != null && cognome != null && email != null && password !=
	 * null;
	 * 
	 * @ ensures tipo.equals("user") && dataNascita != null;
	 * 
	 * @
	 */
	/* @ skipesc @ */
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

	/*
	 * @
	 * 
	 * @ requires email != null;
	 * 
	 * @ ensures this.email == email;
	 * 
	 * @
	 */
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

	/*
	 * @
	 * 
	 * @ requires nome != null;
	 * 
	 * @ ensures this.nome == nome;
	 * 
	 * @
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	/*
	 * @
	 * 
	 * @ requires cognome != null;
	 * 
	 * @ ensures this.cognome == cognome;
	 * 
	 * @
	 */
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

	/*
	 * @
	 * 
	 * @ requires password != null;
	 * 
	 * @ ensures this.password == password;
	 * 
	 * @
	 */
	public void setPassword(String password) {
		if (password != null && password.length() > 30) {
			throw new IllegalArgumentException("Password can be max 30 chars");
		}
		this.password = password;
	}

	public String getTipo() {
		return tipo;
	}

	/*
	 * @
	 * 
	 * @ requires tipo != null && (tipo.equals("admin") || tipo.equals("user"));
	 * 
	 * @ ensures this.tipo == tipo;
	 * 
	 * @
	 */
	public void setTipo(String tipo) {
		if (tipo == null || (!tipo.equals("user") && !tipo.equals("admin"))) {
			throw new IllegalArgumentException("Tipo must be 'user' or 'admin'");
		}
		this.tipo = tipo;
	}

	public java.time.LocalDate getDataNascita() {
		return dataNascita;
	}

	/*
	 * @
	 * 
	 * @ requires dataNascita != null;
	 * 
	 * @ ensures this.dataNascita == dataNascita;
	 * 
	 * @
	 */
	public void setDataNascita(java.time.LocalDate dataNascita) {
		this.dataNascita = dataNascita;
	}

	/* @ skipesc @ */
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

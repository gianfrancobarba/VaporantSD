package com.vaporant.model;

import java.time.LocalDate;

public class UserBean {
	
	private String nome, cognome, numTelefono, email, codF, password, indirizzoFatt;

	private String tipo;
	private LocalDate dataNascita;
	private int id;
	
	
	public String getIndirizzoFatt() {
		return indirizzoFatt;
	}
	public void setIndirizzoFatt(String indirizzoFatt) {
		this.indirizzoFatt = indirizzoFatt;
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
		this.password = password;
	}

	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public LocalDate getDataNascita() {
		return dataNascita;
	}
	public void setDataNascita(LocalDate dataNascita) {
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
		
		s += indirizzoFatt;
		s += "\n";
		
		return s;
	}

}

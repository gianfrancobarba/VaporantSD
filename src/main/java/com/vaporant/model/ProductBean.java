package com.vaporant.model;

import java.io.Serializable;

public class ProductBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/* @ spec_public @ */ int id;
	/* @ spec_public @ */ String name;
	/* @ spec_public @ */ String description;
	/* @ spec_public @ */ float price;
	/* @ spec_public @ */ int quantity, quantityStorage;
	/* @ spec_public @ */ String tipo;
	/* @ spec_public @ */ String colore;


	public ProductBean() {
		this.setQuantity(1);
	}

	/* @ pure @ */
	public int getCode() {
		return id;
	}


	public void setCode(int code) {
		this.id = code;
	}

	/* @ pure @ */
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	/* @ pure @ */
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

	/* @ pure @ */
	public float getPrice() {
		return price;
	}


	public void setPrice(float price) {
		this.price = price;
	}

	/* @ pure @ */
	public int getQuantity() {
		return quantity;
	}


	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/* @ pure @ */
	public int getQuantityStorage() {
		return quantityStorage;
	}


	public void setQuantityStorage(int quantityS) {
		this.quantityStorage = quantityS;
	}

	/* @ pure @ */
	public String getTipo() {
		return tipo;
	}


	public void setTipo(String tipo) {
		if (tipo != null && tipo.length() > 20) {
			throw new IllegalArgumentException("Tipo can be max 20 chars");
		}
		this.tipo = tipo;
	}

	/* @ pure @ */
	public String getColore() {
		return colore;
	}


	public void setColore(String colore) {
		if (colore != null && colore.length() > 20) {
			throw new IllegalArgumentException("Colore can be max 20 chars");
		}
		this.colore = colore;
	}

	/* @ skipesc @ */
	@Override
	public String toString() {
		return "N - " + name + " - I. " + id + " - P. " + price + " - Q." + quantity + " - D. " + description
				+ " - QS. " + quantityStorage;
	}

	/* @ skipesc @ */
	public String toStringProduct() {
		return name + " " + id + " " + price + " " + description;
	}
}

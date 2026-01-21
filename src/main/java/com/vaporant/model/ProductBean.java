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
	/*
	 * @
	 * 
	 * @ public invariant price >= 0;
	 * 
	 * @ public invariant quantity >= 0;
	 * 
	 * @ public invariant quantityStorage >= 0;
	 * 
	 * @
	 */

	public ProductBean() {
		this.setQuantity(1);
	}

	/* @ pure @ */
	public int getCode() {
		return id;
	}

	/*
	 * @
	 * 
	 * @ requires code >= 0;
	 * 
	 * @ ensures id == code;
	 * 
	 * @
	 */
	public void setCode(int code) {
		this.id = code;
	}

	/* @ pure @ */
	public String getName() {
		return name;
	}

	/*
	 * @
	 * 
	 * @ requires name != null;
	 * 
	 * @ ensures this.name == name;
	 * 
	 * @
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* @ pure @ */
	public String getDescription() {
		return description;
	}

	/*
	 * @
	 * 
	 * @ requires description != null;
	 * 
	 * @ ensures this.description == description;
	 * 
	 * @
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* @ pure @ */
	public float getPrice() {
		return price;
	}

	/*
	 * @
	 * 
	 * @ requires price >= 0;
	 * 
	 * @ ensures this.price == price;
	 * 
	 * @
	 */
	public void setPrice(float price) {
		this.price = price;
	}

	/* @ pure @ */
	public int getQuantity() {
		return quantity;
	}

	/*
	 * @
	 * 
	 * @ requires quantity >= 0;
	 * 
	 * @ ensures this.quantity == quantity;
	 * 
	 * @
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/* @ pure @ */
	public int getQuantityStorage() {
		return quantityStorage;
	}

	/*
	 * @
	 * 
	 * @ requires quantityS >= 0;
	 * 
	 * @ ensures this.quantityStorage == quantityS;
	 * 
	 * @
	 */
	public void setQuantityStorage(int quantityS) {
		this.quantityStorage = quantityS;
	}

	/* @ pure @ */
	public String getTipo() {
		return tipo;
	}

	/*
	 * @
	 * 
	 * @ requires tipo != null;
	 * 
	 * @ ensures this.tipo == tipo;
	 * 
	 * @
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/* @ pure @ */
	public String getColore() {
		return colore;
	}

	/*
	 * @
	 * 
	 * @ requires colore != null;
	 * 
	 * @ ensures this.colore == colore;
	 * 
	 * @
	 */
	public void setColore(String colore) {
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

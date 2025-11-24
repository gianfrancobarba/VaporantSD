package com.vaporant.model;

import java.io.Serializable;

public class ProductBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	int id;
	String name;
	String description;
	float price;
	int quantity, quantityStorage;

	
	public ProductBean() {
		this.setQuantity(1);
	}
	
	public int getCode() {
		return id;
	}

	public void setCode(int code) {
		this.id = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public int getQuantityStorage() {
		return quantityStorage;
	}

	public void setQuantityStorage(int quantityS) {
		this.quantityStorage = quantityS;
	}

	@Override
	public String toString() {
		return "N - " + name + " - I. " + id + " - P. " + price + " - Q." + quantity + " - D. " + description + " - QS. " + quantityStorage;
	}
	
	public String toStringProduct() {
		return name + " " + id + " " + price + " " + description;
	}
}

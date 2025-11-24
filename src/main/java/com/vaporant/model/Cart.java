package com.vaporant.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

	private final ArrayList<ProductBean> products;
	private double prezzoTotale = 0;

	public Cart() {
		products = new ArrayList<>();
	}
	
	public double getPrezzoTotale() {
		
		return  Math.round(prezzoTotale*100.0)/100.0;
	}

	public void setPrezzoTotale(double prezzoTotale) {
		this.prezzoTotale = prezzoTotale;
	}
	
	public List<ProductBean> getProducts() {
		return  products;
	}
	
	public void addProduct(ProductBean product) {
		
		ProductBean prod = containsProduct(product);
		
		if (!products.isEmpty() && prod != null) 
		{
			if(prod.getQuantity() < prod.getQuantityStorage())
				aggiorna(product,prod.getQuantity() + 1);	
		} else {
			products.add(product);
			setPrezzoTotale(prezzoTotale += product.getPrice());
		}

	}
	
	public void deleteProduct(ProductBean product) {
		for(ProductBean prod : products) {
			if(prod.getCode() == product.getCode()) {
					setPrezzoTotale(prezzoTotale -= prod.getPrice()*prod.getQuantity());
					products.remove(prod);
					
				break;
			}
		}
 	}
	
	public ProductBean containsProduct(ProductBean product) {
		for (ProductBean pb : products) {
			if (pb.toStringProduct().compareTo(product.toStringProduct()) == 0) {
				return pb;
			}
		}
		return null;
}

	public void aggiorna(ProductBean product, int quantita) {
				
		int index;
		for (index = 0; index < products.size(); index++) {
			if (products.get(index).toStringProduct().compareTo(product.toStringProduct()) == 0) {
				
				setPrezzoTotale(prezzoTotale -= products.get(index).getPrice() * (products.get(index).getQuantity()) );
				
				products.get(index).setQuantity(quantita);
				setPrezzoTotale(prezzoTotale += products.get(index).getPrice() * (quantita) );
				
				break;
			}
		}
	}
}

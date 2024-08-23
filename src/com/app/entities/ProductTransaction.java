package com.app.entities;

public class ProductTransaction implements Entity {
	private static int idCounter = 0;
	private int id;
	private int productId;
	private double productPrice;
	private int titleId; /* -1 se for uma adição no sistema */
	private int quantityChange;

	public static final String FILE = "data/product-transactions.txt";

	public ProductTransaction(int productId, double productPrice, int titleId, int quantityChange) {
		this.id = ++idCounter;
		this.productId = productId;
		this.productPrice = productPrice;
		this.titleId = titleId;
		this.quantityChange = quantityChange;
	}

	public ProductTransaction(int id, int productId, double productPrice, int titleId, int quantityChange) {
		this.id = id;
		this.productId = productId;
		this.productPrice = productPrice;
		this.titleId = titleId;
		this.quantityChange = quantityChange;
	}

	@Override
	public int getId() {
		return id;
	}

	public int getProdcutId() {
		return productId;
	}

	public int getTitleId() {
		return titleId;
	}

	public int getQuantityChange() {
		return quantityChange;
	}

	public double getProductPrice() {
		return productPrice;
	}

	@Override
	public String toString() {
		return id + "," + productId + "," + productPrice + "," + titleId + "," + quantityChange;
	}

	public static ProductTransaction fromString(String str) {
		String[] parts = str.split(",");
		return new ProductTransaction(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
				Double.parseDouble(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
	}

	public static void setIdCounter(int count) {
		if (idCounter == 0) {
			idCounter = count;
		}
	}
}

package com.app.entities;

public class Product implements Entity {
	private static int idCounter = 0;
	private int id;
	private String name;
	private double price;
	private int quantity, minQuantity;

	public static final String FILE = "data/products.txt";

	public Product(String name, double price, int quantity, int minQuantity) {
		this.id = ++idCounter;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.minQuantity = minQuantity;
	}

	public Product(int id, String name, double price, int quantity, int minQuantity) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.minQuantity = minQuantity;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void decrementQuantity() {
		this.quantity--;
	}

	public int getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(int minQuantity) {
		this.minQuantity = minQuantity;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return id + "," + name + "," + price + "," + quantity + "," + minQuantity;
	}

	public static Product fromString(String str) {
		String[] parts = str.split(",");
		return new Product(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]),
				Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
	}

	public static void setIdCounter(int count) {
		if (idCounter == 0) {
			idCounter = count;
		}
	}
}

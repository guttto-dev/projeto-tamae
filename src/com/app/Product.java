package com.app;

public class Product implements Entity {
	private static int idCounter = 0;
	private int id;
	private String name;
	private double price;

	public static final String FILE = "products.txt";

	public Product(String name, double price) {
		this.id = ++idCounter;
		this.name = name;
		this.price = price;
	}

	public Product(int id, String name, double price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return id + "," + name + "," + price;
	}

	public static Product fromString(String str) {
		String[] parts = str.split(",");
		return new Product(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]));
	}

	public static void setIdCounter(int count) {
		if (idCounter == 0) {
			idCounter = count;
		}
	}
}

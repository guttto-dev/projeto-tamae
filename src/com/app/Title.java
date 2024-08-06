package com.app;

public class Title {
	private String id;
	private double amount;
	private boolean paid;

	public Title(String id, double amount, boolean paid) {
		this.id = id;
		this.amount = amount;
		this.paid = paid;
	}

	public String getId() {
		return id;
	}

	public double getAmount() {
		return amount;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	@Override
	public String toString() {
		return id + "," + amount + "," + paid;
	}

	public static Title fromString(String str) {
		String[] parts = str.split(",");
		return new Title(parts[0], Double.parseDouble(parts[1]), Boolean.parseBoolean(parts[2]));
	}
}

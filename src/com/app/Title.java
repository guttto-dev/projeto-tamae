package com.app;

public class Title implements Entity {
	private static int idCounter = 0;
	private int id;
	private int clientId;
	private double amount;
	private boolean paid;

	public static final String FILE = "data/titles.txt";

	public Title(int clientId, double amount, boolean paid) {
		this.id = ++idCounter;
		this.clientId = clientId;
		this.amount = amount;
		this.paid = paid;
	}

	public Title(int id, int clientId, double amount, boolean paid) {
		this.id = id;
		this.clientId = clientId;
		this.amount = amount;
		this.paid = paid;
	}

	@Override
	public int getId() {
		return id;
	}

	public int getClientId() {
		return clientId;
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
		return id + "," + clientId + "," + amount + "," + paid;
	}

	public static Title fromString(String str) {
		String[] parts = str.split(",");
		return new Title(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Double.parseDouble(parts[2]),
				Boolean.parseBoolean(parts[3]));
	}

	public static void setIdCounter(int count) {
		if (idCounter == 0) {
			idCounter = count;
		}
	}
}

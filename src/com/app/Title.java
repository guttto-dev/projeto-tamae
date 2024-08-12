package com.app;

public class Title implements Entity {
	private String id;
	private String clientId;
	private double amount;
	private boolean paid;

	public Title(String id, String clientId, double amount, boolean paid) {
		this.id = id;
		this.clientId = clientId;
		this.amount = amount;
		this.paid = paid;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getClientId() {
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
		return new Title(parts[0], parts[1], Double.parseDouble(parts[2]), Boolean.parseBoolean(parts[3]));
	}
}

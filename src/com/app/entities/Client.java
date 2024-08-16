package com.app.entities;

public class Client implements Entity {
	private static int idCounter = 0;
	private int id;
	private String name;
	private String phoneNumber;

	public static final String FILE = "data/clients.txt";

	public Client(String name, String phoneNumber) {
		this.id = ++idCounter;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	public Client(int id, String name, String phoneNumber) {
		this.id = id;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	@Override
	public String toString() {
		return id + "," + name + "," + phoneNumber;
	}

	public static Client fromString(String str) {
		String[] parts = str.split(",");
		return new Client(Integer.parseInt(parts[0]), parts[1], parts[2]);
	}

	public static void setIdCounter(int count) {
		if (idCounter == 0) {
			idCounter = count;
		}
	}
}

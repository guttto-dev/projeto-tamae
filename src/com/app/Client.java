package com.app;

public class Client implements Entity {
	private String id;
	private String name;
	private String phoneNumber;

	public Client(String id, String name, String phoneNumber) {
		this.id = id;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String getId() {
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
		return new Client(parts[0], parts[1], parts[2]);
	}
}

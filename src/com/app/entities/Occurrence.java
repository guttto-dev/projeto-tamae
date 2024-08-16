package com.app.entities;

public class Occurrence implements Entity {
	private static int idCounter = 0;
	private int id;
	private String username;
	private String text;
	private boolean resolved;

	public static final String FILE = "data/occurrences.txt";

	public Occurrence(String username, String text, boolean resolved) {
		this.id = ++idCounter;
		this.username = username;
		this.text = text;
		this.resolved = resolved;
	}

	public Occurrence(int id, String username, String text, boolean resolved) {
		this.id = id;
		this.username = username;
		this.text = text;
		this.resolved = resolved;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getText() {
		return text;
	}

	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	@Override
	public String toString() {
		return id + "," + username + "," + text + "," + resolved;
	}

	public static Occurrence fromString(String str) {
		String[] parts = str.split(",");
		return new Occurrence(Integer.parseInt(parts[0]), parts[1], parts[2], Boolean.parseBoolean(parts[3]));
	}

	public static void setIdCounter(int count) {
		if (idCounter == 0) {
			idCounter = count;
		}
	}
}

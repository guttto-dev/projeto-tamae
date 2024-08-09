package com.app;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputHelper {
	private static Scanner scanner = new Scanner(System.in);

	public static String readString(String prompt) {
		while (true) {
			System.out.print(prompt);
			try {
				return scanner.nextLine();
			} catch (InputMismatchException e) {
				System.out.println("Entrada inválida. Por favor, tente novamente.");
			}
		}
	}

	public static int readInt(String prompt) {
		while (true) {
			System.out.print(prompt);
			try {
				return scanner.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
			} finally {
				clearBuffer();
			}
		}
	}

	public static double readDouble(String prompt) {
		while (true) {
			System.out.print(prompt);
			try {
				return scanner.nextDouble();
			} catch (InputMismatchException e) {
				System.out.println("Entrada inválida. Por favor, insira um número decimal.");
			} finally {
				clearBuffer();
			}
		}
	}

	public static void clearBuffer() {
		if (scanner.hasNextLine()) {
			scanner.nextLine(); // Consumir nova linha
		}
	}

	public static void close() {
		if (scanner != null) {
			scanner.close();
		}
	}
}

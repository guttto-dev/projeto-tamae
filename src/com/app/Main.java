package com.app;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
	private static final String ERROR_STORE_MESSAGE = "Não foi possível carregar os arquivos, comunique ao setor de TI.";
	private static final String INVALID_INPUT_MESSAGE = "Entrada inválida. Deve ser um número.";
	private static final String INVALID_OPTION_MESSAGE = "Opção inválida. Tente novamente.";
	private static final String EXIT_MESSAGE = "Saindo...";

	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		Store store = null;
		try {
			store = new Store();
		} catch (IOException e) {
			System.out.println(ERROR_STORE_MESSAGE);
			return;
		}

		while (true) {
			System.out.print("""
					Menu:
					1. Adicionar Produto
					2. Listar Produtos
					3. Comprar Produto
					4. Efetuar Pagamento
					5. Listar Títulos em Aberto
					6. Sair
					""");
			int choice = getUserChoice();

			switch (choice) {
			case 1:
				store.addProduct(scanner);
				break;
			case 2:
				store.listProducts();
				break;
			case 3:
				store.purchaseProduct(scanner);
				break;
			case 4:
				store.makePayment(scanner);
				break;
			case 5:
				store.listOutstandingTitles();
				break;
			case 6:
				System.out.println(EXIT_MESSAGE);
				scanner.close();
				return;
			default:
				System.out.println(INVALID_OPTION_MESSAGE);
			}
		}
	}

	private static int getUserChoice() {
		int choice = -1;
		while (choice <= -1) {
			try {
				System.out.print("Escolha uma opção: ");
				choice = scanner.nextInt();
			} catch (InputMismatchException e) {
				choice = -1;
				System.out.println(INVALID_INPUT_MESSAGE);
			}
			scanner.nextLine(); // Consumir nova linha
		}
		return choice;
	}
}

package com.app;

import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		Store store = new Store();

		while (true) {
			System.out.println("Menu:");
			System.out.println("1. Adicionar Produto");
			System.out.println("2. Listar Produtos");
			System.out.println("3. Comprar Produto");
			System.out.println("4. Efetuar Pagamento");
			System.out.println("5. Listar Títulos em Aberto");
			System.out.println("6. Sair");
			System.out.print("Escolha uma opção: ");
			int choice = scanner.nextInt();
			scanner.nextLine(); // Consumir nova linha

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
				System.out.println("Saindo...");
				return;
			default:
				System.out.println("Opção inválida. Tente novamente.");
			}
		}
	}
}

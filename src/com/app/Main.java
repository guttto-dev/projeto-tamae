package com.app;

import java.io.IOException;
import java.util.NoSuchElementException;

public class Main {
	public static void main(String[] args) {
		try {
			runStore();
		} catch (NoSuchElementException e) {
			// Lida com o caso de CTRL-D (EOF) ser pressionado
		} finally {
			InputHelper.close();
			System.out.println("Saindo...");
		}
	}

	private static void runStore() {
		Store store = null;
		try {
			store = new Store();
		} catch (IOException e) {
			System.out.println("ERRO: Não foi possível carregar os arquivos, comunique ao setor de TI.");
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
			int choice = InputHelper.readInt("Escolha uma opção: ");

			switch (choice) {
			case 1:
				store.addProduct();
				break;
			case 2:
				store.listProducts();
				break;
			case 3:
				store.purchaseProduct();
				break;
			case 4:
				store.makePayment();
				break;
			case 5:
				store.listOutstandingTitles();
				break;
			case 6:
				return;
			default:
				System.out.println("ERRO: Opção inválida. Por favor, tente um número do menu.");
			}
			System.out.println();
		}
	}
}

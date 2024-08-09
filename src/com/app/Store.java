package com.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Store {
	private List<Product> products;
	private List<Title> titles;

	private static final String PRODUCTS_FILE = "products.txt";
	private static final String TITLES_FILE = "titles.txt";

	public Store() throws IOException {
		products = new ArrayList<>();
		titles = new ArrayList<>();
		loadProducts();
		loadTitles();
	}

	public void addProduct(Scanner scanner) {
		System.out.print("ID do Produto: ");
		String id = scanner.nextLine();
		System.out.print("Nome do Produto: ");
		String name = scanner.nextLine();
		System.out.print("Preço do Produto: ");
		double price = scanner.nextDouble();
		scanner.nextLine(); // Consumir nova linha

		Product product = new Product(id, name, price);
		products.add(product);
		try {
			saveProducts();
		} catch (IOException e) {
			System.out.println("Não foi adicionar o produto, comunique ao setor de TI.");
			return;
		}
		System.out.println("Produto adicionado com sucesso.");
	}

	public void listProducts() {
		System.out.println("Produtos:");
		for (Product product : products) {
			System.out.println(product.getId() + " - " + product.getName() + " - R$ " + product.getPrice());
		}
	}

	public void purchaseProduct(Scanner scanner) {
		System.out.print("ID do Produto a comprar: ");
		String productId = scanner.nextLine();

		Product product = null;
		for (Product p : products) {
			if (p.getId().equals(productId)) {
				product = p;
				break;
			}
		}

		if (product != null) {
			Title title = new Title(UUID.randomUUID().toString(), product.getPrice(), false);
			titles.add(title);
			try {
				saveTitles();
			} catch (IOException e) {
				System.out.println("Não foi comprar o produto, comunique ao setor de TI.");
				return;
			}
			System.out.println("Produto comprado. Título gerado: " + title.getId());
		} else {
			System.out.println("Produto não encontrado.");
		}
	}

	public void makePayment(Scanner scanner) {
		System.out.print("ID do Título a pagar: ");
		String titleId = scanner.nextLine();

		Title title = null;
		for (Title t : titles) {
			if (t.getId().equals(titleId)) {
				title = t;
				break;
			}
		}

		if (title != null) {
			title.setPaid(true);
			try {
				saveTitles();
			} catch (IOException e) {
				System.out.println("Não foi pagar o título, comunique ao setor de TI.");
				return;
			}
			System.out.println("Título pago com sucesso.");
		} else {
			System.out.println("Título não encontrado.");
		}
	}

	public void listOutstandingTitles() {
		System.out.println("Títulos em Aberto:");
		for (Title title : titles) {
			if (!title.isPaid()) {
				System.out.println(title.getId().toString() + " - R$ " + title.getAmount());
			}
		}
	}

	private void loadProducts() throws IOException {
		File file = new File(PRODUCTS_FILE);
		if (file.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = reader.readLine()) != null) {
					products.add(Product.fromString(line));
				}
			}
		}
	}

	private void loadTitles() throws IOException {
		File file = new File(TITLES_FILE);
		if (file.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = reader.readLine()) != null) {
					titles.add(Title.fromString(line));
				}
			}
		}
	}

	private void saveProducts() throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
			for (Product product : products) {
				writer.write(product.toString());
				writer.newLine();
			}
		}
	}

	private void saveTitles() throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(TITLES_FILE))) {
			for (Title title : titles) {
				writer.write(title.toString());
				writer.newLine();
			}
		}
	}
}

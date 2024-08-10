package com.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

interface Entity {
	public String getId();

	public static Entity fromString(String str) {
		return null;
	}
}

public class Store {
	private List<Product> products;
	private List<Title> titles;
	private List<Client> clients;

	private static final String PRODUCTS_FILE = "products.txt";
	private static final String TITLES_FILE = "titles.txt";
	private static final String CLIENTS_FILE = "clients.txt";

	public Store() throws IOException {
		products = new ArrayList<>();
		titles = new ArrayList<>();
		clients = new ArrayList<>();
		loadEntities(products, PRODUCTS_FILE);
		loadEntities(titles, TITLES_FILE);
		loadEntities(clients, CLIENTS_FILE);
	}

	public void addProduct() {
		String id = InputHelper.readString("ID do Produto: ");
		if (id.isBlank()) {
			System.out.println("ERRO: O id está em branco. Tente novamente.");
			return;
		}

		String name = InputHelper.readString("Nome do Produto: ");
		if (name.isBlank()) {
			System.out.println("ERRO: O nome está em branco. Tente novamente.");
			return;
		}

		double price = InputHelper.readDouble("Preço do Produto: ");
		if (price <= 0) {
			System.out.println("ERRO: O preço é inválido. Tente novamente.");
			return;
		}

		Product product = new Product(id, name, price);
		products.add(product);
		if (saveEntities(products, PRODUCTS_FILE)) {
			System.out.println("Produto adicionado com sucesso.");
		}
	}

	public void listProducts() {
		System.out.println("Produtos:");
		for (Product product : products) {
			System.out.println(product.getId() + " - " + product.getName() + " - R$ " + product.getPrice());
		}
	}

	public void purchaseProduct() {
		String productId = InputHelper.readString("ID do Produto a comprar: ");
		Product product = findEntityById(products, productId);

		if (product != null) {
			Title title = new Title(UUID.randomUUID().toString(), "TODO", product.getPrice(), false);
			titles.add(title);
			if (saveEntities(titles, TITLES_FILE)) {
				System.out.println("Produto comprado. Título gerado: " + title.getId());
			}
		} else {
			System.out.println("Produto não encontrado.");
		}
	}

	public void makePayment() {
		String titleId = InputHelper.readString("ID do Título a pagar: ");
		Title title = findEntityById(titles, titleId);

		if (title != null) {
			title.setPaid(true);
			if (saveEntities(titles, TITLES_FILE)) {
				System.out.println("Título pago com sucesso.");
			}
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

	@SuppressWarnings("unchecked")
	private <T extends Entity> void loadEntities(List<T> entities, String fileName) throws IOException {
		File file = new File(fileName);
		if (file.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = reader.readLine()) != null) {
					entities.add((T) Entity.fromString(line));
				}
			}
		}
	}

	private <T extends Entity> boolean saveEntities(List<T> entities, String fileName) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
			for (T entity : entities) {
				writer.write(entity.toString());
				writer.newLine();
			}
			return true;
		} catch (IOException e) {
			System.out.println("ERRO: Não foi possível salvar os dados, comunique ao setor de TI.");
			return false;
		}
	}

	private <T extends Entity> T findEntityById(List<T> entities, String id) {
		for (T e : entities) {
			if (e.getId().equals(id)) {
				return e;
			}
		}
		return null;
	}
}

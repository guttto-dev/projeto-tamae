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
}

@FunctionalInterface
interface EntityParser<T> {
    T parse(String line);
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
		loadEntities(products, PRODUCTS_FILE, Product::fromString);
		loadEntities(titles, TITLES_FILE, Title::fromString);
		loadEntities(clients, CLIENTS_FILE, Client::fromString);
	}

	public void addProduct() {
		String id = InputHelper.readString("ID do Produto: ");
		if (id.isBlank()) {
			System.out.println("ERRO: O ID está em branco. Tente novamente.");
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

		if (addEntity(products, new Product(id, name, price))) {
			System.out.println("Produto adicionado com sucesso.");
		} else {
			System.out.println("Não foi possível adicionar o produto.");
		}
	}

	public void listProducts() {
		System.out.println("\nProdutos:");
		for (Product product : products) {
			System.out.println(product.getId() + " - " + product.getName() + " - R$ " + product.getPrice());
		}
	}

	public void listClients() {
		System.out.println("\nClientes:");
		for (Client client : clients) {
			System.out.println(client.getId() + " - " + client.getName() + " - " + client.getPhoneNumber());
		}
	}

	public void purchaseProduct() {
		String productId = InputHelper.readString("ID do Produto a comprar: ");
		Product product = findEntityById(products, productId);

		if (product == null) {
			System.out.println("ERRO: Produto não encontrado.");
			return;
		}

		listClients();
		String clientId = InputHelper.readString("ID do Cliente comprador (digite vazio caso não exista): ");
		if (clientId.isBlank()) {
			if (InputHelper.readYesOrNo("O cliente deseja criar um cadastro?")) {
				clientId = createClientAccount();
				if (clientId == null) {
					System.out.println("Não foi possível criar o cadastro.");
					return;
				}
				System.out.println("O cliente foi cadastrado com sucesso.");
			} else {
				clientId = "ANÔNIMO";
			}
		} else if (findEntityById(clients, clientId) == null) {
			System.out.println("ERRO: ID do Cliente não foi encontrado. Tente novamente.");
			return;
		}

		Title title = new Title(UUID.randomUUID().toString(), clientId, product.getPrice(), false);
		if (addEntity(titles, title)) {
			System.out.println("Produto comprado. Título gerado: " + title.getId());
		} else {
			System.out.println("Não foi possível comprar o produto.");
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

	/* Retorna novo ID do cliente */
	public String createClientAccount() {
		String id = InputHelper.readString("ID do Cliente:");
		if (id.isBlank()) {
			System.out.println("ERRO: O ID está em branco. Tente novamente.");
			return null;
		}

		String name = InputHelper.readString("Nome do Cliente: ");
		if (name.isBlank()) {
			System.out.println("ERRO: O nome está em branco. Tente novamente.");
			return null;
		}

		String number = InputHelper.readString("Número de telefone do Cliente: ");
		if (number.isBlank()) {
			System.out.println("ERRO: O nome está em branco. Tente novamente.");
			return null;
		}

		if (addEntity(clients, new Client(id, name, number))) {
			return id;
		} else {
			return null;
		}
	}

	public void listOutstandingTitles() {
		System.out.println("Títulos em Aberto:");
		for (Title title : titles) {
			if (!title.isPaid()) {
				System.out.println(title.getId() + " - " + title.getClientId() + " - R$ " + title.getAmount());
			}
		}
	}

	private <T extends Entity> void loadEntities(List<T> entities, String fileName, EntityParser<T> parser)
			throws IOException {
		File file = new File(fileName);
		if (file.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = reader.readLine()) != null) {
					entities.add(parser.parse(line));
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

	private <T extends Entity> boolean addEntity(List<T> entities, T entity) {
		if (findEntityById(entities, entity.getId()) != null) {
			System.out.println("ERRO: ID já existe no banco de dados.");
			return false;
		}
		Class<?> cl = entity.getClass();
		entities.add(entity);
		if (cl == Product.class) {
			saveEntities(entities, PRODUCTS_FILE);
		} else if (cl == Title.class) {
			saveEntities(entities, TITLES_FILE);
		} else if (cl == Client.class) {
			saveEntities(entities, CLIENTS_FILE);
		} else {
			System.out.println("ERRO: Não foi possível salvar o novo dado");
			return false;
		}
		return true;
	}
}

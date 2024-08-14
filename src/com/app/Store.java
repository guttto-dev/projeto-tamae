package com.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Store {
	public enum AccessLevel {
		OWNER(0),
		MANAGER(1),
		OPERATOR(2);

		@SuppressWarnings("unused")
		private final int id;
	    AccessLevel(int id) {
	        this.id = id;
	    }
	}

	AccessLevel level;
	private List<Product> products;
	private List<Title> titles;
	private List<Client> clients;

	public Store(AccessLevel level) throws IOException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		this.level = level;
		products = new ArrayList<>();
		titles = new ArrayList<>();
		clients = new ArrayList<>();
		loadEntities(Product.class, products);
		loadEntities(Title.class, titles);
		loadEntities(Client.class, clients);
	}

	public void addProduct() {
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

		if (addEntity(products, new Product(name, price))) {
			System.out.println("Produto adicionado com sucesso.");
		} else {
			System.out.println("Não foi possível adicionar o produto.");
		}
	}

	public void listProducts() {
		System.out.print("\nProdutos:");
		if (products.isEmpty()) {
			System.out.println(" <NENHUM>");
			return;
		} else {
			System.out.println();
		}
		for (Product product : products) {
			System.out.println(product.getId() + " - " + product.getName() + " - R$ " + product.getPrice());
		}
	}

	public void listClients() {
		System.out.print("\nClientes:");
		if (clients.isEmpty()) {
			System.out.println(" <NENHUM>");
			return;
		} else {
			System.out.println();
		}
		for (Client client : clients) {
			System.out.println(client.getId() + " - " + client.getName() + " - " + client.getPhoneNumber());
		}
	}

	public void listOutstandingTitles() {
		boolean once = false;
		System.out.print("\nTítulos em Aberto:");
		for (Title title : titles) {
			if (!title.isPaid()) {
				if (!once) {
					once = true;
					System.out.println();
				}
				System.out.println(title.getId() + " - " + title.getClientId() + " - R$ " + title.getAmount());
			}
		}
		if (!once) {
			System.out.println(" <NENHUM>");
		}
	}

	public void purchaseProduct() {
		listProducts();
		int productId = InputHelper.readInt("ID do Produto a comprar: ");
		Product product = findEntityById(products, productId);

		if (product == null) {
			System.out.println("ERRO: Produto não encontrado.");
			return;
		}

		listClients();
		String idPrompt = InputHelper.readString("ID do Cliente comprador (digite vazio caso não exista): ");
		int clientId = -1;
		if (idPrompt.isBlank()) {
			if (InputHelper.readYesOrNo("O cliente deseja criar um cadastro?")) {
				clientId = createClientAccount();
				if (clientId == -1) {
					System.out.println("Não foi possível criar o cadastro.");
					return;
				}
				System.out.println("O cliente foi cadastrado com sucesso.");
			} else {
				clientId = -1; // cliente anônimo
			}
		} else {
			clientId = Integer.parseInt(idPrompt);
			if (findEntityById(clients, clientId) == null) {
				System.out.println("ERRO: Cliente não foi encontrado. Tente novamente.");
				return;
			}
		}

		Title title = new Title(clientId, product.getPrice(), false);
		if (addEntity(titles, title)) {
			System.out.println("Produto comprado. Título gerado: " + title.getId());
		} else {
			System.out.println("Não foi possível comprar o produto.");
			return;
		}

		if (InputHelper.readYesOrNo("Fazer o pagamento?")) {
			makePayment(title);
		}
	}

	public void makePayment() {
		listOutstandingTitles();
		int titleId = InputHelper.readInt("ID do Título a pagar: ");
		Title title = findEntityById(titles, titleId);

		if (title == null) {
			System.out.println("Título não encontrado.");
			return;
		}
		title.setPaid(true);
		if (saveEntities(Title.class, titles)) {
			System.out.println("Título pago com sucesso.");
		}
	}

	public void makePayment(Title title) {
		if (title == null) {
			System.out.println("Título não encontrado.");
			return;
		}
		title.setPaid(true);
		if (saveEntities(Title.class, titles)) {
			System.out.println("Título pago com sucesso.");
		}
	}

	/* Retorna novo ID do cliente */
	public int createClientAccount() {
		String name = InputHelper.readString("Nome do Cliente: ");
		if (name.isBlank()) {
			System.out.println("ERRO: O nome está em branco. Tente novamente.");
			return -1;
		}

		String number = InputHelper.readString("Número de telefone do Cliente: ");
		if (number.isBlank()) {
			System.out.println("ERRO: O nome está em branco. Tente novamente.");
			return -1;
		}

		Client c = new Client(name, number);
		if (addEntity(clients, c)) {
			return c.getId();
		} else {
			return -1;
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Entity> void loadEntities(Class<T> cl, List<T> entities)
			throws IOException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		File file = new File((String) cl.getField("FILE").get(cl));
		Method parser = cl.getMethod("fromString", String.class);
		int maxId = 0;
		if (file.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = reader.readLine()) != null) {
					T e = (T) parser.invoke(cl, line);
					if (e.getId() > maxId) {
						maxId = e.getId();
					}
					entities.add(e);

				}
			}
		}
		Method idCounter = cl.getMethod("setIdCounter", int.class);
		idCounter.invoke(cl, maxId);
	}

	private <T extends Entity> boolean saveEntities(Class<T> cl, List<T> entities) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter((String) cl.getField("FILE").get(cl)))) {
			for (T entity : entities) {
				writer.write(entity.toString());
				writer.newLine();
			}
			return true;
		} catch (IOException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			System.out.println("ERRO: Não foi possível salvar os dados, comunique ao setor de TI.");
			return false;
		}
	}

	private <T extends Entity> T findEntityById(List<T> entities, int id) {
		for (T e : entities) {
			if (e.getId() == id) {
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
		@SuppressWarnings("unchecked")
		Class<T> cl = (Class<T>) entity.getClass();
		entities.add(entity);
		saveEntities(cl, entities);
		return true;
	}
}

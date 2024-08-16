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

import com.app.entities.Client;
import com.app.entities.Entity;
import com.app.entities.Occurrence;
import com.app.entities.Product;
import com.app.entities.Title;
import com.app.util.InputHelper;

public class Store {
	public enum AccessLevel {
		OWNER(0), MANAGER(1), OPERATOR(2);

		public final int id;

		AccessLevel(int id) {
			this.id = id;
		}
	}

	public final AccessLevel level;
	public final String username;
	private List<Product> products;
	private List<Title> titles;
	private List<Client> clients;
	private List<Occurrence> occurrences;

	public Store(AccessLevel level, String username) throws IOException, NoSuchFieldException, NoSuchMethodException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.level = level;
		this.username = username;
		products = new ArrayList<>();
		titles = new ArrayList<>();
		clients = new ArrayList<>();
		occurrences = new ArrayList<>();
		loadEntities(Product.class, products);
		loadEntities(Title.class, titles);
		loadEntities(Client.class, clients);
		loadEntities(Occurrence.class, occurrences);
	}

	public void addProduct() {
		System.out.print("""
				\n[Produtos]
				1. Novo Produto
				2. Alterar quantidade do Produto
				3. Voltar
				""");
		int choice = InputHelper.readInt("Escolha uma opção: ");

		switch (choice) {
		case 1:
			break;
		case 2:
			int id = InputHelper.readInt("\nID do Produto: ");
			Product product = findEntityById(products, id);
			if (product == null) {
				System.out.println("ERRO: Produto não encontrado.");
				return;
			}
			int quantity = InputHelper.readInt("Quantidade do Produto: ");
			if (quantity <= 0) {
				System.out.println("ERRO: A quantidade é inválida. Tente novamente.");
				return;
			}
			int minQuantity = InputHelper.readInt("Quantidade MÍNIMA do Produto: ");
			if (minQuantity <= 0) {
				System.out.println("ERRO: A quantidade é inválida. Tente novamente.");
				return;
			}
			product.setQuantity(quantity);
			product.setMinQuantity(minQuantity);
			saveEntities(Product.class, products);
			return;
		case 3:
			return;
		default:
			System.out.println("ERRO: Opção inválida. Por favor, tente um número do menu.");
			return;
		}
		System.out.println();

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

		int quantity = InputHelper.readInt("Quantidade do Produto: ");
		if (quantity <= 0) {
			System.out.println("ERRO: A quantidade é inválida. Tente novamente.");
			return;
		}

		int minQuantity = InputHelper.readInt("Quantidade MÍNIMA do Produto: ");
		if (minQuantity <= 0) {
			System.out.println("ERRO: A quantidade é inválida. Tente novamente.");
			return;
		}

		if (addEntity(products, new Product(name, price, quantity, minQuantity))) {
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
			if (level == AccessLevel.OPERATOR) {
				System.out.println(product.getId() + " - " + product.getName() + " - R$ " + product.getPrice());
			} else {
				System.out.println(product.getId() + " - " + product.getName() + " - R$ " + product.getPrice() + " - "
						+ product.getQuantity() + " - " + product.getMinQuantity());
			}
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
		System.out.print("\nTítulos em aberto:");
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

	public void listPaidTitles() {
		boolean once = false;
		System.out.print("\nTítulos pagos:");
		for (Title title : titles) {
			if (title.isPaid()) {
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

		if (product.getQuantity() <= 0) {
			System.out.println("ERRO: O Produto não consta quantidade no sistema.");
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
			System.out.println("Produto comprado. ID do Título gerado: " + title.getId());
			product.decrementQuantity();
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
		makePayment(title);
	}

	public void makePayment(Title title) {
		if (title == null) {
			System.out.println("ERRO: Título não encontrado.");
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

	// """"""""IA""""""""
	public void showManagementNotifications() {
		boolean first = true;
		for (Product product : products) {
			if (product.getQuantity() < product.getMinQuantity()) {
				if (first) {
					first = false;
					System.out.println("\nAtenção! Os Produtos a seguir possuem menos unidades que o esperado:");
				}
				System.out.println(product.getId() + " - " + product.getName() + " - R$ " + product.getPrice() + " - "
						+ product.getQuantity() + " - " + product.getMinQuantity());
			}
		}
		if (!first) {
			System.out.println();
		}
	}

	public void updateClient() {
		int id = InputHelper.readInt("ID do Cliente: ");
		Client client = findEntityById(clients, id);
		if (client == null) {
			System.out.println("ERRO: Cliente não encontrado.");
			return;
		}

		System.out.print("""
				\n[Cliente]
				1. Alterar valores
				2. Deletar Cliente
				3. Voltar
				""");
		int choice = InputHelper.readInt("Escolha uma opção: ");

		switch (choice) {
		case 1:
			String name = InputHelper.readString("Nome do Cliente (deixe em branco para manter): ");
			if (name.isBlank()) {
				name = null;
			}
			String number = InputHelper.readString("Número de telefone do Cliente (deixe em branco para manter): ");
			if (number.isBlank()) {
				number = null;
			}

			if (name != null) {
				client.setName(name);
			}
			if (number != null) {
				client.setPhoneNumber(number);
			}
			saveEntities(Client.class, clients);
			break;
		case 2:
			client.setName("<REMOVIDO>");
			client.setPhoneNumber(null);
			saveEntities(Client.class, clients);
			break;
		case 3:
			return;
		default:
			System.out.println("ERRO: Opção inválida. Por favor, tente um número do menu.");
			break;
		}
	}

	public void runOccurrenceMenu() {
		while (true) {
			System.out.print("""
					\n[Ocorrências]
					1. Reportar ocorrência sobre processo interno
					2. Reportar ocorrência sobre o programa ERP
					3. Listar ocorrências
					4. Marcar ocorrência como resolvida
					5. Voltar
					""");
			int choice = InputHelper.readInt("Escolha uma opção: ");

			switch (choice) {
			case 1:
			case 2:
				String name = null;
				String text = "";
				if (InputHelper.readYesOrNo("Deseja se identificar com seu nome de usuário?")) {
					name = username;
				}
				if (choice == 1) {
					text += "[INTERNO] ";
				} else if (choice == 2) {
					text += "[ERP] ";
				}
				text += InputHelper.readString("Digite a ocorrência [ENTER PARA TERMINAR]: ");
				occurrences.add(new Occurrence(name, text, false));
				saveEntities(Occurrence.class, occurrences);
				break;
			case 3:
				if (level == AccessLevel.OPERATOR) {
					System.out.println("ERRO: Usuário não tem nível de acesso para acessar essa opção.");
					break;
				}
				if (occurrences.isEmpty()) {
					System.out.println("Nenhuma ocorrência encontrada.");
					break;
				}
				for (Occurrence o : occurrences) {
					System.out.println("\n* OCORRÊNCIA " + (o.isResolved() ? "RESOLVIDA" : "NÃO RESOLVIDA"));
					System.out.println("ID: " + o.getId());
					System.out.println("Usuário: " + ((o.getUsername() != null) ? o.getUsername() : "<ANÔNIMO>"));
					System.out.println("Texto: " + o.getText());
				}
				break;
			case 4:
				if (level == AccessLevel.OPERATOR) {
					System.out.println("ERRO: Usuário não tem nível de acesso para acessar essa opção.");
					break;
				}
				int id = InputHelper.readInt("ID da ocorrência: ");
				Occurrence o = findEntityById(occurrences, id);
				if (o == null) {
					System.out.println("ERRO: Ocorrência não encontrada.");
					break;
				}
				o.setResolved(true);
				if (saveEntities(Occurrence.class, occurrences)) {
					System.out.println("Ocorrência resolvida!");
				}
				break;
			case 5:
				return;
			default:
				System.out.println("ERRO: Opção inválida. Por favor, tente um número do menu.");
				break;
			}
		}

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

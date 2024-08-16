package com.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.app.entities.Client;
import com.app.entities.Occurrence;
import com.app.entities.Product;
import com.app.entities.Title;
import com.app.util.InputHelper;
import com.app.util.Secret;

public class Main {
	private static final String USERS_FILE = "data/users.dat";
	private static Map<String, User> users;

	private static class User implements Serializable {
		private static final long serialVersionUID = 1L;
		final String username;
		final byte[] salt;
		final byte[] hashedPassword;
		final boolean isAdmin;
		final Store.AccessLevel level;

		User(String username, byte[] salt, byte[] hashedPassword, boolean isAdmin, Store.AccessLevel level) {
			this.username = username;
			this.salt = salt;
			this.hashedPassword = hashedPassword;
			this.isAdmin = isAdmin;
			this.level = level;
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			users = new HashMap<>();
			String username = null;
			String password = null;
			loadUsers();

			if (users.isEmpty()) {
				System.out.println("Nenhum usuário encontrado. Registrando o administrador inicial...");
				while (true) {
					username = InputHelper.readString("Nome de usuário do administrador: ");
					if (username.isBlank()) {
						System.out.println("ERRO: O nome está em branco. Tente novamente.");
						continue;
					}
					password = InputHelper.readString("Senha do administrador: ");
					if (password.isBlank()) {
						System.out.println("ERRO: A senha está em branco. Tente novamente.");
						continue;
					}
					break;
				}
				registerUser(username, password, true, Store.AccessLevel.OWNER);
				System.out.println("Administrador registrado com sucesso!");
			}

			while (true) {
				runLogin(username, password);
				username = password = null;
			}
		} catch (NoSuchElementException e) {
			// Lida com o caso de CTRL-D (EOF) ser pressionado
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERRO: Erro desconhecido, comunique ao setor de TI.");
		} finally {
			encryptFiles();
			InputHelper.close();
			System.out.println("Saindo...");
		}
	}

	private static void runLogin(String username, String password) throws Exception {
		if (username == null || password == null) {
			username = InputHelper.readString("[LOGIN]\nNome do usuário: ");
			if (username.isBlank()) {
				throw new NoSuchElementException();
			}
			password = InputHelper.readString("Senha do usuário: ");
		}

		if (authenticateUser(username, password)) {
			User user = users.get(username);
			runMainMenu(user);
			System.out.println();
		} else {
			System.out.println("ERRO: Autenticação falhou. Usuário ou senha incorretos.\n");
		}
	}

	private static void runMainMenu(User user) throws Exception {
		// Adicionar hook para criptografar arquivos em uma saída inesperada
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					encryptFiles();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("ERRO: Tratamento dos arquivos na saída falhou, comunique ao setor de TI.");
				}
			}
		});

		System.out.println("\nOlá, " + user.username + "!");
		while (true) {
			System.out.print("""
					[MENU]
					1. Acessar loja
					2. Registrar novo usuário
					3. Voltar
					""");
			int choice = InputHelper.readInt("Escolha uma opção: ");

			switch (choice) {
			case 1:
				decryptFiles();
				runStore(user.level, user.username);
				break;
			case 2:
				if (!user.isAdmin) {
					System.out.println("Apenas administradores podem registrar novos usuários.");
					break;
				}
				String username = InputHelper.readString("Nome do usuário: ");
				if (username.isBlank()) {
					System.out.println("ERRO: O nome está em branco. Tente novamente.");
					break;
				}
				String password = InputHelper.readString("Senha do usuário: ");
				if (password.isBlank()) {
					System.out.println("ERRO: A senha está em branco. Tente novamente.");
					break;
				}
				int level = InputHelper.readInt("Nível de acesso (0 -> DONO, 1 -> GERENTE, 2 -> OPERADOR): ");
				if (level == 0) {
					registerUser(username, password, false, Store.AccessLevel.OWNER);
				} else if (level == 1) {
					registerUser(username, password, false, Store.AccessLevel.MANAGER);
				} else if (level == 2) {
					registerUser(username, password, false, Store.AccessLevel.OPERATOR);
				} else {
					System.out.println("ERRO: Nível de acesso inválido. Tente novamente.");
					break;
				}
				System.out.println("Novo usuário registrado com sucesso!");
				break;
			case 3:
				return;
			default:
				System.out.println("ERRO: Opção inválida. Por favor, tente um número do menu.");
			}
			System.out.println();
		}
	}

	private static void runStore(Store.AccessLevel level, String username) {
		Store store = null;
		try {
			store = new Store(level, username);
		} catch (IOException | NoSuchFieldException | NoSuchMethodException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			System.out.println("ERRO: Não foi possível carregar os arquivos, comunique ao setor de TI.");
			return;
		}

		System.out.println();
		switch (level) {
		case OPERATOR -> runOperatorMenu(store);
		case MANAGER -> runManagerMenu(store);
		case OWNER -> runOwnerMenu(store);
		default -> throw new IllegalArgumentException("Nível de acesso inválido: " + level);
		}
	}

	private static void runOperatorMenu(Store store) {
		while (true) {
			System.out.print("""
					[LOJA - OPERADOR]
					1. Listar Produtos
					2. Comprar Produto
					3. Efetuar Pagamento
					4. Listar Títulos em Aberto
					5. Voltar
					0. Reportar ocorrência
					""");
			int choice = InputHelper.readInt("Escolha uma opção: ");

			if (choice == 5) {
				return;
			}
			switch (choice) {
			case 1 -> store.listProducts();
			case 2 -> store.purchaseProduct();
			case 3 -> store.makePayment();
			case 4 -> store.listOutstandingTitles();
			case 0 -> store.runOccurrenceMenu();
			default -> System.out.println("ERRO: Opção inválida. Por favor, tente um número do menu.");
			}
			System.out.println();
		}
	}

	private static void runManagerMenu(Store store) {
		while (true) {
			System.out.print("""
					[LOJA - GERENTE]
					1. Adicionar Produto
					2. Listar Produtos
					3. Listar Títulos em aberto
					4. Listar Títulos pagos
					5. Listar clientes
					6. Atualizar cliente
					7. Voltar
					0. Ocorrências
					""");
			store.showManagementNotifications();
			int choice = InputHelper.readInt("Escolha uma opção: ");

			if (choice == 7) {
				return;
			}
			switch (choice) {
			case 1 -> store.addProduct();
			case 2 -> store.listProducts();
			case 3 -> store.listOutstandingTitles();
			case 4 -> store.listPaidTitles();
			case 5 -> store.listClients();
			case 6 -> store.updateClient();
			case 0 -> store.runOccurrenceMenu();
			default -> System.out.println("ERRO: Opção inválida. Por favor, tente um número do menu.");
			}
			System.out.println();
		}
	}

	private static void runOwnerMenu(Store store) {
		while (true) {
			System.out.print("""
					[LOJA - DONO]
					1. Adicionar Produto
					2. Listar Produtos
					3. Comprar Produto
					4. Efetuar Pagamento
					5. Listar Títulos em aberto
					6. Listar Títulos pagos
					7. Listar clientes
					8. Atualizar cliente
					9. Voltar
					0. Ocorrências
					""");
			store.showManagementNotifications();
			int choice = InputHelper.readInt("Escolha uma opção: ");

			if (choice == 9) {
				return;
			}
			switch (choice) {
			case 1 -> store.addProduct();
			case 2 -> store.listProducts();
			case 3 -> store.purchaseProduct();
			case 4 -> store.makePayment();
			case 5 -> store.listOutstandingTitles();
			case 6 -> store.listPaidTitles();
			case 7 -> store.listClients();
			case 8 -> store.updateClient();
			case 0 -> store.runOccurrenceMenu();
			default -> System.out.println("ERRO: Opção inválida. Por favor, tente um número do menu.");
			}
			System.out.println();
		}
	}

	private static User registerUser(String username, String password, boolean isAdmin, Store.AccessLevel level)
			throws Exception {
		if (users.containsKey(username)) {
			System.out.println("ERRO: Usuário já existe.");
			return null;
		}
		byte[] salt = Secret.generateSalt();
		byte[] hashedPassword = Secret.hashPassword(password, salt);
		User user = new User(username, salt, hashedPassword, isAdmin, level);
		users.put(username, user);
		saveUsers();
		return user;
	}

	private static boolean authenticateUser(String username, String password) throws Exception {
		User user = users.get(username);
		if (user == null) {
			return false;
		}
		byte[] hashedPassword = Secret.hashPassword(password, user.salt);
		return Arrays.equals(hashedPassword, user.hashedPassword);
	}

	@SuppressWarnings("unchecked")
	private static void loadUsers() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
			users = (Map<String, User>) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERRO: Não foi possível carregar os usuários, comunique ao setor de TI.");
		}
	}

	private static void saveUsers() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
			oos.writeObject(users);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERRO: Não foi possível salvar os usuários, comunique ao setor de TI.");
		}
	}

	private static void decryptFiles() throws Exception {
		Secret.decryptFile(Product.FILE + ".enc", Product.FILE);
		Secret.decryptFile(Title.FILE + ".enc", Title.FILE);
		Secret.decryptFile(Client.FILE + ".enc", Client.FILE);
		Secret.decryptFile(Occurrence.FILE + ".enc", Occurrence.FILE);
	}

	private static void encryptFiles() throws Exception {
		Secret.encryptFile(Product.FILE, Product.FILE + ".enc");
		Secret.encryptFile(Title.FILE, Title.FILE + ".enc");
		Secret.encryptFile(Client.FILE, Client.FILE + ".enc");
		Secret.encryptFile(Occurrence.FILE, Occurrence.FILE + ".enc");
	}
}

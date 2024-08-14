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
			User user = null;
			loadUsers();

			if (users.isEmpty()) {
				System.out.println("Nenhum usuário encontrado. Registrando o administrador inicial...");
				String username = InputHelper.readString("Nome de usuário do administrador: ");
				if (username.isBlank()) {
					System.out.println("ERRO: O nome está em branco. Tente novamente.");
					return;
				}
				String password = InputHelper.readString("Senha do administrador: ");
				if (password.isBlank()) {
					System.out.println("ERRO: A senha está em branco. Tente novamente.");
					return;
				}
				user = registerUser(username, password, true, Store.AccessLevel.OWNER);
				System.out.println("Administrador registrado com sucesso!");
				runMainMenu(user);
				return;
			}

			while (true) {
				String username = InputHelper.readString("[LOGIN]\nNome do usuário: ");
				if (username.isBlank()) {
					return;
				}
				String password = InputHelper.readString("Senha do usuário: ");

				if (authenticateUser(username, password)) {
					user = users.get(username);
					break;
				} else {
					System.out.println("Autenticação falhou. Usuário ou senha incorretos.\n");
				}
			}
			runMainMenu(user);
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

	private static void runMainMenu(User user) throws Exception {
		System.out.println("\nOlá, " + user.username + "!");
		while (true) {
			System.out.print("""
					[MENU]
					1. Acessar loja
					2. Registrar novo usuário
					3. Sair
					""");
			int choice = InputHelper.readInt("Escolha uma opção: ");

			switch (choice) {
			case 1:
				// Adicionar hook para criptografar arquivos em uma saída inesperada
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						try {
							encryptFiles();
						} catch (Exception e) {

							e.printStackTrace();
							System.out.println("Couldn't save score before terminating.");
						}
					}
				});
				decryptFiles();
				runStore(user.level);
				return;
			case 2:
				if (!user.isAdmin) {
					System.out.println("Apenas administradores podem registrar novos usuários.");
					break;
				}
				String username = InputHelper.readString("Nome do usuário: ");
				String password = InputHelper.readString("Senha do usuário: ");
				registerUser(username, password, false, Store.AccessLevel.OPERATOR);
				System.out.println("Novo usuário registrado com sucesso!\n");
				break;
			case 3:
				return;
			default:
				System.out.println("ERRO: Opção inválida. Por favor, tente um número do menu.");
			}
			System.out.println();
		}
	}

	private static void runStore(Store.AccessLevel level) {
		Store store = null;
		try {
			store = new Store(level);
		} catch (IOException | NoSuchFieldException | NoSuchMethodException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			System.out.println("ERRO: Não foi possível carregar os arquivos, comunique ao setor de TI.");
			return;
		}

		System.out.println();
		while (true) {
			System.out.print("""
					[LOJA]
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
	}

	private static void encryptFiles() throws Exception {
		Secret.encryptFile(Product.FILE, Product.FILE + ".enc");
		Secret.encryptFile(Title.FILE, Title.FILE + ".enc");
		Secret.encryptFile(Client.FILE, Client.FILE + ".enc");
	}
}

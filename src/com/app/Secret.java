package com.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Secret {
	private static final String FILE_ENC_ALGORITHM = "AES";
	private static final byte[] FILE_SHARED_KEY = "supersecret".getBytes();

	public static byte[] generateSalt() {
		byte[] salt = new byte[16];
		new SecureRandom().nextBytes(salt);
		return salt;
	}

	public static byte[] hashPassword(String password, byte[] salt) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(salt);
		return md.digest(password.getBytes("UTF-8"));
	}

	public static SecretKey getFileSharedKey() throws Exception {
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] key = sha.digest(FILE_SHARED_KEY);
		return new SecretKeySpec(key, 0, 16, FILE_ENC_ALGORITHM); // AES 128 bits
	}

	public static void encryptFile(String inputFile, String outputFile) throws Exception {
		File file = new File(inputFile);
		if (!file.exists()) {
			return;
		}

		SecretKey key = getFileSharedKey();
		Cipher cipher = Cipher.getInstance(FILE_ENC_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		try (FileInputStream fis = new FileInputStream(inputFile);
				FileOutputStream fos = new FileOutputStream(outputFile);
				CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				cos.write(buffer, 0, bytesRead);
			}
		}

		if (!file.delete()) {
			System.out.println("ERRO: Falha ao deletar o arquivo " + inputFile + ", comunique ao setor de TI.");
		}
	}

	public static void decryptFile(String inputFile, String outputFile) throws Exception {
		File file = new File(inputFile);
		if (!file.exists()) {
			return;
		}

		SecretKey key = getFileSharedKey();
		Cipher cipher = Cipher.getInstance(FILE_ENC_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		try (FileInputStream fis = new FileInputStream(inputFile);
				FileOutputStream fos = new FileOutputStream(outputFile);
				CipherInputStream cis = new CipherInputStream(fis, cipher)) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = cis.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesRead);
			}
		}
	}
}

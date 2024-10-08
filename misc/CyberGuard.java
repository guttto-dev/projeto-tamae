// DISCLAIMER: This is all fake

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CyberGuard {
    private static final String SANDBOX_DIR = "instance";
    private static final String DATA_FILE = SANDBOX_DIR + "/dados.txt";
    private static final String ENCRYPTED_FILE = SANDBOX_DIR + "/dados_encriptados.txt";

    private static Firewall firewall;
    private static IntrusionDetectionSystem ids;
    private static EncryptionService encryptionService;

    public static void main(String[] args) {
        try {
            System.out.println("""

   ______      __              ______                     __
  / ____/_  __/ /_  ___  _____/ ____/_  ______ __________/ /
 / /   / / / / __ \\/ _ \\/ ___/ / __/ / / / __ `/ ___/ __  /
/ /___/ /_/ / /_/ /  __/ /  / /_/ / /_/ / /_/ / /  / /_/ /
\\____/\\__, /_.___/\\___/_/   \\____/\\__,_/\\__,_/_/   \\__,_/
     /____/
""");
            enterSandbox(SANDBOX_DIR);

            firewall = new Firewall();
            ids = new IntrusionDetectionSystem();
            encryptionService = new EncryptionService();

            FlaskRunner flaskRunner = new FlaskRunner();
            flaskRunner.runFlaskServer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            encryptDataFile(DATA_FILE, ENCRYPTED_FILE);
        }
    }

    private static void enterSandbox(String dirName) {
        try {
            Path path = Paths.get(dirName);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            System.setProperty("user.dir", path.toAbsolutePath().toString());
            System.out.println("Entrando em Sandbox no diretório: " + path.toAbsolutePath().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void decryptDataFile(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                Files.write(path, "<dados importantes de clientes>".getBytes());
            }
            byte[] encryptedData = Files.readAllBytes(Paths.get(fileName));
            String decryptedData = encryptionService.decrypt(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void encryptDataFile(String inputFileName, String outputFileName) {
        try {
            String decryptedData = new String(Files.readAllBytes(Paths.get(inputFileName)));
            byte[] encryptedData = encryptionService.encrypt(decryptedData);
            Files.write(Paths.get(outputFileName), encryptedData);
            System.out.println("Protejendo dados na saída...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void routine() {
        decryptDataFile(DATA_FILE);

        firewall.isAllowed("192.168.1.101");
        firewall.isAllowed("192.168.1.100");

        ids.loginAttempt("op", false);
        ids.loginAttempt("op", false);
        ids.loginAttempt("op", false);
        ids.loginAttempt("op", false);
        ids.loginAttempt("man", true);
    }
}

class FlaskRunner {

    public void runFlaskServer() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("flask", "run");
        try {
            System.out.println("Iniciando aplicação em http://127.0.0.1:5000");
            Process process = processBuilder.start();
            CyberGuard.routine();
            int exitCode = process.waitFor();
            System.out.println("Aplicação finalizada com o código: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Firewall {
    private Map<String, Boolean> accessControlList;

    public Firewall() {
        System.out.println("[Firewall] ATIVADO");
        accessControlList = new HashMap<>();
        accessControlList.put("192.168.1.101", true);
        accessControlList.put("192.168.1.100", false);
    }

    public void isAllowed(String ip) {
        if (accessControlList.getOrDefault(ip, false)) {
            System.out.println("[Firewall] Acesso permitido para o IP: " + ip);
        } else {
            System.out.println("[Firewall] bloqueado para o IP: " + ip);
        }
    }
}

class IntrusionDetectionSystem {
    private Map<String, Integer> failedLoginAttempts;

    public IntrusionDetectionSystem() {
        System.out.println("[IntrusionDetectionSystem] ATIVADO");
        System.out.println("[IntrusionDetectionSystem] BUSCANDO AMEAÇAS");
        failedLoginAttempts = new HashMap<>();
    }

    public void loginAttempt(String username, boolean success) {
        if (!success) {
            failedLoginAttempts.put(username, failedLoginAttempts.getOrDefault(username, 0) + 1);
            if (failedLoginAttempts.get(username) > 3) {
                System.out.println("[IntrusionDetectionSystem] ALERTA: Tentativa de intrusão detectada para o usuário: " + username);
            } else {
                System.out.println("[IntrusionDetectionSystem] FALHA: Login para o usuário: " + username);
            }
        } else {
            System.out.println("[IntrusionDetectionSystem] INFO: Login bem-sucedido para o usuário: " + username);
            failedLoginAttempts.put(username, 0);
        }
    }
}

class EncryptionService {
    public int count = 0;

    public EncryptionService() {
        System.out.println("[EncryptionService] ATIVADO");
    }
    
    public byte[] encrypt(String data) {
        return data.getBytes();
    }

    public String decrypt(byte[] encryptedData) {
        System.out.println("[EncryptionService] ACESSANDO DADOS DE FORMA SEGURA #" + Integer.toString(count));
        ++count;
        return new String(encryptedData);
    }
}

// vi: ts=4 sw=4 et:

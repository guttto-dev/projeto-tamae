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
            addShutdownCleanup();
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void enterSandbox(String dirName) throws IOException {
        Path path = Paths.get(dirName);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        System.setProperty("user.dir", path.toAbsolutePath().toString());
        System.out.println("Entrando em Sandbox no diretório: " + path.toAbsolutePath().toString());
    }

    private static void decryptDataFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            Files.write(path, "<dados importantes de clientes>".getBytes());
        }
        byte[] encryptedData = Files.readAllBytes(Paths.get(fileName));
        String decryptedData = encryptionService.decrypt(encryptedData);
    }

    private static void encryptDataFile(String inputFileName, String outputFileName) throws IOException {
        String decryptedData = new String(Files.readAllBytes(Paths.get(inputFileName)));
        byte[] encryptedData = encryptionService.encrypt(decryptedData);
        Files.write(Paths.get(outputFileName), encryptedData);
        System.out.println("Protegendo dados na saída...");
    }

    private static void addShutdownCleanup() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    encryptDataFile(DATA_FILE, ENCRYPTED_FILE);
                    System.out.println("Saindo...");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }});
    }

    public static void routine() throws IOException {
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

    public void runFlaskServer() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("flask", "run");
        System.out.println("Iniciando aplicação em http://127.0.0.1:5000");
        Process process = processBuilder.start();
        CyberGuard.routine();
        int exitCode = process.waitFor();
        System.out.println("Aplicação finalizada com o código: " + exitCode);
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

import java.net.URI;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;

public class Main {
    public static JSONArray passwords = new JSONArray();
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = new URI("ws://vault-backend:8765");
        VaultClient client = new VaultClient();
        try {
            container.connectToServer(client, uri);
            Thread.sleep(1000);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n=== Menú Principal ===");
            System.out.println("1. Autenticarse a un vault");
            System.out.println("2. Leer vault");
            System.out.println("3. Guardar nueva contraseña");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese la contraseña del vault: ");
                    String password = scanner.nextLine();
                    String authMessage = """
                        { "action": "auth", "password": "%s" }
                    """.formatted(password);
                    client.sendMessage(authMessage);
                    Thread.sleep(1000);
                    break;
                case 2:
                    String readMessage = """
                        { "action": "read" }
                    """;
                    client.sendMessage(readMessage);
                    for (int i = 0; i < passwords.length(); i++) {
                        JSONObject entry = passwords.getJSONObject(i);
                        System.out.println(entry.getString("service") + ": " + entry.getString("password"));
                    }
                    Thread.sleep(3000);
                    break;
                case 3:
                    System.out.print("Ingrese el nombre del servicio: ");
                    String service = scanner.nextLine();
                    System.out.print("Ingrese la contraseña a guardar: ");
                    String newPassword = scanner.nextLine();
                    JSONObject newEntry = new JSONObject()
                        .put("service", service)
                        .put("password", newPassword);

                    passwords.put(newEntry);

                    String saveMessage = new JSONObject()
                        .put("action", "save")
                        .put("data", passwords)
                        .toString();
                    client.sendMessage(saveMessage);
                    Thread.sleep(1000);
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    client.sendMessage("{ \"action\": \"end\" }");
                    break;
                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
                    break;
            }
        }
        scanner.close();
    }
}
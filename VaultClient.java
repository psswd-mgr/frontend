
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

@ClientEndpoint
public class VaultClient {

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Conectado al servidor WebSocket.");
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            if (json.has("data")) {
                JSONArray dataArray = json.getJSONArray("data");
                Main.passwords = dataArray;
            } else {
                System.out.println(message);
            }
        } catch (Exception e) {
            System.out.println("Error parseando mensaje: " + e.getMessage());
            System.out.println("Mensaje crudo: " + message);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Conexión cerrada: " + closeReason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
    }

    // Método para enviar mensajes
    public void sendMessage(String message) throws Exception {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(message);
        } else {
            System.out.println("No hay sesión activa.");
        }
    }
}

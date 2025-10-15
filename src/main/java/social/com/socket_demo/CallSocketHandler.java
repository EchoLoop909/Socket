package social.com.socket_demo;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CallSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String userId = (query != null && query.contains("="))
                ? query.split("=")[1]
                : session.getId();

        sessions.put(userId, session);
        System.out.println("✅ User connected: " + userId);

        // Gửi về cho client biết ID của mình (để hiển thị)
        JSONObject assigned = new JSONObject();
        assigned.put("type", "id-assigned");
        assigned.put("userId", userId);
        session.sendMessage(new TextMessage(assigned.toString()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject msg = new JSONObject(message.getPayload());
        String type = msg.optString("type");
        String toUser = msg.optString("to");

        if (toUser == null || toUser.isEmpty()) return;

        WebSocketSession receiver = sessions.get(toUser);
        if (receiver != null && receiver.isOpen()) {
            receiver.sendMessage(new TextMessage(message.getPayload()));
            System.out.println("➡️ Forwarded [" + type + "] to " + toUser);
        } else {
            System.out.println("⚠️ Receiver not connected: " + toUser);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.values().remove(session);
        System.out.println("❌ User disconnected");
    }
}

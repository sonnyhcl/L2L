package vesselpart.awsiot;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

public class WebSocketMessage implements Message<String> {
    private  String payload;
    private  MessageHeaders messageHeaders;

    public WebSocketMessage(String payload , MessageHeaders messageHeaders) {
        this.payload = payload;
        this.messageHeaders = messageHeaders;
    }

    @Override
    public String getPayload() {
        return this.payload;
    }

    @Override
    public MessageHeaders getHeaders() {
        return this.messageHeaders;
    }
}

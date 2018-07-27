package lvc.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;


@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
@Order(1)
public class WebSocketMessageBrokerConfig extends AbstractWebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic" , "/queue"); //Use the built-in , message broker for subscribtions and broadscasting
        registry.setApplicationDestinationPrefixes("/app"); //STOMP messages whose destination header begins with "/app" are routed to @MessageMapping methods in @Controller classes.
       // registry.setUserDestinationPrefix("/user/");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/sps").setAllowedOrigins("*").withSockJS(); //the HTTP URL for the endpoint to which a WebSocket (or SockJS) client will need to connect to for the WebSocket handshake.
    }


}

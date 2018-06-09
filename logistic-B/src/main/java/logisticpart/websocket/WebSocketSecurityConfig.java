package logisticpart.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpTypeMatchers(SimpMessageType.CONNECT , SimpMessageType.CONNECT_ACK, SimpMessageType.DISCONNECT, SimpMessageType.UNSUBSCRIBE , SimpMessageType.SUBSCRIBE).permitAll()
                .simpDestMatchers("/app/**").permitAll()
                .simpDestMatchers("/user/**").authenticated()
                .simpSubscribeDestMatchers("/topic/**", "/queue/**").permitAll()
                .anyMessage().denyAll();

    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
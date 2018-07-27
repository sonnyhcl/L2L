package supplierA.websocket;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.security.Principal;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
@Order(1)
public class WebSocketMessageBrokerConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Autowired
    private IdentityService identityService;

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

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptorAdapter() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor =
//                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//                String username = accessor.getNativeHeader("username").get(0);
//                String password = accessor.getNativeHeader("password").get(0);
//                User user = identityService.createUserQuery().userId(username).singleResult();
//                System.out.println("identityService :"+identityService+" :"+username);
//                if (StompCommand.CONNECT.equals(accessor.getCommand()) && user != null) {
//                    if(user.getPassword().equals(password)){
//                       Principal principal = new MyPrincipal(user);
//                       accessor.setUser(principal);
//                    }else{
//                        return null;
//                    }
//
//                }
//                return null ;
//            }
//        });
//    }

    class MyPrincipal implements Principal{

        private User user;

        public MyPrincipal(User user) {
            this.user = user;
        }

        @Override
        public String getName() {
            return String.valueOf(user.getId());
        }

    }


}

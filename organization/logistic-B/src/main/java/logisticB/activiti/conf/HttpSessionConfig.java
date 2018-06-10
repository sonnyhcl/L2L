package logisticB.activiti.conf;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

//@EnableRedisHttpSession
public class HttpSessionConfig {
//    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

}

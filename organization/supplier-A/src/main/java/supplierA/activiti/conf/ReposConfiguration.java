package supplierA.activiti.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import supplierA.repos.CommonRepository;


import javax.inject.Inject;

@Configuration
@SuppressWarnings("all")
public class ReposConfiguration {
    private  final static Logger logger = LoggerFactory.getLogger(ReposConfiguration.class);
    @Inject
    private Environment environment;
//    @Bean
    public CommonRepository commonRepository(){
        CommonRepository commonRepository = new CommonRepository(environment);
        return  commonRepository;
    }
}

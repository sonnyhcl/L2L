package iot.conf;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import com.fasterxml.jackson.databind.ObjectMapper;
import iot.service.AWSClientService;
import iot.service.EventHandler;
import iot.service.EventSubscriber;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import iot.domain.VesselDevice;

import java.io.IOException;

@Configuration
@SuppressWarnings("all")
public class AppConfig {
    private static Logger logger = Logger.getLogger(AppConfig.class);

    private AWSIotQos topicQos = AWSIotQos.QOS0;

    @Autowired
    private Environment environment;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public AWSClientService awsClientService() throws AWSIotException, InterruptedException, IOException {
      String keysCsv = environment.getRequiredProperty("awsiot.keys");
      AWSClientService awsClientService = new AWSClientService(keysCsv);
      return awsClientService;
    }

}

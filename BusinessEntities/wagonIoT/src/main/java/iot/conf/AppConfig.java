package iot.conf;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import iot.service.pubSub.WagonSubscriber;
import iot.service.shadow.WagonDevice;

@Configuration
public class AppConfig {
    private static Logger logger = Logger.getLogger(AppConfig.class);
    private static final AWSIotQos topicQos = AWSIotQos.QOS0;

    @Autowired
    private Environment environment;


    @Bean
    public AWSIotMqttClient awsIotMqttClient() throws AWSIotException {
        AWSIotMqttClient awsIotMqttClient = null;
        logger.debug("--awsIotMqttClient--");
        String clientEndpoint = environment.getProperty("awsiot.clientEndpoint");
        String clientId = environment.getProperty("awsiot.clientId");
        String certificateFile = environment.getProperty("awsiot.certificate");
        String privateKeyFile = environment.getProperty("awsiot.privateKey");
        String algorithm = environment.getProperty("keyAlgorithm");
        String awsAccessKeyId = environment.getProperty("awsAccessKeyId");
        String awsSecretAccessKey = environment.getProperty("awsSecretAccessKey");
        String sessionToken = environment.getProperty("sessionToken");
        if (awsIotMqttClient == null && certificateFile != null && privateKeyFile != null) {
            KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile, algorithm);
            awsIotMqttClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        }
        if (awsIotMqttClient == null) {
            if (awsAccessKeyId != null && awsSecretAccessKey != null) {
                awsIotMqttClient = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey,
                        sessionToken);
            }
        }

        if (awsIotMqttClient == null) {
            throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");
        }
        //create awsIotClient and connect to aws console
        WagonDevice device = wagonDevice();
        awsIotMqttClient.setWillMessage(new AWSIotMessage("client/disconnect", AWSIotQos.QOS0, awsIotMqttClient.getClientId()));
        device.setReportInterval(0);
        awsIotMqttClient.attach(device);
        awsIotMqttClient.connect();
        // Delete existing document if any
        device.delete();
        AWSIotConnectionStatus status = AWSIotConnectionStatus.DISCONNECTED;
        logger.debug("--aws mqtt client--");

        return awsIotMqttClient;
    }

    @Bean
    public WagonDevice wagonDevice(){
        logger.debug("--wagonDevice--");

        String thingName = environment.getProperty("awsiot.thingName");

        return new WagonDevice(thingName);
    }

    @Bean
    public WagonSubscriber wagonSubscriber() throws AWSIotException {
        logger.debug("--wagonSubscriber--");
        //subscribe to activiti topic
        String activitiTopic = environment.getProperty("wagon.topic.activiti");
        WagonSubscriber wagonSubscriber = new WagonSubscriber(activitiTopic, topicQos);
        awsIotMqttClient().subscribe(wagonSubscriber);
        logger.debug("--wagon Subscriber--");

        return  wagonSubscriber;
    }

}

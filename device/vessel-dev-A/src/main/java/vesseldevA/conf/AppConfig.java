package vesseldevA.conf;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import vesseldevA.services.pubSub.DelayIotMessage;
import vesseldevA.services.pubSub.VesselSubscriber;
import vesseldevA.services.shadow.VesselDevice;

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
    public AWSIotMqttClient awsIotMqttClient() throws AWSIotException {
        logger.debug("new awsIotMqttClientss");
        AWSIotMqttClient awsIotMqttClient = null;
//        System.out.println("AppConfig" + environment);
        String clientEndpoint = environment.getProperty("awsiot.clientEndpoint");
        logger.debug(clientEndpoint);
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
        VesselDevice device = vesselDevice();

        awsIotMqttClient.setWillMessage(new AWSIotMessage("client/disconnect", AWSIotQos.QOS0, awsIotMqttClient.getClientId()));
        device.setReportInterval(0);
        awsIotMqttClient.attach(device);
        awsIotMqttClient.connect();
        // Delete existing document if any
        device.delete();
        AWSIotConnectionStatus status = AWSIotConnectionStatus.DISCONNECTED;

        return awsIotMqttClient;
    }

    @Bean
    public VesselDevice vesselDevice(){

        logger.debug("new vesselDevice");

        String thingName = environment.getProperty("awsiot.thingName");

        return new VesselDevice(thingName);
    }

    @Bean(autowire = Autowire.BY_NAME,value = "vesselSubcriber")
    public VesselSubscriber vesselSubscriber() throws AWSIotException {
        logger.debug("new vesselSubscriber");
        //subscribe to activiti topic
        String activitiTopic = environment.getProperty("vessel.topic.activiti");
        VesselSubscriber vesselSubscriber = new VesselSubscriber(activitiTopic, topicQos);
        awsIotMqttClient().subscribe(vesselSubscriber , false);
        return  vesselSubscriber;
    }

    @Bean(autowire = Autowire.BY_NAME,value = "delayIotMessage")
    public DelayIotMessage delayIotMessage() throws AWSIotException {
        logger.debug("new vesselSubscriber");
        //subscribe to activiti topic
        String activitiTopic = environment.getProperty("vessel.topic.delay");
        DelayIotMessage delayIotMessage = new DelayIotMessage(activitiTopic, topicQos);
        awsIotMqttClient().subscribe(delayIotMessage , false);
        return  delayIotMessage;
    }
}

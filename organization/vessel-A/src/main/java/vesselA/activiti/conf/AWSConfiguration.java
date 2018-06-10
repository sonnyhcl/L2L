package vesselA.activiti.conf;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import vesselA.awsiot.ActivitiSubscriber;
import vesselA.awsiot.MessageHandler;

import javax.inject.Inject;

@Configuration
public class AWSConfiguration {
    @Inject
    private Environment environment;
    @Bean
    public AWSIotMqttClient awsIotMqttClient() throws AWSIotException, InterruptedException {
        AWSIotMqttClient awsIotMqttClient = null;
//        System.out.println("AppConfig" + environment);
        String clientEndpoint = environment.getProperty("awsiot.clientEndpoint");
        String clientId = environment.getProperty("awsiot.clientId");
        String certificateFile = environment.getProperty("awsiot.certificateFile");
        String privateKeyFile = environment.getProperty("awsiot.privateKeyFile");
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

        System.out.println(environment.getProperty("awsiot.clientEndpoint"));
        awsIotMqttClient.connect();
        ActivitiSubscriber activitiSubscriber = new ActivitiSubscriber("$aws/things/vessel/#" , AWSIotQos.QOS0);
        activitiSubscriber.setMessageHandler(messageHandler());
        awsIotMqttClient.subscribe(activitiSubscriber);
        //TODO : add other subscribers into MqttClient
        ActivitiSubscriber activitiSubscriber1 = new ActivitiSubscriber("activiti/vessel/#" , AWSIotQos.QOS0);
        activitiSubscriber1.setMessageHandler(messageHandler());
        awsIotMqttClient.subscribe(activitiSubscriber1);


//        //subscribe to activiti topic
//        ActivitiSubscriber t = new ActivitiSubscriber("test/hello",  AWSIotQos.QOS0);
//        awsIotMqttClient.subscribe(t);

        return  awsIotMqttClient;
    }

    @Bean
    public MessageHandler messageHandler() throws AWSIotException, InterruptedException {
        MessageHandler mh = new MessageHandler();
        return mh;
    }



}

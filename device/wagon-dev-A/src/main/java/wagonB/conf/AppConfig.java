package wagonB.conf;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import wagonB.services.pubSub.VesselSubscriber;
import wagonB.services.shadow.MessageHandler;
import wagonB.services.shadow.VesselDevice;

import java.io.IOException;

@Configuration
@PropertySource("classpath:vessel.properties")
public class AppConfig {
    private static Logger logger = Logger.getLogger(AppConfig.class);

    private static final String activitiTopic = "activiti/#";
    private static final AWSIotQos topicQos = AWSIotQos.QOS0;

    @Autowired
    private Environment environment;

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public VesselStateCache vesselStateCache() throws IOException {
        String vesselTrajectoryFilename = environment.getProperty("vessel.trajectoryFileName");
        String vesselId = environment.getProperty("vessel.id");
        String portsFileName = environment.getProperty("ports.fileName");

        return new VesselStateCache(vesselTrajectoryFilename , vesselId , portsFileName);
    }


    @Bean
    public AWSIotMqttClient awsIotMqttClient() throws AWSIotException {
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
        //create awsIotClient and connect to aws console
        VesselDevice device = vesselDevice();
        awsIotMqttClient.setWillMessage(new AWSIotMessage("client/disconnect", AWSIotQos.QOS0, awsIotMqttClient.getClientId()));
        device.setReportInterval(0);
        awsIotMqttClient.attach(device);
        awsIotMqttClient.connect();
        // Delete existing document if any
        device.delete();
        AWSIotConnectionStatus status = AWSIotConnectionStatus.DISCONNECTED;

        //subscribe to activiti topic
        VesselSubscriber vesselSubscriber = new VesselSubscriber(activitiTopic, topicQos);
        awsIotMqttClient.subscribe(vesselSubscriber);

//        while (true) {
//            String payload = "hello~ "+ DateUtil.date2str(new Date());
//            AWSIotMessage pub = new VesselPublisher("test/hello", topicQos, payload);
//            awsIotMqttClient.publish(pub);
//            logger.info("send hello .");
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                break;
//            }
//        }

        return awsIotMqttClient;
    }

    @Bean
    public VesselDevice vesselDevice(){

        String thingName = environment.getProperty("awsiot.thingName");
        return new VesselDevice(thingName);
    }

    @Bean
    public MessageHandler messageHandler(){
        return new MessageHandler();
    }
}

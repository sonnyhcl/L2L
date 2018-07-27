package iot.service;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import iot.domain.AwsKey;
import iot.domain.IoTClient;
import iot.domain.VesselDevice;
import iot.util.CsvUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class AWSClientService {
    private static final Logger logger = LoggerFactory.getLogger(AWSClientService.class);
    private List<IoTClient> iotClients = new ArrayList<IoTClient>();

    public  AWSClientService(String keysCsv) throws IOException, AWSIotException, InterruptedException {
        String dataPath = this.getClass().getResource("/").getPath()+ "data/";
        List<AwsKey> awsKeys = CsvUtil.readAwsKeys(dataPath+keysCsv);
        logger.debug("---AwsClientService---"+awsKeys.toString());
        for(int i = 0; i < awsKeys.size();i++){
            AwsKey awsKey = awsKeys.get(i);
            IoTClient ioTClient = new IoTClient();
            String vid = awsKey.getVid();
            String thingName = 'V'+vid;
            ioTClient.setVid(vid);
            VesselDevice device = createVesselDevice(thingName);
            device.updateVid(vid);
            AWSIotMqttClient awsIotMqttClient = createMqttClient(awsKey , device);
            ioTClient.setVesselDevice(device);
            ioTClient.setAwsIotMqttClient(awsIotMqttClient);
            ioTClient.setDefaultTopic(awsKey.getDefaultTopic());
            ioTClient.setCustomTopic(awsKey.getCustomTopic());
            iotClients.add(ioTClient);

        }
    }


    public IoTClient findDeviceClient(String vid){
        for(IoTClient ioTClient : iotClients){
            if(vid.equals(ioTClient.getVid())){
                return ioTClient;
            }
        }
        return null;
    }

    private AWSIotMqttClient createMqttClient(AwsKey awsKey , VesselDevice device) throws AWSIotException {
        AWSIotMqttClient awsIotMqttClient = null;
        String clientEndpoint = awsKey.getClientEndpoint();
        logger.debug(clientEndpoint);
        String clientId = 'V'+awsKey.getVid();
        String certificateFile = awsKey.getLocalPath()+"/certificate.pem.crt";
        String privateKeyFile = awsKey.getLocalPath()+"/private.pem.key";
        String algorithm = null;
        String awsAccessKeyId = null;
        String awsSecretAccessKey = null;
        String sessionToken = null;
        if (awsIotMqttClient == null && certificateFile != null && privateKeyFile != null) {
            SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile, algorithm);
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

        awsIotMqttClient.setWillMessage(new AWSIotMessage("client/disconnect", AWSIotQos.QOS0, awsIotMqttClient.getClientId()));
        device.setReportInterval(0);
        awsIotMqttClient.attach(device);
        awsIotMqttClient.connect();
        // Delete existing document if any
        device.delete();

        return awsIotMqttClient;
    }

    private VesselDevice createVesselDevice(String thingName){
        logger.debug("new vesselDevice"+thingName);
        return new VesselDevice(thingName);
    }

}

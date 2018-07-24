package vesseldevA.repos;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import jxl.read.biff.BiffException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vesseldevA.domain.AwsKey;
import vesseldevA.domain.DeviceClient;
import vesseldevA.services.shadow.VesselDevice;
import vesseldevA.util.CsvUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class AWSClientService {
    private static final Logger logger = LoggerFactory.getLogger(AWSClientService.class);
    private List<DeviceClient> awsClients = new ArrayList<DeviceClient>();

    public  AWSClientService(@Value("${awsiot.keys}") String keysCsv) throws IOException, BiffException, AWSIotException, InterruptedException {
        String dataPath = this.getClass().getResource("/").getPath()+"data/";
        List<AwsKey> awsKeys = CsvUtil.readAwsKeys(dataPath+keysCsv);
        logger.debug("---AwsClientService---"+awsKeys.toString());
        for(int i = 0; i < awsKeys.size();i++){
            AwsKey awsKey = awsKeys.get(i);
            DeviceClient deviceClient = new DeviceClient();
            String vid = awsKey.getVid();
            String thingName = 'V'+vid;
            deviceClient.setVid(vid);
            VesselDevice device = createVesselDevice(thingName);
            device.updateVid(vid);
            AWSIotMqttClient awsIotMqttClient = createMqttClient(awsKey , device);
            deviceClient.setVesselDevice(device);
            deviceClient.setAwsIotMqttClient(awsIotMqttClient);
            deviceClient.setAwsUpdateShadowTopic("$aws/things/"+thingName+"/shadow/update");
            awsClients.add(deviceClient);

        }
    }


    public DeviceClient findDeviceClient(String vid){
        for(DeviceClient deviceClient : awsClients){
            if(vid.equals(deviceClient.getVid())){
                return deviceClient;
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
        AWSIotConnectionStatus status = AWSIotConnectionStatus.DISCONNECTED;

        return awsIotMqttClient;
    }

    private VesselDevice createVesselDevice(String thingName){
        logger.debug("new vesselDevice"+thingName);
        return new VesselDevice(thingName);
    }

}

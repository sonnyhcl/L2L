package iot.domain;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import iot.services.shadow.VesselDevice;
import lombok.Data;

@Data
public class IoTClient {
    private String vid;
    private VesselDevice vesselDevice;
    private AWSIotMqttClient awsIotMqttClient;
    private String awsUpdateShadowTopic;
}

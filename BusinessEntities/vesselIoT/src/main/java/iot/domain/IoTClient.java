package iot.domain;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import lombok.Data;

@Data
public class IoTClient {
    private String vid;
    private VesselDevice vesselDevice;
    private AWSIotMqttClient awsIotMqttClient;
    private String defaultTopic;
    private String customTopic;

    public String getUpdateStatusTopic(){
        return customTopic+"status";
    }

    public String getUpdateAWSShadowTopic(){
        return defaultTopic+"update";
    }
}

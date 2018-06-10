package wagonB.services.shadow;

import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotDeviceErrorCode;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.shadow.AwsIotDeviceCommand;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import wagonB.domain.Port;
import wagonB.domain.VesselState;

import java.io.IOException;
import java.util.List;

@JsonFilter("wantedProperties")
public class VesselDevice extends AWSIotDevice {
    private Logger logger = Logger.getLogger(this.getClass());

    public VesselDevice(String thingName) {
        super(thingName);
    }

//    @AWSIotDeviceProperty
    private String vid;

//    @AWSIotDeviceProperty
    private VesselState vesselState;

//    @AWSIotDeviceProperty
    private List<Port> ports;

//    @AWSIotDeviceProperty
    private int positionIndex;

//    @AWSIotDeviceProperty
    private String simuStartDateStr;

//    @AWSIotDeviceProperty
    private int nextPortIndex;

//    @AWSIotDeviceProperty
    private String status;

    public String getVid() {
        return vid;
    }

//    @Override
//    public void onCommandAck(AWSIotMessage response) {
//        if (response != null && response.getTopic() != null) {
//            logger.debug("Unknown command received from topic " + response.getTopic()+response.toString());
//        }
//    }

    public void updateVid(String vid) {
        this.vid = vid;
    }

    public VesselState getVesselState() {
        return vesselState;
    }

    public void updateVesselState(VesselState vesselState) {
        this.vesselState = vesselState;
    }
    public void setVesselState(VesselState vesselState) {
//        this.vesselState = vesselState;
    }


    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
//        this.ports = ports;
    }
    public void updatePorts(List<Port> ports){
        this.ports = ports;
    }
    public int getPositionIndex() {
        return positionIndex;
    }

    public void updatePositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    public String getSimuStartDateStr() {
        return simuStartDateStr;
    }

    public void updateSimuStartDateStr(String simuStartDateStr) {
        this.simuStartDateStr = simuStartDateStr;
    }

    public int getNextPortIndex() {
        return nextPortIndex;
    }

    public void updateNextPortIndex(int nextPortIndex) {
        this.nextPortIndex = nextPortIndex;
    }

    public String getStatus() {
        return status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VesselDevice{" +
                "vid='" + vid + '\'' +
                ", vesselState=" + vesselState +
                ", ports=" + ports +
                ", positionIndex=" + positionIndex +
                ", simuStartDateStr='" + simuStartDateStr + '\'' +
                ", nextPortIndex=" + nextPortIndex +
                ", status='" + status + '\'' +
                '}';
    }

    private AwsIotDeviceCommand getPendingCommand(AWSIotMessage message) {
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = message.getStringPayload();
        if (payload == null) {
            return null;
        } else {
            try {
                JsonNode jsonNode = objectMapper.readTree(payload);
                if (!jsonNode.isObject()) {
                    return null;
                } else {
                    JsonNode node = jsonNode.get("clientToken");
                    if (node == null) {
                        return null;
                    } else {
                        String commandId = node.textValue();
                        AwsIotDeviceCommand command = (AwsIotDeviceCommand)this.getCommandManager().getPendingCommands().remove(commandId);
                        if (command == null) {
                            return null;
                        } else {
                            node = jsonNode.get("code");
                            if (node != null) {
                                command.setErrorCode(AWSIotDeviceErrorCode.valueOf(node.longValue()));
                            }

                            node = jsonNode.get("message");
                            if (node != null) {
                                command.setErrorMessage(node.textValue());
                            }

                            return command;
                        }
                    }
                }
            } catch (IOException var7) {
                return null;
            }
        }
    }

}

package vessel.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import vessel.domain.Port;
import vessel.domain.VesselState;
import vessel.services.shadow.VesselDevice;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapperUtil {

    public static VesselDevice toVesselDevice(JsonNode rootNode){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode portsJNode = rootNode.get("vesselShadow").get("ports");
        JsonNode vesselStateJNode = rootNode.get("vesselShadow").get("vesselState");
        int positionIndex = rootNode.get("vesselShadow").findValue("positionIndex").asInt();
        String simuStartDate = rootNode.get("vesselShadow").findValue("simuStartDateStr").asText();
        int nextPortIndex = rootNode.get("vesselShadow").findValue("nextPortIndex").asInt();


        VesselDevice vesselDevice = (VesselDevice)SpringUtil.getBean("vessel");
        try {
            List<Port> ports = objectMapper.readValue(portsJNode.toString() , List.class);
            VesselState vesselState = objectMapper.readValue(vesselStateJNode.toString() , VesselState.class);
            vesselDevice.updateVesselState(vesselState);
            vesselDevice.updatePorts(ports);
            vesselDevice.updatePositionIndex(positionIndex);
            vesselDevice.updateSimuStartDateStr(simuStartDate);
            vesselDevice.updateNextPortIndex(nextPortIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vesselDevice;
    }

    @Test
    public void test(){
        List<VesselState> list = new ArrayList<VesselState>();
        List<VesselState> list1 = new ArrayList<VesselState>();

        VesselState vesselState = new VesselState();
        vesselState.setDate("2017-10-23 23:34：12");
        list.add(vesselState);
        list.add(vesselState);
        list1.add(vesselState);
        System.out.println(list.toString());
        VesselState getVes = list.get(0);
        System.out.println(getVes == list.get(0));
        System.out.println((getVes == vesselState));

        List<VesselState> list2 = new ArrayList<VesselState>();
        VesselState vesselState1 = vesselState.deepCopy();
        list2.add(vesselState1);

        vesselState.setDate("modified1");
        System.out.println("list : "+list.toString());
        System.out.println("list1 : "+list1.toString());
        System.out.println("list2 : "+list2.toString());


    }
}

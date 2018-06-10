package vesselA.vessel.domain;

import java.util.List;

public class VesselShadow {

    public VesselShadow(String vid  , VesselState vesselState, List<Port> ports) {
        this.vid = vid;
        this.vesselState = vesselState;
        this.ports = ports;
    }

    public VesselShadow() {
        super();
    }

    //vessel shadow data
    private VesselState vesselState;

    private List<Port> ports;
    private  int positionIndex = 0;
    private String vid;

    //process data
    private String vpid;

    private String status;

    private String simuStartDateStr;

    private int nextPortIndex;

    private String wpid;

    public VesselState getVesselState() {
        return vesselState;
    }

    public void setVesselState(VesselState vesselState) {
        this.vesselState = vesselState;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }

    public String getVpid() {
        return vpid;
    }

    public void setVpid(String vpid) {
        this.vpid = vpid;
    }

    public int getPositionIndex() {
        return positionIndex;
    }

    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSimuStartDateStr() {
        return simuStartDateStr;
    }

    public void setSimuStartDateStr(String simuStartDateStr) {
        this.simuStartDateStr = simuStartDateStr;
    }

    public int getNextPortIndex() {
        return nextPortIndex;
    }

    public void setNextPortIndex(int nextPortIndex) {
        this.nextPortIndex = nextPortIndex;
    }

    public String getWpid() {
        return wpid;
    }

    public void setWpid(String wpid) {
        this.wpid = wpid;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }



    @Override
    public String toString() {
        return "VesselShadow{" +
                "vesselState=" + vesselState +
                ", ports=" + ports +
                ", positionIndex=" + positionIndex +
                ", vid='" + vid + '\'' +
                ", vpid='" + vpid + '\'' +
                ", status='" + status + '\'' +
                ", simuStartDateStr='" + simuStartDateStr + '\'' +
                ", nextPortIndex='" + nextPortIndex + '\'' +
                ", wpid='" + wpid + '\'' +
                '}';
    }
}

package wagonB.conf;



import org.apache.log4j.Logger;
import wagonB.domain.Port;
import wagonB.domain.VesselState;
import wagonB.util.ExcelUtil;
import wagonB.util.UtilTest;

import java.io.IOException;
import java.util.List;

public class VesselStateCache {
    private  static Logger logger = Logger.getLogger(VesselStateCache.class);

    private  List<VesselState> vesselStates;
    private List<Port> ports;
    private long defautDelayHour; //默认港口停留时间
    private long zoomInVal; // 如果按1000的压缩比，停留一小时只需要3.6s
    private  String  vid;

    public VesselStateCache(String vesselTrajectoryFilename , String vesselId , String portsFileName) throws IOException {
        //将船的轨迹数据存入ArrayList
        logger.info(vesselTrajectoryFilename);
        String path = UtilTest.getCurrentPath()+vesselTrajectoryFilename;
        vesselStates = ExcelUtil.readVesselStates(path, vesselId);
        ports = ExcelUtil.readPorts(UtilTest.getCurrentPath()+portsFileName , vesselId);
     //   logger.info(ports.toString());
        this.vid = vesselId;

    }


    public List<VesselState> getVesselStates() {
        return vesselStates;
    }

    public void setVesselStates(List<VesselState> vesselStates) {
        this.vesselStates = vesselStates;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }

    public long getDefautDelayHour() {
        return defautDelayHour;
    }

    public void setDefautDelayHour(long defautDelayHour) {
        this.defautDelayHour = defautDelayHour;
    }

    public long getZoomInVal() {
        return zoomInVal;
    }

    public void setZoomInVal(long zoomInVal) {
        this.zoomInVal = zoomInVal;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }
}

package iot.domain;

import com.amazonaws.services.iot.client.AWSIotDevice;
import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.ToString;
import org.apache.log4j.Logger;
import iot.domain.Destination;
import iot.domain.VesselState;

import java.util.List;

@ToString
@JsonFilter("wantedProperties")
public class VesselDevice extends AWSIotDevice {
    private Logger logger = Logger.getLogger(this.getClass());

    public VesselDevice(String thingName) {
        super(thingName);
    }

    private String vid;

    private double longitude;

    private double latitude;

    private double velocity;

    private  String timeStamp;

    private String startTime;

    private int stepIndex;

    private String status;
    private List<Destination> destinations;


    public String getVid() {
        return vid;
    }
    public void updateVid(String vid) {
        this.vid = vid;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }
    public void updateDestinations(List<Destination> destinations){
        this.destinations = destinations;
    }

    public String getStatus() {
        return status;
    }
    public void updateStatus(String status) {
        this.status = status;
    }

    public double getLongitude() {
        return longitude;
    }

    public void updateLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void updateLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getVelocity() {
        return velocity;
    }

    public void updateVelocity(double velocity) {
        this.velocity = velocity;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void updateTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStartTime() {
        return startTime;
    }

    public void updateStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public void updateStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }

    public void updateState(VesselState vesselState){
        this.longitude = vesselState.getLongitude();
        this.latitude = vesselState.getLatitude();
        this.velocity = vesselState.getVelocity();
        this.timeStamp = vesselState.getTimeStamp();
    }
}

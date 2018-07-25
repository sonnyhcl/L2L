package iot.service.shadow;

import com.amazonaws.services.iot.client.AWSIotDevice;
import com.fasterxml.jackson.annotation.JsonFilter;


@JsonFilter("wantedProperties")
public class WagonDevice extends AWSIotDevice {
    public WagonDevice(String thingName) {
        super(thingName);
    }

    private String id;
    private double longitude;
    private double latitude;
    private double speed;
    private double movedDistance;
    private String status;

    public String getId() {
        return id;
    }

    public void updateId(String id) {
        this.id = id;
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

    public double getSpeed() {
        return speed;
    }

    public void updateSpeed(double speed) {
        this.speed = speed;
    }

    public double getMovedDistance() {
        return movedDistance;
    }

    public void updateMovedDistance(double movedDistance) {
        this.movedDistance = movedDistance;
    }

    public String getStatus() {
        return status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}

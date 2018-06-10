package vessel.domain;

public class VesselState {
    private double longitude;
    private double latitude;
    private double velocity;
    private  String date;
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public VesselState() {
    }

    public VesselState(double longitude, double latitude, double velocity, String date) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.velocity = velocity;
        this.date = date;
    }

    @Override
    public String toString() {
        return "VesselState{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", velocity=" + velocity +
                ", date='" + date + '\'' +
                '}';
    }

    public VesselState deepCopy(){
        VesselState res = new VesselState();
        res.setDate(this.date);
        res.setLatitude(this.latitude);
        res.setLongitude(this.longitude);
        res.setVelocity(this.velocity);
        return res;
    }
}

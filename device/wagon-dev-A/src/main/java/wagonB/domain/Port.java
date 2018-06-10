package wagonB.domain;

public class Port {
    private String pname;
    private double longitude;
    private double latitude;
    private String stAnchorTime;
    private String estiReachTime;
    private String estiDepartureTime;

    public Port(String pname, double longitude, double latitude, String estiReachTime, String estiDepartureTime) {
        this.pname = pname;
        this.longitude = longitude;
        this.latitude = latitude;
        this.estiReachTime = estiReachTime;
        this.estiDepartureTime = estiDepartureTime;
    }

    public Port() {
        super();
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

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

    public String getEstiReachTime() {
        return estiReachTime;
    }

    public void setEstiReachTime(String estiReachTime) {
        this.estiReachTime = estiReachTime;
    }

    public String getEstiDepartureTime() {
        return estiDepartureTime;
    }

    public void setEstiDepartureTime(String estiDepartureTime) {
        this.estiDepartureTime = estiDepartureTime;
    }

    public String getStAnchorTime() {
        return stAnchorTime;
    }

    public void setStAnchorTime(String stAnchorTime) {
        this.stAnchorTime = stAnchorTime;
    }

    @Override
    public String toString() {
        return "Port{" +
                "pname='" + pname + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", stAnchorTime='" + stAnchorTime + '\'' +
                ", estiReachTime='" + estiReachTime + '\'' +
                ", estiDepartureTime='" + estiDepartureTime + '\'' +
                '}';
    }
}

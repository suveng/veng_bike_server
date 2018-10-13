package my.suveng.veng_bike_server.vehicle.pojo.mysql;

public class Vehicle {
    private String vehicleid;

    private String qrcode;

    private Double longitude;

    private Double latitude;

    private Integer status;

    private Integer type;

    private String pointid;

    public Vehicle() {
    }

    public Vehicle(String vehicleid, String qrcode, Double longitude, Double latitude, Integer status, Integer type, String pointid) {
        this.vehicleid = vehicleid;
        this.qrcode = qrcode;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
        this.type = type;
        this.pointid = pointid;
    }

    public String getVehicleid() {
        return vehicleid;
    }

    public void setVehicleid(String vehicleid) {
        this.vehicleid = vehicleid == null ? null : vehicleid.trim();
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode == null ? null : qrcode.trim();
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPointid() {
        return pointid;
    }

    public void setPointid(String pointid) {
        this.pointid = pointid == null ? null : pointid.trim();
    }
}
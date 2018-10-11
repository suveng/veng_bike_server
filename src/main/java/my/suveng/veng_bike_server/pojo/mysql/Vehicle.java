package my.suveng.veng_bike_server.pojo.mysql;

import lombok.Data;

@Data
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
}
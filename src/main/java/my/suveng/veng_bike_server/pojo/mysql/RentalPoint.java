package my.suveng.veng_bike_server.pojo.mysql;

import lombok.Data;

@Data
public class RentalPoint {
    private String pointid;

    private Double longitude;

    private Double latitude;

    private Integer leftbike;

    public RentalPoint() {
    }

    public RentalPoint(String pointid, Double longitude, Double latitude, Integer leftbike) {
        this.pointid = pointid;
        this.longitude = longitude;
        this.latitude = latitude;
        this.leftbike = leftbike;
    }
}
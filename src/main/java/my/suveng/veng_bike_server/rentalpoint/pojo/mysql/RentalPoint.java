package my.suveng.veng_bike_server.rentalpoint.pojo.mysql;

import lombok.Data;

@Data
public class RentalPoint {
    private String pointid;

    private Double longitude;

    private Double latitude;

    private Integer leftbike;
}

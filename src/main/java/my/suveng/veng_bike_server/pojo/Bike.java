package my.suveng.veng_bike_server.pojo;

import lombok.Data;

@Data
public class Bike {
    private String id;

    private String qrCode;

    private Double latitude;

    private Double longitude;

    private Integer status;
}
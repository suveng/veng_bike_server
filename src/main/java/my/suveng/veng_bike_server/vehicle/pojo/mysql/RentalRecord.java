package my.suveng.veng_bike_server.vehicle.pojo.mysql;

import lombok.Data;

import java.util.Date;
@Data
public class RentalRecord {
    private Long rentalid;

    private String userid;

    private String vehicleid;

    private Integer isfinish;

    private String beginpoint;

    private String endpoint;

    private Date begintime;

    private Date endtime;
}

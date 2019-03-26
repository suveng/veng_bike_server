package my.suveng.veng_bike_server.vehicle.pojo.mysql;

import lombok.Data;

import java.util.Date;
@Data
public class RentalRecord {
    /**
     * 记录id
     */
    private Long rentalid;

    /**
     * 当前记录的使用者 用户id
     */
    private String userid;


    /**
     * 当前记录的车辆id
     **/
    private String vehicleid;

    private Integer isfinish;

    private String beginpoint;

    private String endpoint;

    private Date begintime;

    private Date endtime;
}

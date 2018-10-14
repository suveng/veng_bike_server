package my.suveng.veng_bike_server.vehicle.pojo.mysql;

import java.util.Date;

public class RentalRecord {
    private Long rentalid;

    private String userid;

    private String vehicleid;

    private Integer isfinish;

    private String beginpoint;

    private String endpoint;

    private Date begintime;

    private Date endtime;

    public Long getRentalid() {
        return rentalid;
    }

    public void setRentalid(Long rentalid) {
        this.rentalid = rentalid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }

    public String getVehicleid() {
        return vehicleid;
    }

    public void setVehicleid(String vehicleid) {
        this.vehicleid = vehicleid == null ? null : vehicleid.trim();
    }

    public Integer getIsfinish() {
        return isfinish;
    }

    public void setIsfinish(Integer isfinish) {
        this.isfinish = isfinish;
    }

    public String getBeginpoint() {
        return beginpoint;
    }

    public void setBeginpoint(String beginpoint) {
        this.beginpoint = beginpoint == null ? null : beginpoint.trim();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint == null ? null : endpoint.trim();
    }

    public Date getBegintime() {
        return begintime;
    }

    public void setBegintime(Date begintime) {
        this.begintime = begintime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }
}
package my.suveng.veng_bike_server.pojo.mysql;

import lombok.Data;

import java.util.Date;
@Data
public class Order {
    private String orderid;

    private String userid;

    private Double fee;

    private String beginrentalid;

    private String endrentalid;

    private Integer status;

    private Date begintime;

    private Date endtime;

    public Order() {
    }

    public Order(String orderid, String userid, Double fee, String beginrentalid, String endrentalid, Integer status, Date begintime, Date endtime) {
        this.orderid = orderid;
        this.userid = userid;
        this.fee = fee;
        this.beginrentalid = beginrentalid;
        this.endrentalid = endrentalid;
        this.status = status;
        this.begintime = begintime;
        this.endtime = endtime;
    }
}
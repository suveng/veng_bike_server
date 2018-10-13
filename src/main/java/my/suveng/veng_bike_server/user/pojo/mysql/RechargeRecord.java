package my.suveng.veng_bike_server.user.pojo.mysql;

import lombok.Data;

import java.util.Date;
@Data
public class RechargeRecord {
    private Long rechareid;

    private String userid;

    private Double charge;

    private Date createtime;

    private String province;

    private String city;

    private String district;

    public RechargeRecord() {
    }

    public RechargeRecord(Long rechareid, String userid, Double charge, Date createtime, String province, String city, String district) {
        this.rechareid = rechareid;
        this.userid = userid;
        this.charge = charge;
        this.createtime = createtime;
        this.province = province;
        this.city = city;
        this.district = district;
    }

    public RechargeRecord(Double charge, Date createtime, String province, String city, String district) {
        this.charge = charge;
        this.createtime = createtime;
        this.province = province;
        this.city = city;
        this.district = district;
    }

    public RechargeRecord(Double charge, String province, String city, String district) {
        this.charge = charge;
        this.province = province;
        this.city = city;
        this.district = district;
    }
}
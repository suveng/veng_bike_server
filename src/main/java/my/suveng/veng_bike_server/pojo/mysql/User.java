package my.suveng.veng_bike_server.pojo.mysql;

import lombok.Data;

@Data
public class User {
    private String userid;

    private Integer status;

    private Integer phonenum;

    private String name;

    private String idnum;

    private Double deposit;

    private Double balance;

    public User() {
    }

    public User(String userid, Integer status, Integer phonenum, String name, String idnum, Double deposit, Double balance) {
        this.userid = userid;
        this.status = status;
        this.phonenum = phonenum;
        this.name = name;
        this.idnum = idnum;
        this.deposit = deposit;
        this.balance = balance;
    }
}
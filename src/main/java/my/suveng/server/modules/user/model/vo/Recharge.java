package my.suveng.server.modules.user.model.vo;

import lombok.Data;

/**
 * create at 2018/10/12
 * author: suveng
 * email: suveng@163.com
 * 充值的request封装类
 **/
@Data
public class Recharge {
    private String phoneNum;
    private double charge;

    public Recharge() {
    }

    public Recharge(String phoneNum, double charge) {
        this.phoneNum = phoneNum;
        this.charge = charge;
    }
}

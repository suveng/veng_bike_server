package my.suveng.veng_bike_server.vehicle.vo;

import lombok.Data;

/**
 * create at 2018/10/14
 * author: suveng
 * email: suveng@163.com
 **/

public enum BikeStatus {
    NOT_RENTED("未出租",0),
    RENTED("已出租",1),
    BOOKEND("损坏",2);
    private String name;
    private int status;

    BikeStatus(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

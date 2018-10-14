package my.suveng.veng_bike_server.vehicle.vo;

/**
 * create at 2018/10/14
 * author: suveng
 * email: suveng@163.com
 **/
public enum  RntalRecordFlag {
    BEGIN("未完成",0),
    FINISH("已完成",1),
    WRONG("出现问题",2)
    ;
    private String name;
    private int status;

    RntalRecordFlag(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

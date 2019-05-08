package my.suveng.veng_bike_server.common.enums;

/**
 * @author suwenguang
 *         suveng@163.com
 * since 2019/5/7
 * description:
 **/
public enum VehicleEnums {
    /**
     * 待租
     */
    WAIT(0, "待租状态"),
    /**
     * 已租
     */
    LEND(1, "已租状态"),
    /**
     * 预定
     */
    RESERVATION(1, "预定状态"),
    ;


    VehicleEnums(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

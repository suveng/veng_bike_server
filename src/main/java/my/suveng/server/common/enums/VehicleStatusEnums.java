package my.suveng.server.common.enums;

/**
 * @author suwenguang
 *         suveng@163.com
 * since 2019/5/1
 * description:
 **/
public enum VehicleStatusEnums {
    /**
     * 待出租
     */
    READY(0, "待出租"),
    /**
     * 已完成
     */
    LEND(1, "已出租"),
    /**
     * 已预订
     */
    BOOKED(2, "已预订"),
    ;

    VehicleStatusEnums(Integer code, String msg) {
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

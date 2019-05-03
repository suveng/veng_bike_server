package my.suveng.server.common.enums;

/**
 * @author suwenguang
 *         suveng@163.com
 * since 2019/5/1
 * description:
 **/
public enum RentalRecordEnums {
    /**
     * 未完成
     */
    NOT_FINISH(0,"未完成"),
    /**
     * 已完成
     */
    FINISH(1,"已完成"),
    ;

    RentalRecordEnums(Integer code, String msg) {
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

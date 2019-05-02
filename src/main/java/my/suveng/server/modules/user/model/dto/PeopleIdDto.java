package my.suveng.server.modules.user.model.dto;

import lombok.Data;

/**
 * @author suwenguang
 * email suveng@163.com
 * since 2019/4/1
 * description: 身份证实名认证传输对象
 **/
@Data
public class PeopleIdDto {
    private String name;
    private String idNo;
    private String respMessage;
    private String respCode;
    private String province;
    private String city;
    private String county;
    private String birthday;
    private String sex;
    private String age;
}

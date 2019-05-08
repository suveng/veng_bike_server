package my.suveng.veng_bike_server.user.pojo.mongo;


import lombok.Data;
import my.suveng.veng_bike_server.user.pojo.mysql.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-9-9 上午10:21
 */

@Document(collection = "users")
@Data
public class UserMongo {

    @Id
    private String id;

    private int status;

    //这个字段创建索引
    @Indexed(unique = true)
    private String phoneNum;

    private String name;
    /**
     * 身份证号
     */
    private String idNum;

    private double deposit;

    private double balance;

    //这个属性在数据库中不存储
    @Transient
    private String verifyCode;

    public User toMysql() {
        User res = new User();
        res.setBalance(this.balance);
        res.setDeposit(this.deposit);
        res.setIdnum(this.idNum);
        res.setName(this.name);
        res.setPhonenum(this.phoneNum);
        res.setStatus(this.getStatus());
        res.setUserid(this.getId());
        return res;
    }
}

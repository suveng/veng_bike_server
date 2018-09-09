package my.suveng.veng_bike_server.pojo;



import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-9-9 上午10:21
 */

@Document(collection="users")
@Data
public class User {

    @Id
    private String id;

    private int status;

    //这个字段创建索引
    @Indexed(unique = true)
    private String phoneNum;

    private String name;

    private String idNum;

    private double deposit;

    private double balance;

    //这个属性在数据库中不存储
    @Transient
    private String verifyCode;

}

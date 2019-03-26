package my.suveng.veng_bike_server.vehicle.pojo.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;

/**
 * create at 2018/10/14
 * author: suveng
 * email: suveng@163.com
 * description: 轨迹记录
 **/

@Document(collection = "rentalrecords")
@Data
public class RentalRecord {
    @Id
    private String rentalId;

    @Field
    private ArrayList<double[]> locations;
}

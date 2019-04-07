package my.suveng.veng_bike_server.rentalpoint.pojo.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * create at 2018/10/10
 * author: suveng
 * email: suveng@163.com
 * 租赁点的实体类
 **/
@Document(collection = "RentalPoints")
@Data
public class RentalPoint {
    @Id
    private String id;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location;

    private int left_bike;

    public my.suveng.veng_bike_server.rentalpoint.pojo.mysql.RentalPoint toMySQL(RentalPoint rentalPoint) {
        my.suveng.veng_bike_server.rentalpoint.pojo.mysql.RentalPoint myRentalPoint =new my.suveng.veng_bike_server.rentalpoint.pojo.mysql.RentalPoint();
        myRentalPoint.setPointid(rentalPoint.getId());
        double[] location = rentalPoint.getLocation();
        myRentalPoint.setLongitude(location[0]);
        myRentalPoint.setLatitude(location[1]);
        myRentalPoint.setLeftbike(rentalPoint.getLeft_bike());
        return myRentalPoint;
    }
}

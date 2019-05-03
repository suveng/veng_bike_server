package my.suveng.server.modules.rental.model.po;

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
public class RentalPointMongo {
    @Id
    private Long id;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location;

    private int leftBike;

    public RentalPoint toMySQL(RentalPointMongo rentalPointMongo) {
        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setPointId(Long.valueOf(rentalPointMongo.getId()));
        double[] location = rentalPointMongo.getLocation();
        rentalPoint.setLongitude(location[0]);
        rentalPoint.setLatitude(location[1]);
        rentalPoint.setLeftBike(rentalPointMongo.getLeftBike());
        return rentalPoint;
    }
}

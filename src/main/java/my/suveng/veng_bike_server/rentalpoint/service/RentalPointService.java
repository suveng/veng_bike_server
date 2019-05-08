package my.suveng.veng_bike_server.rentalpoint.service;

import my.suveng.veng_bike_server.rentalpoint.pojo.mongo.RentalPointMongo;
import my.suveng.veng_bike_server.rentalpoint.pojo.mysql.RentalPoint;
import org.springframework.data.geo.GeoResults;

import java.util.List;

/**
 * create at 2018/10/10
 * author: suveng
 * email: suveng@163.com
 **/
public interface RentalPointService {
    void save(RentalPointMongo rentalPointMongo);

    List<RentalPointMongo> findAll();

    GeoResults<RentalPointMongo> findNear(Double longitude, Double latitude);

    boolean update(RentalPoint rentalPointMysql);
}

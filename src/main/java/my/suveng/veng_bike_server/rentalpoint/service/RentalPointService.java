package my.suveng.veng_bike_server.rentalpoint.service;

import my.suveng.veng_bike_server.rentalpoint.pojo.mongo.RentalPoint;
import org.springframework.data.geo.GeoResults;

import java.util.List;

/**
 * create at 2018/10/10
 * author: suveng
 * email: suveng@163.com
 **/
public interface RentalPointService {
    void save(RentalPoint rentalPoint);

    List<RentalPoint> findAll();

    GeoResults<RentalPoint> findNear(double longitude, double latitude);
}

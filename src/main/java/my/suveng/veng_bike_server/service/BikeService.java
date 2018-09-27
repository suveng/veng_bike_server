package my.suveng.veng_bike_server.service;

import my.suveng.veng_bike_server.pojo.Bike;
import org.springframework.data.geo.GeoResults;

import java.util.List;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-25 下午7:03
 */
public interface BikeService {
    void save(Bike bike);

    List<Bike> findAll();

    GeoResults<Bike> findNear(double longitude, double latitude);
}

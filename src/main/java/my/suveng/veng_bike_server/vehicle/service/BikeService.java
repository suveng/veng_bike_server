package my.suveng.veng_bike_server.vehicle.service;

import my.suveng.veng_bike_server.user.pojo.mongo.User;
import my.suveng.veng_bike_server.vehicle.pojo.mongo.Bike;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle;
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

    void saveInMysql(Vehicle vehicle);

    void unlock(User user, Bike bike);

    void lock(User user, Bike bike);
}

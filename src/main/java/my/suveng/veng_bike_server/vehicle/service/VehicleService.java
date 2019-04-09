package my.suveng.veng_bike_server.vehicle.service;

import my.suveng.veng_bike_server.user.pojo.mongo.User;
import my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.geo.GeoResults;

import java.util.List;
import java.util.Map;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-25 下午7:03
 */
public interface VehicleService {
    void save(Vehicle vehicle);

    List<Vehicle> findAll();

    GeoResults<Vehicle> findNear(double longitude, double latitude);

    boolean saveInMysql(my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle vehicle);

    boolean unlock(User user, Vehicle vehicle);

    Map lock(String userId, Double lo, Double la);

    boolean updateMysql(my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle toMySQL);

}

package my.suveng.veng_bike_server.vehicle.service;

import my.suveng.veng_bike_server.user.pojo.mongo.UserMongo;
import my.suveng.veng_bike_server.vehicle.pojo.mongo.VehicleMongo;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle;
import org.springframework.data.geo.GeoResults;

import java.util.List;
import java.util.Map;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-25 下午7:03
 */
public interface VehicleService {
    void save(VehicleMongo vehicleMongo);

    List<VehicleMongo> findAll();

    GeoResults<VehicleMongo> findNear(double longitude, double latitude);

    boolean saveInMysql(Vehicle vehicle);

    boolean unlock(UserMongo userMongo, VehicleMongo vehicleMongo);

    Map lock(String userId, Double lo, Double la) throws Exception;

    boolean updateMysql(Vehicle toMySQL);

}

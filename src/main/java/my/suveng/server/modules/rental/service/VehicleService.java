package my.suveng.server.modules.rental.service;

import my.suveng.server.modules.rental.model.po.Vehicle;
import my.suveng.server.modules.rental.model.po.VehicleMongo;
import my.suveng.server.modules.user.model.po.UserMongo;
import org.springframework.data.geo.GeoResults;

import java.util.List;
import java.util.Map;

/**
 * @author suwenguang
 * suveng@163.com
 * since 2019/3/19
 * description:
 **/
public interface VehicleService {
    void save(VehicleMongo vehicleMongo);

    List<VehicleMongo> findAll();

    GeoResults<Vehicle> findNear(double longitude, double latitude);

    boolean saveInMysql(Vehicle vehicle);

    boolean unlock(UserMongo userMongo, VehicleMongo vehicleMongo);

    Map lock(String userId, Double lo, Double la);

    boolean updateMysql(Vehicle toMySQL);
}

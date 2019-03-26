package my.suveng.veng_bike_server.vehicle.service.impl;

import my.suveng.veng_bike_server.user.pojo.mongo.User;
import my.suveng.veng_bike_server.vehicle.dao.mysql.RentalRecordMapper;
import my.suveng.veng_bike_server.vehicle.dao.mysql.VehicleMapper;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.RentalRecord;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle;
import my.suveng.veng_bike_server.vehicle.service.VehicleService;
import my.suveng.veng_bike_server.vehicle.vo.BikeStatus;
import my.suveng.veng_bike_server.vehicle.vo.RntalRecordFlag;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-25 下午7:03
 */
@Service
public class VehicleServiceImpl implements VehicleService {

    @Resource
    MongoTemplate mongoTemplate;
    @Resource
    VehicleMapper vehicleMapper;
    @Resource
    RentalRecordMapper rentalRecordMapper;

    @Override
    public void save(my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle bike) {
        mongoTemplate.insert(bike);
    }

    @Override
    public List<my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle> findAll() {
        return mongoTemplate.findAll(my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle.class, "bike");
    }

    @Override
    public GeoResults<my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle> findNear(double longitude, double latitude) {
        //指定nearquery 相当于查询条件
        NearQuery nearQuery = NearQuery.near(new Point(longitude, latitude), Metrics.KILOMETERS);
        nearQuery.maxDistance(1).query(new Query().addCriteria(Criteria.where("status").is(0)).limit(20));
        //通过mongo 的geohash算法的接口计算出来。
        return mongoTemplate.geoNear(nearQuery, my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle.class);
    }

    @Override
    public void saveInMysql(Vehicle vehicle) {
        vehicleMapper.insert(vehicle);
    }

    @Override
    public void unlock(User user, my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle bike) {
        List<User> id1 = mongoTemplate.find(new Query(Criteria.where("id").is(user.getId())), User.class);
        user = id1.get(0);
        if (user.getBalance() < 0 || user.getDeposit() < 100) {
            throw new IllegalStateException("不符合解锁条件");
        }
//        1. 创建租车就记录
        List<my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle> id = mongoTemplate.find(new Query(Criteria.where("id").is(bike.getId())), my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle.class);
        bike = id.get(0);

        RentalRecord rentalRecord = new RentalRecord();
        rentalRecord.setIsfinish(RntalRecordFlag.BEGIN.getStatus());
        rentalRecord.setBeginpoint(bike.getPointid());
        rentalRecord.setUserid(user.getId());
        rentalRecord.setVehicleid(bike.getId());
        rentalRecordMapper.insert(rentalRecord);
        //        1. 修改车辆状态
        bike.setStatus(BikeStatus.RENTED.getStatus());
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(bike.getId())), new Update().set("status", bike.getStatus()), my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle.class);
    }

    @Override
    public void lock(User user, my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle bike) {
        my.suveng.veng_bike_server.vehicle.pojo.mongo.RentalRecord rentalRecord = new my.suveng.veng_bike_server.vehicle.pojo.mongo.RentalRecord();
        rentalRecord.setRentalId("fadfadqwefadfa");
        ArrayList<double[]> doubles = new ArrayList<>();
        doubles.add(new double[]{1, 123});
        doubles.add(new double[]{1, 123});
        doubles.add(new double[]{1, 123});
        doubles.add(new double[]{1, 123});
        doubles.add(new double[]{1, 123});
        rentalRecord.setLocations(doubles);
        mongoTemplate.insert(rentalRecord);
    }
}

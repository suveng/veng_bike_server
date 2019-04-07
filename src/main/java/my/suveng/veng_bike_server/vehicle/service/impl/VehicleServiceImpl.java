package my.suveng.veng_bike_server.vehicle.service.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import my.suveng.veng_bike_server.rentalpoint.dao.mysql.RentalPointMapper;
import my.suveng.veng_bike_server.rentalpoint.pojo.mongo.RentalPoint;
import my.suveng.veng_bike_server.user.pojo.mongo.User;
import my.suveng.veng_bike_server.vehicle.dao.mysql.RentalRecordMapper;
import my.suveng.veng_bike_server.vehicle.dao.mysql.VehicleMapper;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.RentalRecord;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle;
import my.suveng.veng_bike_server.vehicle.service.VehicleService;
import my.suveng.veng_bike_server.vehicle.vo.BikeStatus;
import my.suveng.veng_bike_server.vehicle.vo.RntalRecordFlag;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.joda.time.LocalDateTime;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-25 下午7:03
 */
@Service
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    @Resource
    MongoTemplate mongoTemplate;
    @Resource
    VehicleMapper vehicleMapper;
    @Resource
    RentalRecordMapper rentalRecordMapper;
    @Resource
    RentalPointMapper rentalPointMapper;

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
    public boolean saveInMysql(Vehicle vehicle) {
        return vehicleMapper.insertSelective(vehicle)>=1;
    }

    @Override
    public boolean unlock(User user, my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle bike) {
        //检查
        bike = mongoTemplate.findById(bike.getId(), my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle.class);
        user = mongoTemplate.findById(user.getId(), User.class);
        if (!ObjectUtils.allNotNull(user,bike,bike.getPointid())) {
            return false;
        }
        RentalPoint rentalPoint = mongoTemplate.findById(bike.getPointid(), RentalPoint.class);
        if (!ObjectUtils.allNotNull(rentalPoint)){
            return false;
        }
        //检查是否是预留车
        if (bike.getStatus()!=0){
            return false;
        }
        //检查用户满足条件
        assert user != null;
        if (user.getBalance() < 0 || user.getDeposit() < 100) {
            return false;
        }
        List<RentalRecord> rentalRecords = rentalRecordMapper.selectByUserId(user.getId(), 0);
        if (!CollectionUtils.isEmpty(rentalRecords)){
            return false;
        }else {
            RentalRecord rentalRecord1 = rentalRecords.get(0);
            if (rentalRecord1.getVehicleid().equals(bike.getId())) {
                return true;
            }
        }
        //创建租赁记录
        RentalRecord rentalRecord = new RentalRecord();
        rentalRecord.setIsfinish(RntalRecordFlag.BEGIN.getStatus());
        rentalRecord.setBeginpoint(bike.getPointid());
        rentalRecord.setBegintime(LocalDateTime.now().toDate());
        rentalRecord.setUserid(user.getId());
        rentalRecord.setVehicleid(bike.getId());
        rentalRecordMapper.insertSelective(rentalRecord);
        //修改车辆状态
        bike.setStatus(BikeStatus.RENTED.getStatus());
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(bike.getId())), new Update().set("status", bike.getStatus()), my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle.class);
        //修改租赁点剩余车辆
        int i = rentalPoint.getLeft_bike() - 1;
        if (i < 0) {
            log.error("租赁点有脏数据！【{}】", JSON.toJSONString(rentalPoint));
            return false;
        }
        rentalPoint.setLeft_bike(i);
        UpdateResult updateResult = mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(rentalPoint.getId())), new Update().set("left_bike", i), RentalPoint.class);
        my.suveng.veng_bike_server.rentalpoint.pojo.mysql.RentalPoint rentalPoint1 = rentalPoint.toMySQL(rentalPoint);
        rentalPointMapper.updateByPrimaryKeySelective(rentalPoint1);
        return true;
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

    @Override
    public boolean updateMysql(Vehicle toMySQL) {
        return vehicleMapper.updateByPrimaryKeySelective(toMySQL)>=1;
    }
}

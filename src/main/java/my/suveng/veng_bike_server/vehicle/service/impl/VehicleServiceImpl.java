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
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return vehicleMapper.insertSelective(vehicle) >= 1;
    }

    @Override
    public boolean unlock(User user, my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle bike) {
        //检查
        bike = mongoTemplate.findById(bike.getId(), my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle.class);
        user = mongoTemplate.findById(user.getId(), User.class);
        if (!ObjectUtils.allNotNull(user, bike)) {
            return false;
        }
        if (!ObjectUtils.allNotNull(bike.getPointid())){
            return false;
        }
        //检查是否是预留车
        RentalPoint rentalPoint = mongoTemplate.findById(bike.getPointid(), RentalPoint.class);
        if (!ObjectUtils.allNotNull(rentalPoint)) {
            return false;
        }
        List<RentalRecord> rentalRecords = rentalRecordMapper.selectByUserId(user.getId(), 0);
        if (!CollectionUtils.isEmpty(rentalRecords)) {
            if (rentalRecords.size() > 1) {
                return false;
            }
            if (rentalRecords.size() == 1) {
                RentalRecord rentalRecord1 = rentalRecords.get(0);
                if (rentalRecord1.getVehicleid().equals(bike.getId())) {
                    mongoTemplate.updateFirst(new Query(Criteria.where("id").is(bike.getId())), new Update().set("status", 1), my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle.class);
                    return true;
                }else{
                    return false;
                }
            }
        }
        //检查车辆状态
        if (bike.getStatus() != 0) {
            return false;
        }
        //检查用户满足条件
        if (user.getBalance() < 0 || user.getDeposit() < 100) {
            return false;
        }

        //创建租赁记录
        return !getRecord(user, bike, rentalPoint);
    }

    private boolean getRecord(User user, my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle bike, RentalPoint rentalPoint) {
        RentalRecord rentalRecord = new RentalRecord();
        rentalRecord.setIsfinish(RntalRecordFlag.BEGIN.getStatus());
        rentalRecord.setBeginpoint(bike.getPointid());
        rentalRecord.setBegintime(LocalDateTime.now().toDate());
        rentalRecord.setUserid(user.getId());
        rentalRecord.setVehicleid(bike.getId());
        rentalRecordMapper.insertSelective(rentalRecord);
        //修改车辆状态
        bike.setStatus(BikeStatus.RENTED.getStatus());
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(bike.getId())), new Update().set("status", 1), my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle.class);
        //修改租赁点剩余车辆
        int i = rentalPoint.getLeft_bike() - 1;
        if (i < 0) {
            log.error("租赁点有脏数据！【{}】", JSON.toJSONString(rentalPoint));
            return true;
        }
        rentalPoint.setLeft_bike(i);
        UpdateResult updateResult = mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(rentalPoint.getId())), new Update().set("left_bike", i), RentalPoint.class);
        my.suveng.veng_bike_server.rentalpoint.pojo.mysql.RentalPoint rentalPoint1 = rentalPoint.toMySQL(rentalPoint);
        rentalPointMapper.updateByPrimaryKeySelective(rentalPoint1);
        return false;
    }

    @Override
    @Transactional
    public Map lock(String userId, Double lo, Double la) {
        //根据userId查找订单
        User user = mongoTemplate.findById(userId, User.class);
        if (!ObjectUtils.allNotNull(user)) {
            throw new RuntimeException("事务");
        }
        List<RentalRecord> rentalRecords = rentalRecordMapper.selectByUserId(userId, 0);
        if (CollectionUtils.isEmpty(rentalRecords)) {
            throw new RuntimeException("事务");
        }
        if (rentalRecords.size() != 1) {
            throw new RuntimeException("事务");
        }
        //检查是否车辆状态是否租赁中
        RentalRecord rentalRecord = rentalRecords.get(0);
        my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle ve = mongoTemplate.findOne(Query.query(Criteria.where("id").is(rentalRecord.getVehicleid())), my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle.class);
        if (!ve.getStatus().equals(1)) {
            throw new RuntimeException("事务");
        }
        //完成订单
        GeoResults<RentalPoint> geoResults = mongoTemplate.geoNear(NearQuery.near(lo, la).maxDistance(new Distance(1, Metrics.KILOMETERS)).query(new Query().limit(10)), RentalPoint.class);
        List<GeoResult<RentalPoint>> content = geoResults.getContent();
        if (CollectionUtils.isEmpty(content)) {
            throw new RuntimeException("事务");
        }
        RentalPoint rentalPoint;
        if (content.size() > 0) {
            GeoResult<RentalPoint> rentalPointGeoResult = content.get(0);
            rentalPoint = rentalPointGeoResult.getContent();
            rentalRecord.setEndpoint(rentalPoint.getId());
            //租赁点车辆增加
            rentalPoint.setLeft_bike(rentalPoint.getLeft_bike() + 1);
        } else {
            throw new RuntimeException("事务");
        }
        rentalRecord.setIsfinish(1);
        rentalRecord.setEndtime(new Date());
        rentalRecordMapper.updateByPrimaryKeySelective(rentalRecord);
        //获取时间计算费用
        Duration duration = new Duration(rentalRecord.getBegintime().getTime(), rentalRecord.getEndtime().getTime());
        long standardHours = duration.getStandardHours();
        if (standardHours == 0) {
            standardHours = 1;
        }
        HashMap<String, String> res = new HashMap<>();
        Double cost = Double.valueOf(standardHours * 10);
        res.put("cost", String.valueOf(cost));
        double balance = user.getBalance();
        res.put("balence", String.valueOf(balance));
        if (cost > balance) {
            res.put("toPay", "true");
        }
        //更新租赁点信息
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(rentalPoint.getId())), Update.update("left_bike", rentalPoint.getLeft_bike()), rentalPoint.getClass());
        //更新车辆信息
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(ve.getId())), Update.update("status", 0).set("location", rentalPoint.getLocation()).set("pointid",rentalPoint.getId()), my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle.class);
        return res;
    }

    @Override
    public boolean updateMysql(Vehicle toMySQL) {
        return vehicleMapper.updateByPrimaryKeySelective(toMySQL) >= 1;
    }
}

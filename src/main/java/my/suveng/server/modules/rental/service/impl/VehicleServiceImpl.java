package my.suveng.server.modules.rental.service.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import my.suveng.server.common.enums.RentalRecordEnums;
import my.suveng.server.common.enums.VehicleStatusEnums;
import my.suveng.server.modules.rental.dao.VehicleRepository;
import my.suveng.server.modules.rental.model.po.*;
import my.suveng.server.modules.rental.model.vo.RntalRecordFlag;
import my.suveng.server.modules.rental.service.RentalPointService;
import my.suveng.server.modules.rental.service.RentalRecordService;
import my.suveng.server.modules.rental.service.VehicleService;
import my.suveng.server.modules.user.model.po.UserMongo;
import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suwenguang
 *         suveng@163.com
 * since 2019/5/1
 * description:
 **/
@Service
@Slf4j
public class VehicleServiceImpl implements VehicleService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private RentalRecordService rentalRecordService;
    @Autowired
    private RentalPointService rentalPointService;

    @Override
    public void save(VehicleMongo vehicleMongo) {
        mongoTemplate.insert(vehicleMongo);
    }

    @Override
    public List<VehicleMongo> findAll() {
        return mongoTemplate.findAll(VehicleMongo.class, "bike");
    }

    @Override
    public GeoResults<Vehicle> findNear(double longitude, double latitude) {
        //指定nearquery 相当于查询条件
        NearQuery nearQuery = NearQuery.near(new Point(longitude, latitude), Metrics.KILOMETERS);
        nearQuery.maxDistance(1).query(new Query().addCriteria(Criteria.where("status").is(0)).limit(20));
        //通过mongo 的geohash算法的接口计算出来。
        return mongoTemplate.geoNear(nearQuery, Vehicle.class);
    }

    @Override
    public boolean saveInMysql(Vehicle vehicle) {
        try {
            vehicleRepository.save(vehicle);
            return true;
        } catch (Exception e) {
            log.info("[vehicle]:保存到MySQL失败,记录:{}", JSON.toJSONString(vehicle));
            return false;
        }
    }

    @Override
    public boolean unlock(UserMongo userMongo, VehicleMongo vehicleMongo) {
        //检查
        vehicleMongo = mongoTemplate.findById(vehicleMongo.getId(), VehicleMongo.class);
        userMongo = mongoTemplate.findById(userMongo.getId(), UserMongo.class);
        if (!ObjectUtils.allNotNull(userMongo, vehicleMongo)) {
            return false;
        }
        assert vehicleMongo != null;
        if (!ObjectUtils.allNotNull(vehicleMongo.getPointId())) {
            return false;
        }
        //检查是否是预留车
        RentalPointMongo rentalPoint = mongoTemplate.findById(vehicleMongo.getPointId(), RentalPointMongo.class);
        if (!ObjectUtils.allNotNull(rentalPoint)) {
            return false;
        }

        assert userMongo != null;
        List<RentalRecord> rentalRecords = rentalRecordService.getByUserId(userMongo.getId(), RentalRecordEnums.NOT_FINISH.getCode());

        if (!CollectionUtils.isEmpty(rentalRecords)) {
            if (rentalRecords.size() > 1) {
                return false;
            }
            if (rentalRecords.size() == 1) {
                RentalRecord rentalRecord = rentalRecords.get(0);
                if (rentalRecord.getVehicleId().equals(vehicleMongo.getId())) {
                    UpdateResult updateResult = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(vehicleMongo.getId())), new Update().set("status", 1), Vehicle.class);
                    if (updateResult.getModifiedCount() < 1) {
                        log.error("[vehicle]:更新mongo的车辆状态失败");
                        return false;
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }
        //检查车辆状态
        if (vehicleMongo.getStatus() != 0) {
            return false;
        }
        //检查用户满足条件
        if (userMongo.getBalance() < 0 || userMongo.getDeposit() < 100) {
            return false;
        }

        //创建租赁记录
        return createRentalRecord(userMongo, vehicleMongo, rentalPoint);
    }

    private boolean createRentalRecord(UserMongo user, VehicleMongo vehicleMongo, RentalPointMongo rentalPointMongo) {
        RentalRecord rentalRecord = new RentalRecord();
        rentalRecord.setStatus(RntalRecordFlag.BEGIN.getStatus());
        rentalRecord.setBeginPointId(vehicleMongo.getPointId());
        rentalRecord.setBeginTime(LocalDateTime.now().toDate());
        rentalRecord.setUserId(user.getId());
        rentalRecord.setVehicleId(vehicleMongo.getId());
        if (rentalRecordService.save(rentalRecord)) {
            log.info("[vehicle]:uid:{},创建租赁记录成功", user.getId());
        } else {
            log.error("[vehicle]:uid:{},创建租赁记录失败", user.getId());
        }
        //修改车辆状态
        vehicleMongo.setStatus(VehicleStatusEnums.LEND.getCode());
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(vehicleMongo.getId())), new Update().set("status", vehicleMongo.getStatus()), Vehicle.class);
        //修改租赁点剩余车辆
        int left = rentalPointMongo.getLeftBike() - 1;
        if (left < 0) {
            log.error("[rental]:租赁点有脏数据!记录为:{}", JSON.toJSONString(rentalPointMongo));
            return false;
        }
        rentalPointMongo.setLeftBike(left);
        UpdateResult updateResult = mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(rentalPointMongo.getId())), new Update().set("leftBike", left), RentalPointMongo.class);
        if (updateResult.getModifiedCount() < 1) {
            log.error("[rental]:修改租赁点剩余车辆,失败:记录为:{}", JSON.toJSONString(rentalPointMongo));
            return false;
        } else {
            log.info("[rental]:修改租赁点剩余车辆,失败");
        }
        if (rentalPointService.save(rentalPointMongo)) {
            log.error("[rental]:保存租赁点信息失败");
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public Map lock(String userId, Double lo, Double la) {
        //根据userId查找订单
        UserMongo userMongo = mongoTemplate.findById(userId, UserMongo.class);
        if (!ObjectUtils.allNotNull(userMongo)) {
            throw new RuntimeException("事务");
        }
        List<RentalRecord> rentalRecords = rentalRecordService.getByUserId(userId, RentalRecordEnums.NOT_FINISH.getCode());
        if (CollectionUtils.isEmpty(rentalRecords)) {
            throw new RuntimeException("事务");
        }
        if (rentalRecords.size() != 1) {
            throw new RuntimeException("事务");
        }
        //检查是否车辆状态是否租赁中
        RentalRecord rentalRecord = rentalRecords.get(0);
        VehicleMongo ve = mongoTemplate.findOne(Query.query(Criteria.where("id").is(rentalRecord.getVehicleId())), VehicleMongo.class);
        if (!ObjectUtils.allNotNull(ve)){
            throw new RuntimeException("[rental]:数据异常,MySQL中的记录的vehicleId 在 MongoDB中不存在");
        }
        assert ve != null;
        if (!ve.getStatus().equals(1)) {
            throw new RuntimeException("事务");
        }
        //完成订单
        GeoResults<RentalPointMongo> geoResults = mongoTemplate.geoNear(NearQuery.near(lo, la).maxDistance(new Distance(1, Metrics.KILOMETERS)).query(new Query().limit(10)), RentalPointMongo.class);
        List<GeoResult<RentalPointMongo>> content = geoResults.getContent();
        if (CollectionUtils.isEmpty(content)) {
            throw new RuntimeException("事务");
        }
        RentalPointMongo rentalPoint;
        if (content.size() > 0) {
            GeoResult<RentalPointMongo> rentalPointGeoResult = content.get(0);
            rentalPoint = rentalPointGeoResult.getContent();
            rentalRecord.setEndPointId(Long.valueOf(rentalPoint.getId()));
            //租赁点车辆增加
            rentalPoint.setLeftBike(rentalPoint.getLeftBike() + 1);
        } else {
            throw new RuntimeException("事务");
        }
        rentalRecord.setStatus(RentalRecordEnums.FINISH.getCode());
        rentalRecord.setEndTime(new Date());
        rentalRecordService.save(rentalRecord);
        //获取时间计算费用
        Duration duration = new Duration(rentalRecord.getBeginTime().getTime(), rentalRecord.getEndTime().getTime());
        long standardHours = duration.getStandardHours();
        if (standardHours == 0) {
            standardHours = 1;
        }
        HashMap<String, String> res = new HashMap<>();
        Double cost = (double) (standardHours * 10);
        res.put("cost", String.valueOf(cost));
        assert userMongo != null;
        double balance = userMongo.getBalance();
        res.put("balence", String.valueOf(balance));
        if (cost > balance) {
            res.put("toPay", "true");
        }
        //更新租赁点信息
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(rentalPoint.getId())), Update.update("left_bike", rentalPoint.getLeftBike()), rentalPoint.getClass());
        //更新车辆信息
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(ve.getId())), Update.update("status", 0).set("location", rentalPoint.getLocation()).set("pointId", rentalPoint.getId()), Vehicle.class);
        return res;
    }

    @Override
    public boolean updateMysql(Vehicle toMySQL) {
        try {
            vehicleRepository.save(toMySQL);
        }catch (Exception e){
            log.error("[rental]:更新vehicle失败,记录为:{}",JSON.toJSONString(toMySQL));
            return false;
        }
        return true;
    }
}

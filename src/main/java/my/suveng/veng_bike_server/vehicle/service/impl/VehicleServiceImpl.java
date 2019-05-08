package my.suveng.veng_bike_server.vehicle.service.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import my.suveng.veng_bike_server.common.enums.VehicleEnums;
import my.suveng.veng_bike_server.rentalpoint.dao.mysql.RentalPointMapper;
import my.suveng.veng_bike_server.rentalpoint.pojo.mongo.RentalPointMongo;
import my.suveng.veng_bike_server.rentalpoint.pojo.mysql.RentalPoint;
import my.suveng.veng_bike_server.rentalpoint.service.RentalPointService;
import my.suveng.veng_bike_server.user.pojo.mongo.UserMongo;
import my.suveng.veng_bike_server.user.service.UserService;
import my.suveng.veng_bike_server.vehicle.dao.mysql.RentalRecordMapper;
import my.suveng.veng_bike_server.vehicle.dao.mysql.VehicleMapper;
import my.suveng.veng_bike_server.vehicle.pojo.mongo.VehicleMongo;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.RentalRecord;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle;
import my.suveng.veng_bike_server.vehicle.service.VehicleService;
import my.suveng.veng_bike_server.vehicle.vo.BikeStatus;
import my.suveng.veng_bike_server.vehicle.vo.RntalRecordFlag;
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
import org.springframework.util.Assert;
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
    @Autowired
    private RentalPointService rentalPointService;
    @Autowired
    private UserService userService;

    @Override
    public void save(VehicleMongo bike) {
        mongoTemplate.insert(bike);
    }

    @Override
    public List<VehicleMongo> findAll() {
        return mongoTemplate.findAll(VehicleMongo.class, "bike");
    }

    @Override
    public GeoResults<VehicleMongo> findNear(double longitude, double latitude) {
        //指定nearquery 相当于查询条件
        NearQuery nearQuery = NearQuery.near(new Point(longitude, latitude), Metrics.KILOMETERS);
        nearQuery.maxDistance(1).query(new Query().addCriteria(Criteria.where("status").is(0)).limit(20));
        //通过mongo 的geohash算法的接口计算出来。
        return mongoTemplate.geoNear(nearQuery, VehicleMongo.class);
    }

    @Override
    public boolean saveInMysql(Vehicle vehicle) {
        return vehicleMapper.insertSelective(vehicle) >= 1;
    }

    @Override
    public boolean unlock(UserMongo userMongo, VehicleMongo bike) {
        log.info("[vehicle]:userId:{},解锁车辆vehicleId:{}", userMongo.getId(),bike.getId());
        //检查
        bike = mongoTemplate.findById(bike.getId(), VehicleMongo.class);
        userMongo = mongoTemplate.findById(userMongo.getId(), UserMongo.class);
        if (!ObjectUtils.allNotNull(userMongo, bike)) {
            return false;
        }
        Assert.notNull(userMongo, "user不能为空");
        Assert.notNull(bike, "bike不能为空");
        if (!ObjectUtils.allNotNull(bike.getPointid())) {
            return false;
        }
        //检查是否是预留车
        log.info("[vehicle]:检查userId:{},有没有预约记录", userMongo.getId());

        RentalPointMongo rentalPointMongo = mongoTemplate.findById(bike.getPointid(), RentalPointMongo.class);
        if (!ObjectUtils.allNotNull(rentalPointMongo)) {
            return false;
        }
        Assert.notNull(rentalPointMongo, "rentalPoint不能为空");

        List<RentalRecord> rentalRecords = rentalRecordMapper.selectByUserId(userMongo.getId(), 0);
        if (!CollectionUtils.isEmpty(rentalRecords)) {
            if (rentalRecords.size() > 1) {
                log.error("[vehicle]:未知异常!");
                return false;
            }
            if (rentalRecords.size() == 1) {
                RentalRecord rentalRecord1 = rentalRecords.get(0);
                log.info("[vehicle]:userID:{}有预约记录,订单记录id为:{}", userMongo.getId(), rentalRecord1.getRentalid());
                if (rentalRecord1.getVehicleid().equals(bike.getId())) {
                    bike.setStatus(VehicleEnums.LEND.getCode());
                    mongoTemplate.updateFirst(new Query(Criteria.where("id").is(bike.getId())), new Update().set("status", bike.getStatus()), VehicleMongo.class);
                    this.updateMysql(bike.toMySQL());
                    log.info("[vehicle]:更新车辆状态为已租状态,成功,vehicleId:{}", bike.getId());
                    return true;
                } else {
                    log.error("[vehicle]:更新车辆状态为已租状态,失败,vehicleId:{}", bike.getId());
                    return false;
                }
            }
        }
        log.info("[vehicle]:userId:{},没有预约记录!", userMongo.getId());
        //检查车辆状态
        if (bike.getStatus() != 0) {
            return false;
        }
        //检查用户满足条件
        if (userMongo.getBalance() < 0 || userMongo.getDeposit() < 100) {
            return false;
        }
        //创建租赁记录
        boolean res = createRentalRecord(userMongo, bike, rentalPointMongo);
        if (res) {
            log.info("[vehicle]:userId:{},创建租赁记录,成功,vehicleId:{},rentalPoint:{}", userMongo.getId(), bike.getId(), rentalPointMongo.getId());
        } else {
            log.error("[vehicle]:userId:{},创建租赁记录,失败,vehicleId:{},rentalPoint:{}", userMongo.getId(), bike.getId(), rentalPointMongo.getId());
        }
        return res;
    }

    private boolean createRentalRecord(UserMongo userMongo, VehicleMongo bike, RentalPointMongo rentalPointMongo) {
        RentalRecord rentalRecord = new RentalRecord();
        rentalRecord.setIsfinish(RntalRecordFlag.BEGIN.getStatus());
        rentalRecord.setBeginpoint(bike.getPointid());
        rentalRecord.setBegintime(LocalDateTime.now().toDate());
        rentalRecord.setUserid(userMongo.getId());
        rentalRecord.setVehicleid(bike.getId());
        rentalRecordMapper.insertSelective(rentalRecord);
        //修改车辆状态
        bike.setStatus(BikeStatus.RENTED.getStatus());
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(bike.getId())), new Update().set("status", 1), VehicleMongo.class);
        //修改租赁点剩余车辆
        int i = rentalPointMongo.getLeft_bike() - 1;
        if (i < 0) {
            log.error("租赁点有脏数据:{}", JSON.toJSONString(rentalPointMongo));
            return false;
        }
        rentalPointMongo.setLeft_bike(i);
        UpdateResult updateResult = mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(rentalPointMongo.getId())), new Update().set("left_bike", i), RentalPointMongo.class);
        RentalPoint rentalPoint1 = rentalPointMongo.toMySQL();
        rentalPointMapper.updateByPrimaryKeySelective(rentalPoint1);
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
        List<RentalRecord> rentalRecords = rentalRecordMapper.selectByUserId(userId, 0);
        if (CollectionUtils.isEmpty(rentalRecords)) {
            throw new RuntimeException("事务");
        }
        if (rentalRecords.size() != 1) {
            throw new RuntimeException("事务");
        }
        //检查是否车辆状态是否租赁中
        RentalRecord rentalRecord = rentalRecords.get(0);
        VehicleMongo ve = mongoTemplate.findOne(Query.query(Criteria.where("id").is(rentalRecord.getVehicleid())), VehicleMongo.class);
        if (!ve.getStatus().equals(1)) {
            throw new RuntimeException("事务");
        }
        //完成订单
        GeoResults<RentalPointMongo> geoResults = mongoTemplate.geoNear(NearQuery.near(lo, la).maxDistance(new Distance(1, Metrics.KILOMETERS)).query(new Query().limit(10)), RentalPointMongo.class);
        List<GeoResult<RentalPointMongo>> content = geoResults.getContent();
        if (CollectionUtils.isEmpty(content)) {
            throw new RuntimeException("事务");
        }
        RentalPointMongo rentalPointMongo;
        if (content.size() > 0) {
            GeoResult<RentalPointMongo> rentalPointGeoResult = content.get(0);
            rentalPointMongo = rentalPointGeoResult.getContent();
            rentalRecord.setEndpoint(rentalPointMongo.getId());
            //租赁点车辆增加
            rentalPointMongo.setLeft_bike(rentalPointMongo.getLeft_bike() + 1);
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
        double balance = userMongo.getBalance();
        res.put("balence", String.valueOf(balance));
        boolean toPay = false;
        if (cost > balance) {
            toPay=true;
            res.put("toPay", "true");
        }
        //进行扣费
        if (!toPay){
            userMongo.setBalance(balance-cost);
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(userMongo.getId())), Update.update("deposit",userMongo.getBalance()),UserMongo.class);
            if (userService.save(userMongo.toMysql())){
                log.info("[vehicle]扣费成功");
            }
        }
        //更新租赁点信息
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(rentalPointMongo.getId())), Update.update("left_bike", rentalPointMongo.getLeft_bike()), rentalPointMongo.getClass());
        rentalPointService.update(rentalPointMongo.toMySQL());
        //更新车辆信息
        ve.setStatus(VehicleEnums.WAIT.getCode());
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(ve.getId())), Update.update("status", ve.getStatus()).set("location", rentalPointMongo.getLocation()).set("pointid", rentalPointMongo.getId()), VehicleMongo.class);
        this.updateMysql(ve.toMySQL());
        return res;
    }

    @Override
    public boolean updateMysql(Vehicle toMySQL) {
        if (vehicleMapper.updateByPrimaryKeySelective(toMySQL) < 1) {
            return false;
        }
        return true;
    }
}

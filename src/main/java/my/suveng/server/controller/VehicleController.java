package my.suveng.server.controller;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import my.suveng.server.common.enums.RentalRecordEnums;
import my.suveng.server.common.enums.VehicleStatusEnums;
import my.suveng.server.common.response.Result;
import my.suveng.server.common.response.ResultBuilder;
import my.suveng.server.common.response.ResultEnums;
import my.suveng.server.modules.rental.model.po.*;
import my.suveng.server.modules.rental.service.RentalPointService;
import my.suveng.server.modules.rental.service.RentalRecordService;
import my.suveng.server.modules.rental.service.VehicleService;
import my.suveng.server.modules.user.model.po.UserMongo;
import my.suveng.server.modules.user.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-23 下午5:54
 */
@Controller
@Slf4j
public class VehicleController {
    @Autowired
    private RentalRecordService rentalRecordService;
    @Resource
    private VehicleService vehicleService;
    @Resource
    private RentalPointService rentalPointService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private UserService userService;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello";
    }


    /**
     * 添加测试数据
     * @return string
     */
    @GetMapping("/addTestData")
    @ResponseBody
    public String addTestData() {
        List<RentalPointMongo> rentalPointMongoList = rentalPointService.findAll();
        if (!ObjectUtils.allNotNull(rentalPointMongoList)) {
            return "success";
        }
        for (RentalPointMongo
                rentalPoint : rentalPointMongoList) {
            for (int i = 0; i < 30; i++) {
                Vehicle vehicle = new Vehicle();
                VehicleMongo mongoVe = new VehicleMongo();
                String id = UUID.randomUUID().toString().toLowerCase();
                mongoVe.setId(id);
                vehicle.setVehicleId(id);
                vehicle.setQrCode(id);
                mongoVe.setQrCode(id);
                double[] location = rentalPoint.getLocation();
                mongoVe.setLocation(location);
                vehicle.setLongitude(location[0]);
                vehicle.setLatitude(location[1]);

                mongoVe.setPointId(rentalPoint.getId());
                vehicle.setPointId(rentalPoint.getId());
                vehicleService.save(mongoVe);
                vehicleService.saveInMysql(vehicle);
            }
        }
        return "success";
    }

    /**
     * 查找全部车辆
     * @return List<Vehicle>
     */
    @GetMapping("/vehicles")
    @ResponseBody
    public List<VehicleMongo> vehicles() {
        List<VehicleMongo> list = vehicleService.findAll();
        log.info("[vehicle]:vehicles:{}", JSON.toJSONString(list));
        return list;
    }

    /**
     * 查找附近车辆
     * @param longitude 经度
     * @param latitude 纬度
     * @return GeoResults<Vehicle>
     */
    @GetMapping("/vehicles/near")
    @ResponseBody
    public GeoResults<Vehicle> findNearVehicles(double longitude, double latitude) {
        return vehicleService.findNear(longitude, latitude);
    }

    /**
     * 返回车辆列表视图
     * @return string
     */
    @RequestMapping("/vehicle_list")
    public String toVehicleList() {
        return "vehicle/list";
    }

    /**
     * 预约租车
     * @param longitude 经度
     * @param latitude 纬度
     * @param userId uid
     * @return result
     */
    @RequestMapping("/vehicle/reservate")
    @ResponseBody
    @Transactional
    public Result reservate(String longitude, String latitude, String userId) {
        if (!ObjectUtils.allNotNull(latitude, longitude, userId)) {
            return ResultBuilder.buildSimpleErrorResult();
        }
        //只能预约一辆
        if (!userService.checkRentalRecord(userId)) {
            return ResultBuilder.buildSimpleErrorResult();
        }

        log.info("[vehicleMongo]:reservate:参数:longitude:{},latitude:{},userId:{}",longitude,latitude,userId);

        GeoResults<RentalPoint> near = null;
        double lo = Double.parseDouble(longitude);
        double la = Double.parseDouble(latitude);
        NearQuery query = NearQuery.near(new Point(lo, la), Metrics.KILOMETERS).maxDistance(new Distance(1, Metrics.KILOMETERS)).query(new Query().limit(20));
        //查找最近的的租车点
        GeoResults<RentalPointMongo> geoResults = mongoTemplate.geoNear(query, RentalPointMongo.class);
        RentalPointMongo rentalPoint = geoResults.getContent().get(0).getContent();
        rentalPoint.setLeftBike(rentalPoint.getLeftBike() - 1);

        //更新mongo车辆状态
        List<VehicleMongo> vehicleMongos = mongoTemplate.find(Query.query(Criteria.where("pointId").is(rentalPoint.getId()).and("status").is(0)), VehicleMongo.class);
        if (CollectionUtils.isEmpty(vehicleMongos)) {
            return ResultBuilder.buildSimpleErrorResult();
        }
        VehicleMongo vehicleMongo = vehicleMongos.get(0);
        vehicleMongo.setStatus(VehicleStatusEnums.BOOKED.getCode());
        log.info("[vehicle]:预约成功,修改车辆状态成功,记录:{}",JSON.toJSONString(vehicleMongo));
        System.out.println(vehicleMongo.getId());
        if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(vehicleMongo.getId())), Update.update("status", vehicleMongo.getStatus()), VehicleMongo.class).getModifiedCount() < 1) {
            throw new RuntimeException("[vehicle]:更新mongo车辆状态失败！");
        }
        if (!vehicleService.updateMysql(vehicleMongo.toMySQL())) {
            throw new RuntimeException("更新mysql车辆状态失败！");
        }
        //生成租赁记录
        RentalRecord rentalRecord = new RentalRecord();
        rentalRecord.setStatus(RentalRecordEnums.NOT_FINISH.getCode());
        rentalRecord.setBeginPointId(vehicleMongo.getPointId());
        rentalRecord.setBeginTime(LocalDateTime.now().toDate());
        rentalRecord.setUserId(userId);
        rentalRecord.setVehicleId(vehicleMongo.getId());
        try {
            rentalRecordService.save(rentalRecord);
        }catch (Exception e){
            log.error("[vehicle]:预约租车,租赁记录,插入失败.记录为:{}",JSON.toJSONString(rentalRecord));
            throw new RuntimeException("生成租赁记录失败！");
        }
        if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(rentalPoint.getId())), new Update().set("leftBike", rentalPoint.getLeftBike()), RentalPointMongo.class).getModifiedCount() < 1) {
            throw new RuntimeException("更新mongo租赁点失败！");
        }
        return ResultBuilder.buildSimpleSuccessResult();
    }


    /**
     * 解锁车辆
     * @param vehicleNo 单车id
     * @param id userid
     * @return string
     */
    @PostMapping("/vehicle/unlock")
    @ResponseBody
    public String unlock(String vehicleNo, String id) {
        try {
            VehicleMongo vehicleMongo = new VehicleMongo();
            UserMongo userMongo = new UserMongo();
            vehicleMongo.setId(vehicleNo);
            userMongo.setId(id);
            if (!vehicleService.unlock(userMongo, vehicleMongo)) {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    /**
     * 上锁功能和计费功能:更新车辆状态，租车记录状态
     * @param userId userId
     * @param longitude 经度
     * @param latitude 纬度
     * @return result
     */
    @RequestMapping("/vehicle/lock")
    @ResponseBody
    public Result lock(String userId,String longitude,String latitude) {
        if (StringUtils.isBlank(userId)){
            return ResultBuilder.buildSimpleIllegalArgumentError();
        }
        try {
            Map lock = vehicleService.lock(userId, Double.valueOf(longitude), Double.valueOf(latitude));
            if (ObjectUtils.allNotNull(lock)){
                return ResultBuilder.build(ResultEnums.SIMPLE_SUCCESS,lock);
            }
        } catch (Exception e) {
            return ResultBuilder.buildSimpleErrorResult();
        }
        return ResultBuilder.buildSimpleIllegalArgumentError();
    }
    @RequestMapping("/re")
    @ResponseBody
    public Result re(){
        RentalRecord rentalRecord = JSON.parseObject("{\"beginPointId\":2000000,\"beginTime\":1556864446846,\"cellStyleMap\":{},\"status\":0,\"userId\":\"o3mG94kJ8eJYMOJOmdduOskJo6ZQ\",\"vehicleId\":\"54487b66-27c8-42d2-88ce-d8f8404efc0c\"}", RentalRecord.class);
        rentalRecordService.save(rentalRecord);
        return ResultBuilder.buildSimpleSuccessResult();
    }

}

package my.suveng.veng_bike_server.vehicle.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import my.suveng.veng_bike_server.common.response.Result;
import my.suveng.veng_bike_server.common.response.ResultBuilder;
import my.suveng.veng_bike_server.common.response.ResultEnums;
import my.suveng.veng_bike_server.rentalpoint.pojo.mongo.RentalPointMongo;
import my.suveng.veng_bike_server.rentalpoint.pojo.mysql.RentalPoint;
import my.suveng.veng_bike_server.rentalpoint.service.RentalPointService;
import my.suveng.veng_bike_server.user.pojo.mongo.UserMongo;
import my.suveng.veng_bike_server.user.service.UserService;
import my.suveng.veng_bike_server.vehicle.dao.mysql.RentalRecordMapper;
import my.suveng.veng_bike_server.vehicle.pojo.mongo.VehicleMongo;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.RentalRecord;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle;
import my.suveng.veng_bike_server.vehicle.service.VehicleService;
import my.suveng.veng_bike_server.vehicle.vo.RntalRecordFlag;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
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

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-23 下午5:54
 */
@Api(value = "vehicle", tags = {"vehicle接口模块"}, description = "动车相关")
@Controller
@Slf4j
public class VehicleController {
    @Resource
    private VehicleService vehicleService;
    @Resource
    private RentalPointService rentalPointService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private RentalRecordMapper rentalRecordMapper;
    @Resource
    private UserService userService;

    @GetMapping("/hello")
    @ResponseBody
    @ApiOperation(value = "测试")
    public String hello() {
        return "hello";
    }


    @GetMapping("/addTestData")
    @ResponseBody
    @ApiOperation(value = "添加测试数据")
    @Deprecated
    public String addTestData() {
//        List<RentalPoint> all = rentalPointService.findAll();
//        if (!ObjectUtils.allNotNull(all)) {
//            return "success";
//        }
//        for (RentalPoint rentalPoint : all) {
//            for (int i = 0; i < 30; i++) {
//                my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle vehicle = new my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle();
//                Vehicle mongoVe = new Vehicle();
//                String id = UUID.randomUUID().toString().toLowerCase();
//                mongoVe.setId(id);
//                vehicle.setVehicleid(id);
//                vehicle.setQrcode(id);
//                mongoVe.setQrCode(id);
//                double[] location = rentalPoint.getLocation();
//                mongoVe.setLocation(location);
//                vehicle.setLongitude(location[0]);
//                vehicle.setLatitude(location[1]);
//
//                mongoVe.setPointid(rentalPoint.getId());
//                vehicle.setPointid(rentalPoint.getId());
//                vehicleService.save(mongoVe);
//                vehicleService.saveInMysql(vehicle);
//            }
//        }
        return "接口已经废弃,请手动添加租车点";
    }

    @GetMapping("/vehicles")
    @ResponseBody
    @ApiOperation(value = "查找全部车辆")
    public List<VehicleMongo> vehicles() {
        List<VehicleMongo> list = vehicleService.findAll();
        System.out.println(list);
        return list;
    }

    @GetMapping("/vehicles/near")
    @ResponseBody
    @ApiOperation(value = "查找附近车辆")
    public GeoResults<VehicleMongo> findNearVehicles(double longitude, double latitude) {
        return vehicleService.findNear(longitude, latitude);
    }

    @RequestMapping("/vehicle_list")
    @ApiOperation(value = "返回车辆列表视图")
    public String toVehicleList() {
        return "vehicle/list";
    }

    @RequestMapping("/vehicle/reservate")
    @ApiOperation(value = "预约租车")
    @ResponseBody
    @Transactional
    public Result reservate(String longitude, String latitude, String userId) {
        log.info("[vehicle]:预约租车,入参:longitude:{},latitude:{},userId:{}",longitude,latitude,userId);
        if (!ObjectUtils.allNotNull(latitude, longitude, userId)) {
            log.error("[vehicle]:参数校验失败");
            return ResultBuilder.buildSimpleErrorResult();
        }
        //只能预约一辆
        if (!userService.checkRentalRecord(userId)) {
            log.error("[vehicle]:只能预约一辆,userId:{}",userId);
            return ResultBuilder.buildSimpleErrorResult();
        }
        GeoResults<RentalPointMongo> near = null;
        double lo = Double.parseDouble(longitude);
        double la = Double.parseDouble(latitude);

        NearQuery query = NearQuery.near(new Point(lo, la), Metrics.KILOMETERS).maxDistance(new Distance(1, Metrics.KILOMETERS)).query(new Query().limit(20));
        //查找最近的的租车点
        GeoResults<RentalPointMongo> geoResults = mongoTemplate.geoNear(query, RentalPointMongo.class);
        RentalPointMongo rentalPointMongo = geoResults.getContent().get(0).getContent();
        rentalPointMongo.setLeft_bike(rentalPointMongo.getLeft_bike() - 1);
        log.info("[vehicle]:查询最近的租车点:mongoRentalPoint:{}", JSON.toJSONString(rentalPointMongo));
        //更新mongo车辆状态
        List<VehicleMongo> vehicleMongos = mongoTemplate.find(Query.query(Criteria.where("pointid").is(rentalPointMongo.getId()).and("status").is(0)), VehicleMongo.class);
        if (CollectionUtils.isEmpty(vehicleMongos)) {
            return ResultBuilder.buildSimpleErrorResult();
        }
        VehicleMongo vehicleMongo = vehicleMongos.get(0);
        log.info("[vehicle]:租赁点:{},的空闲车辆:{},设置状态为2(预约)", rentalPointMongo.getId(), vehicleMongo.getId());
        vehicleMongo.setStatus(2);
        if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(vehicleMongo.getId())), Update.update("status", vehicleMongo.getStatus()), VehicleMongo.class).getModifiedCount() < 1) {
            log.error("[vehicle]:更新mongo车辆状态失败!");
            throw new RuntimeException("更新mongo车辆状态失败！");
        }
        log.info("[vehicle]:更新mongo车辆状态,成功,id:{}", vehicleMongo.getId());
        Vehicle toMySQL = vehicleMongo.toMySQL();
        if (!vehicleService.updateMysql(toMySQL)) {
            log.error("[vehicle]:更新mysql车辆状态失败,记录为:{}",toMySQL);
            throw new RuntimeException("更新mysql车辆状态失败！");
        }
        log.info("[vehicle]:更新mysql车辆状态,成功,id:{}",toMySQL.getVehicleid());

        //生成租赁记录
        RentalRecord rentalRecord = new RentalRecord();
        rentalRecord.setIsfinish(RntalRecordFlag.BEGIN.getStatus());
        rentalRecord.setBeginpoint(vehicleMongo.getPointid());
        rentalRecord.setBegintime(LocalDateTime.now().toDate());
        rentalRecord.setUserid(userId);
        rentalRecord.setVehicleid(vehicleMongo.getId());
        if (rentalRecordMapper.insertSelective(rentalRecord) < 1) {
            log.error("[vehicle]:生成租赁记录失败,记录为:{}",JSON.toJSONString(rentalRecord));
            throw new RuntimeException("生成租赁记录失败！");
        }
        log.info("[vehicle]:生成租赁记录,成功");

        //更新租赁点剩余车辆数
        if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(rentalPointMongo.getId())), new Update().set("left_bike", rentalPointMongo.getLeft_bike()), RentalPointMongo.class).getModifiedCount() < 1) {
            log.error("[vehicle]:更新mongo租赁点剩余车辆数:{},失败,id:{}", rentalPointMongo.getLeft_bike(), rentalPointMongo.getId());
            throw new RuntimeException("更新mongo租赁点失败！");
        }
        RentalPoint rentalPointMysql = rentalPointMongo.toMySQL();
        if (rentalPointService.update(rentalPointMysql)){
            log.info("[vehicle]:更新剩余车辆成功");
        }else {
            log.info("[vehicle]:更新剩余车辆失败,记录:{}", JSON.toJSONString(rentalPointMysql));
            throw new RuntimeException("更新剩余车辆失败!");
        }

        log.info("[vehicle]:userId:{},预约成功!,车辆id:{},租赁点id:{}",userId,rentalRecord.getVehicleid(),rentalRecord.getBeginpoint());
        return ResultBuilder.buildSimpleSuccessResult();
    }


    @PostMapping("/vehicle/unlock")
    @ResponseBody
    @ApiOperation(value = "解锁车辆")
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
            return "fail";
        }
        return "success";
    }

    @RequestMapping("/vehicle/lock")
    @ResponseBody
    @ApiOperation(value = "上锁功能和计费功能:更新车辆状态，租车记录状态")
    public Result lock(String userId,String longitude,String latitude) {
        log.info("[vehicle]:上锁和计费,入参为:userId:{},longitude:{},latitude:{}",userId,longitude,latitude);
        if (StringUtils.isBlank(userId)){
            log.error("[vehicle]:参数错误");
            return ResultBuilder.buildSimpleIllegalArgumentError();
        }
        try {
            Map lock = vehicleService.lock(userId, Double.valueOf(longitude), Double.valueOf(latitude));
            if (ObjectUtils.allNotNull(lock)){
                log.info("[vehicle]:上锁和计费成功:userId:{}",userId);
                return ResultBuilder.build(ResultEnums.SIMPLE_SUCCESS,lock);
            }
        } catch (Exception e) {
            return ResultBuilder.buildSimpleErrorResult();
        }
        return ResultBuilder.buildSimpleIllegalArgumentError();
    }

}

package my.suveng.veng_bike_server.vehicle.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import my.suveng.veng_bike_server.common.response.Result;
import my.suveng.veng_bike_server.common.response.ResultBuilder;
import my.suveng.veng_bike_server.rentalpoint.pojo.mongo.RentalPoint;
import my.suveng.veng_bike_server.rentalpoint.service.RentalPointService;
import my.suveng.veng_bike_server.user.pojo.mongo.User;
import my.suveng.veng_bike_server.vehicle.dao.mysql.RentalRecordMapper;
import my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.RentalRecord;
import my.suveng.veng_bike_server.vehicle.service.VehicleService;
import my.suveng.veng_bike_server.vehicle.vo.RntalRecordFlag;
import org.apache.commons.lang3.ObjectUtils;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.beans.Transient;
import java.util.List;
import java.util.UUID;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-23 下午5:54
 */
@Api(value = "vehicle", tags = {"vehicle接口模块"}, description = "动车相关")
@Controller
public class VehicleController {
    @Resource
    private VehicleService vehicleService;
    @Autowired
    RentalPointService rentalPointService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private RentalRecordMapper rentalRecordMapper;

    @GetMapping("/hello")
    @ResponseBody
    @ApiOperation(value = "测试")
    public String hello() {
        return "hello";
    }


    @GetMapping("/addTestData")
    @ResponseBody
    @ApiOperation(value = "添加测试数据")
    public String addTestData() {
        List<RentalPoint> all = rentalPointService.findAll();
        if (!ObjectUtils.allNotNull(all)) {
            return "success";
        }
        for (RentalPoint rentalPoint : all) {
            for (int i = 0; i < 300; i++) {
                my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle vehicle = new my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle();
                Vehicle mongoVe = new Vehicle();
                String id = UUID.randomUUID().toString().toLowerCase();
                mongoVe.setId(id);
                vehicle.setVehicleid(id);
                vehicle.setQrcode(id);
                mongoVe.setQrCode(id);
                double[] location = rentalPoint.getLocation();
                mongoVe.setLocation(location);
                vehicle.setLongitude(location[0]);
                vehicle.setLatitude(location[1]);

                mongoVe.setPointid(rentalPoint.getId());
                vehicle.setPointid(rentalPoint.getId());
                vehicleService.save(mongoVe);
                vehicleService.saveInMysql(vehicle);
            }
        }
        return "success";
    }

    @GetMapping("/vehicles")
    @ResponseBody
    @ApiOperation(value = "查找全部车辆")
    public List<Vehicle> vehicles() {
        List<Vehicle> list = vehicleService.findAll();
        System.out.println(list);
        return list;
    }

    @GetMapping("/vehicles/near")
    @ResponseBody
    @ApiOperation(value = "查找附近车辆")
    public GeoResults<Vehicle> findNearVehicles(double longitude, double latitude) {
        GeoResults<Vehicle> near = vehicleService.findNear(longitude, latitude);
        return near;
    }

    @RequestMapping("/vehicle_list")
    @ApiOperation(value = "返回车辆列表视图")
    public String to_vehicle_list() {
        return "vehicle/list";
    }

    @RequestMapping("/vehicle/reservate")
    @ApiOperation(value = "预约租车")
    @ResponseBody
    @Transient
    public Result reservate(String longitude, String latitude, String userId) {
        if (!ObjectUtils.allNotNull(latitude, longitude, userId)) {
            return ResultBuilder.buildSimpleErrorResult();
        }
        System.out.println(latitude + "-" + longitude + "-" + userId);
        GeoResults<RentalPoint> near = null;
        Double lo = Double.valueOf(longitude);
        Double la = Double.valueOf(latitude);
        NearQuery query = NearQuery.near(new Point(lo, la), Metrics.KILOMETERS).maxDistance(new Distance(1, Metrics.KILOMETERS)).query(new Query().limit(20));
        //查找最近的的租车点
        GeoResults<RentalPoint> geoResults = mongoTemplate.geoNear(query, RentalPoint.class);
        RentalPoint rentalPoint = geoResults.getContent().get(0).getContent();
        rentalPoint.setLeft_bike(rentalPoint.getLeft_bike() - 1);

        //更新mongo车辆状态
        List<Vehicle> vehicles = mongoTemplate.find(Query.query(Criteria.where("pointid").is(rentalPoint.getId()).and("status").is(0)), Vehicle.class);
        if (CollectionUtils.isEmpty(vehicles)) {
            return ResultBuilder.buildSimpleErrorResult();
        }
        Vehicle vehicle = vehicles.get(0);
        vehicle.setStatus(2);
        if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(vehicle.getId())), Update.update("status", vehicle.getStatus()), Vehicle.class).getModifiedCount() < 1) {
            throw new RuntimeException("更新mongo车辆状态失败！");
        }
        if (!vehicleService.updateMysql(vehicle.toMySQL())) {
            throw new RuntimeException("更新mysql车辆状态失败！");
        }
        //生成租赁记录
        RentalRecord rentalRecord = new RentalRecord();
        rentalRecord.setIsfinish(RntalRecordFlag.BEGIN.getStatus());
        rentalRecord.setBeginpoint(vehicle.getPointid());
        rentalRecord.setBegintime(LocalDateTime.now().toDate());
        rentalRecord.setUserid(userId);
        rentalRecord.setVehicleid(vehicle.getId());
        if (rentalRecordMapper.insertSelective(rentalRecord) < 1) {
            throw new RuntimeException("生成租赁记录失败！");
        }
        if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(rentalPoint.getId())), new Update().set("left_bike", rentalPoint.getLeft_bike()), RentalPoint.class).getModifiedCount() < 1) {
            throw new RuntimeException("更新mongo租赁点失败！");
        }


        return ResultBuilder.buildSimpleSuccessResult();
    }


    @PostMapping("/vehicle/unlock")
    @ResponseBody
    @ApiOperation(value = "解锁车辆")
    public String unlock(String vehicleNo, String id) {
        try {
            Vehicle vehicle = new Vehicle();
            User user = new User();
            vehicle.setId(vehicleNo);
            user.setId(id);
            if (!vehicleService.unlock(user, vehicle)) {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    //TODO：还车业务：上锁功能 和 计费功能 更新MySQL车辆状态， 租车记录状态  更新mongo的车辆状态租车记录
    @PostMapping("/vehicle/lock")
    @ResponseBody
    @ApiOperation(value = "上锁功能和计费功能:更新车辆状态，租车记录状态")
    public String lock() {
        try {
            User user = new User();
            Vehicle vehicle = new Vehicle();
            vehicleService.lock(user, vehicle);
        } catch (Exception e) {
            return "fail";
        }
        return "success";
    }

}
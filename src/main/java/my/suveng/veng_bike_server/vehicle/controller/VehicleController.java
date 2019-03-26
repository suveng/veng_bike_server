package my.suveng.veng_bike_server.vehicle.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import my.suveng.veng_bike_server.user.pojo.mongo.User;
import my.suveng.veng_bike_server.vehicle.pojo.mongo.RentalRecord;
import my.suveng.veng_bike_server.vehicle.pojo.mongo.Vehicle;
import my.suveng.veng_bike_server.vehicle.service.VehicleService;
import org.springframework.data.geo.GeoResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    @GetMapping("/hello")
    @ResponseBody
    @ApiOperation(value = "测试")
    public String hello() {
        return "hello";
    }



    @GetMapping("/vehicles")
    @ResponseBody
    @ApiOperation(value = "查找全部单车")
    public List<Vehicle> vehicles() {
        List<Vehicle> list = vehicleService.findAll();
        System.out.println(list);
        return list;
    }

    @GetMapping("/vehicles/near")
    @ResponseBody
    @ApiOperation(value = "查找附近单车")
    public GeoResults<Vehicle> findNearVehicles(double longitude, double latitude) {
        GeoResults<Vehicle> near = vehicleService.findNear(longitude, latitude);
        return near;
    }

    @GetMapping("/vehicle_list")
    @ApiOperation(value = "返回单车列表视图")
    public String to_vehicle_list() {
        return "vehicle/list";
    }


    @PostMapping("/vehicle/unlock")
    @ResponseBody
    @ApiOperation(value = "解锁车辆")
    public String unlock(@RequestParam(value = "vehicleNo") String vehicleNo,
                         @RequestParam(value = "id") String id) {
        try {
            Vehicle vehicle=new Vehicle();
            User user=new User();
            vehicle.setId(vehicleNo);
            user.setId(id);
            vehicleService.unlock(user,vehicle);
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    //TODO：还车业务：上锁功能 和 计费功能 更新MySQL单车状态， 租车记录状态  更新mongo的单车状态租车记录
    @PostMapping("/vehicle/lock")
    @ResponseBody
    @ApiOperation(value = "上锁功能和计费功能:更新单车状态，租车记录状态")
    public String lock() {
        try {
            RentalRecord rentalRecord=new RentalRecord();
            User user=new User();
            Vehicle vehicle=new Vehicle();
            vehicleService.lock(user,vehicle);
        }catch (Exception e){
            return "fail";
        }
        return "success";
    }

}

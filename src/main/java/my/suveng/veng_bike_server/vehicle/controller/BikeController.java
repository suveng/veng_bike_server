package my.suveng.veng_bike_server.vehicle.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import my.suveng.veng_bike_server.vehicle.pojo.mongo.Bike;
import my.suveng.veng_bike_server.vehicle.service.BikeService;
import org.springframework.data.geo.GeoResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-23 下午5:54
 */
@Api(value="bike", tags={"bike接口模块"},description = "动车相关")
@Controller
public class BikeController {
    @Resource
    BikeService bikeService;

    @GetMapping("/hello")
    @ResponseBody
    @ApiOperation(value = "测试")
    public String hello() {
        return "hello";
    }

    @PostMapping("/save")
    @ResponseBody
    @ApiOperation(value = "手动生成单车")
    public String save(@RequestBody Bike bike) {
        bikeService.save(bike);
        return "succ";
    }

    @GetMapping("/bikes")
    @ResponseBody
    @ApiOperation(value = "查找全部单车")
    public List<Bike> bikes() {
        List<Bike> list=bikeService.findAll();
        System.out.println(list);
        return list;
    }
    @GetMapping("/bikes/near")
    @ResponseBody
    @ApiOperation(value = "查找附近单车")
    public GeoResults<Bike> findNearBikes(double longitude, double latitude) {
        GeoResults<Bike> near = bikeService.findNear(longitude, latitude);
        return near;
    }

    @GetMapping("/bike_list")
    @ApiOperation(value = "返回单车列表视图")
    public String to_bike_list(){
        return "bike/list";
    }




}

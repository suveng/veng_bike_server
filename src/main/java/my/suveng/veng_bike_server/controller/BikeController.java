package my.suveng.veng_bike_server.controller;

import io.swagger.annotations.Api;
import my.suveng.veng_bike_server.pojo.Bike;
import my.suveng.veng_bike_server.service.BikeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-23 下午5:54
 */
@Api(value="bike", tags="bike接口模块")
@Controller
public class BikeController {
    @Resource
    BikeService bikeService;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello";
    }

    @PostMapping("/save")
    @ResponseBody
    public String save(@RequestBody Bike bike) {
        bikeService.save(bike);
        return "succ";
    }

    @GetMapping("/bikes")
    @ResponseBody
    public List<Bike> bikes() {
        List<Bike> list=bikeService.findAll();
        System.out.println(list);
        return list;
    }
    @GetMapping("/bike_list")
    public String to_bike_list(){
        return "bike/list";
    }


}

package my.suveng.veng_bike_server.controller;

import my.suveng.veng_bike_server.dao.BikeMapper;
import my.suveng.veng_bike_server.pojo.Bike;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-23 下午5:54
 */
@Controller
public class BikeController {
    @Resource
    BikeMapper bikeMapper;

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello";
    }

    @RequestMapping("/save")
    @ResponseBody
    public String save(@RequestBody Bike bike){
        bikeMapper.insertSelective(bike);
        return "succ";
    }
}

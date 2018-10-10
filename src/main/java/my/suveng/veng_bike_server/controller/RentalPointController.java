package my.suveng.veng_bike_server.controller;

import io.swagger.annotations.ApiOperation;
import my.suveng.veng_bike_server.pojo.RentalPoint;
import my.suveng.veng_bike_server.service.RentalPointService;
import org.springframework.data.geo.GeoResults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * create at 2018/10/10
 * author: suveng
 * email: suveng@163.com
 **/
@RestController
public class RentalPointController {
    @Resource
    RentalPointService rentalPointService;

    /**
     * 手动创建租车点
     * @param rentalPoint 租车点
     */
    @PostMapping("/rental/save")
    @ApiOperation(value = "手动创建租车点")
    public void save(@RequestBody RentalPoint rentalPoint){
        rentalPointService.save(rentalPoint);
    }

    /**
     * 查询所有租车点
     * @return 所有租车点
     */
    @GetMapping("/rental/findAll")
    @ApiOperation(value = "查询所有租车点")
    public List<RentalPoint> findAllRentals(){
        return rentalPointService.findAll();
    }

    /**
     * 查找附近租车点
     * @param longitude 经度
     * @param latitude 纬度
     * @return 附近10公里的租车点
     */
    @GetMapping("/rental/findNearRentals")
    @ApiOperation(value = "附近10公里的租车点")
    public GeoResults<RentalPoint> findNearRentals(double longitude, double latitude) {
        GeoResults<RentalPoint> near = rentalPointService.findNear(longitude, latitude);
        return near;
    }
}

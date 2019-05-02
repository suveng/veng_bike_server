package my.suveng.server.controller;

import io.swagger.annotations.ApiOperation;
import my.suveng.server.rentalpoint.pojo.mongo.RentalPoint;
import my.suveng.server.rentalpoint.service.RentalPointService;
import my.suveng.server.vehicle.pojo.mongo.Vehicle;
import my.suveng.server.vehicle.service.VehicleService;
import org.springframework.data.geo.GeoResults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * create at 2018/10/10
 * author: suveng
 * email: suveng@163.com
 **/
@RestController
public class RentalPointController {
    @Resource
    RentalPointService rentalPointService;
    @Resource
    VehicleService bikeService;

    /**
     * 手动创建租车点
     * @param rentalPoint 租车点
     */
    @PostMapping("/rental/save")
    @ApiOperation(value = "手动创建租车点")
    public void save(@RequestBody RentalPoint rentalPoint){
        rentalPoint.setId(UUID.randomUUID().toString().toLowerCase().replace("-", ""));
        rentalPoint.setLeft_bike(300);
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
    @ApiOperation(value = "附近1公里的租车点")
    public GeoResults<RentalPoint> findNearRentals(double longitude, double latitude) {
        GeoResults<RentalPoint> near = rentalPointService.findNear(longitude, latitude);
        return near;
    }

    @PostMapping("/rental/generate")
    public void generateVehicle(){
        int bikeNo=1000001;
        List<RentalPoint> all = rentalPointService.findAll();
        for (RentalPoint rentalPoint: all){
            for (int i = 0; i < 300; i++) {
                String No=String.valueOf(bikeNo);
                double longitude=rentalPoint.getLocation()[0];
                double latitude = rentalPoint.getLocation()[1];
                String pointid=rentalPoint.getId();
                bikeService.save(new Vehicle(No,No,rentalPoint.getLocation(),0,pointid));
                bikeService.saveInMysql(new my.suveng.server.vehicle.pojo.mysql.Vehicle(No,No,longitude,latitude,0,0,pointid));
                bikeNo++;
            }
        }
    }
}
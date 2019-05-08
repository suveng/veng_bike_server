package my.suveng.veng_bike_server.rentalpoint.controller;

import io.swagger.annotations.ApiOperation;
import my.suveng.veng_bike_server.rentalpoint.pojo.mongo.RentalPointMongo;
import my.suveng.veng_bike_server.rentalpoint.service.RentalPointService;
import my.suveng.veng_bike_server.vehicle.pojo.mongo.VehicleMongo;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.Vehicle;
import my.suveng.veng_bike_server.vehicle.service.VehicleService;
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
     * @param rentalPointMongo 租车点
     */
    @PostMapping("/rental/save")
    @ApiOperation(value = "手动创建租车点")
    public void save(@RequestBody RentalPointMongo rentalPointMongo) {
        rentalPointMongo.setId(UUID.randomUUID().toString().toLowerCase().replace("-", ""));
        rentalPointMongo.setLeft_bike(30);
        rentalPointService.save(rentalPointMongo);
    }

    /**
     * 查询所有租车点
     * @return 所有租车点
     */
    @GetMapping("/rental/findAll")
    @ApiOperation(value = "查询所有租车点")
    public List<RentalPointMongo> findAllRentals() {
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
    public GeoResults<RentalPointMongo> findNearRentals(double longitude, double latitude) {
        GeoResults<RentalPointMongo> near = rentalPointService.findNear(longitude, latitude);
        return near;
    }

    @PostMapping("/rental/generate")
    public void generateVehicle() {
        int bikeNo = 1000001;
        List<RentalPointMongo> all = rentalPointService.findAll();
        for (RentalPointMongo rentalPointMongo : all) {
            for (int i = 0; i < 300; i++) {
                String No = String.valueOf(bikeNo);
                double longitude = rentalPointMongo.getLocation()[0];
                double latitude = rentalPointMongo.getLocation()[1];
                String pointid = rentalPointMongo.getId();
                bikeService.save(new VehicleMongo(No, No, rentalPointMongo.getLocation(), 0, pointid));
                bikeService.saveInMysql(new Vehicle(No, No, longitude, latitude, 0, 0, pointid));
                bikeNo++;
            }
        }
    }
}

package my.suveng.server.controller;

import lombok.extern.slf4j.Slf4j;
import my.suveng.server.common.enums.VehicleStatusEnums;
import my.suveng.server.modules.rental.model.po.RentalPoint;
import my.suveng.server.modules.rental.model.po.RentalPointMongo;
import my.suveng.server.modules.rental.model.po.VehicleMongo;
import my.suveng.server.modules.rental.service.RentalPointService;
import my.suveng.server.modules.rental.service.VehicleService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * create at 2018/10/10
 * author: suveng
 * email: suveng@163.com
 **/
@RestController
@RequestMapping("/rental")
@Slf4j
public class RentalPointController {
    @Autowired
    private RentalPointService rentalPointService;
    @Autowired
    private VehicleService vehicleService;

    /**
     * 手动创建租车点
     * @param rentalPointMongo 租车点
     */
    @PostMapping("/save")
    public void save(@RequestBody RentalPointMongo rentalPointMongo) {
        RentalPoint last = rentalPointService.findLastId();
        if (!ObjectUtils.allNotNull(last)) {
            log.info("[rental]:设置值为2000000");
            rentalPointMongo.setId(2000000L);
        } else {
            rentalPointMongo.setId(last.getPointId() + 1);
        }
        rentalPointMongo.setLeftBike(300);
        rentalPointService.save(rentalPointMongo);
    }

    /**
     * 查询所有租车点
     * @return 所有租车点
     */
    @GetMapping("/findAll")
    public List<RentalPointMongo> findAllRentals() {
        return rentalPointService.findAll();
    }

    /**
     * 查找附近租车点
     * @param longitude 经度
     * @param latitude 纬度
     * @return 附近10公里的租车点
     */
    @GetMapping("/findNearRentals")
    public GeoResults<RentalPointMongo> findNearRentals(double longitude, double latitude) {
        return rentalPointService.findNear(longitude, latitude);
    }

    @PostMapping("/generate")
    public void generateVehicle() {
        int bikeNo = 1000001;
        List<RentalPointMongo> all = rentalPointService.findAll();
        for (RentalPointMongo rentalPoint : all) {
            for (int i = 0; i < 300; i++) {
                String no = String.valueOf(bikeNo);
                double longitude = rentalPoint.getLocation()[0];
                double latitude = rentalPoint.getLocation()[1];
                Long pointId = rentalPoint.getId();
                VehicleMongo vehicleMongo = new VehicleMongo();
                vehicleMongo.setId(no);
                vehicleMongo.setQrCode(no);
                vehicleMongo.setLocation(rentalPoint.getLocation());
                vehicleMongo.setStatus(VehicleStatusEnums.READY.getCode());
                vehicleMongo.setPointId(pointId);
                vehicleService.save(vehicleMongo);
                vehicleService.saveInMysql(vehicleMongo.toMySQL());
                bikeNo++;
            }
        }
    }
}

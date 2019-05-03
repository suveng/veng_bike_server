package my.suveng.server.modules.rental.service;


import my.suveng.server.modules.rental.model.po.RentalPoint;
import my.suveng.server.modules.rental.model.po.RentalPointMongo;
import org.springframework.data.geo.GeoResults;

import java.util.List;

/**
 * @author suwenguang
 * suveng@163.com
 * since 2019/3/19
 * description:
 **/
public interface RentalPointService {
    boolean save(RentalPointMongo rentalPointMongo);

    List<RentalPointMongo> findAll();

    GeoResults<RentalPointMongo> findNear(Double longitude, Double latitude);

    RentalPoint findLastId();

}

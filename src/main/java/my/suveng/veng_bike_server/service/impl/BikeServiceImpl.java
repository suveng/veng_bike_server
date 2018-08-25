package my.suveng.veng_bike_server.service.impl;

import my.suveng.veng_bike_server.dao.BikeMapper;
import my.suveng.veng_bike_server.pojo.Bike;
import my.suveng.veng_bike_server.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-25 下午7:03
 */
@Service
public class BikeServiceImpl implements BikeService {
    @Resource
    BikeMapper bikeMapper;
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void save(Bike bike) {
        mongoTemplate.insert(bike);
    }

    @Override
    public List<Bike> findAll() {
        return mongoTemplate.findAll(Bike.class, "bike");
    }
}

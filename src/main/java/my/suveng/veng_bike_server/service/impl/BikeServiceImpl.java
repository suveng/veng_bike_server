package my.suveng.veng_bike_server.service.impl;

import my.suveng.veng_bike_server.dao.BikeMapper;
import my.suveng.veng_bike_server.pojo.mongo.Bike;
import my.suveng.veng_bike_server.service.BikeService;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
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
    @Resource
    MongoTemplate mongoTemplate;

    @Override
    public void save(Bike bike) {
        mongoTemplate.insert(bike);
    }

    @Override
    public List<Bike> findAll() {
        return mongoTemplate.findAll(Bike.class, "bike");
    }

    @Override
    public GeoResults<Bike> findNear(double longitude, double latitude) {
        //指定nearquery 相当于查询条件
        NearQuery nearQuery=NearQuery.near(new Point(longitude,latitude), Metrics.KILOMETERS);
       nearQuery.maxDistance(1).query(new Query().addCriteria(Criteria.where("status").is(0)).limit(20));
       //通过mongo 的geohash算法的接口计算出来。
        GeoResults<Bike> geoResults = mongoTemplate.geoNear(nearQuery, Bike.class);
        return geoResults;
    }
}

package my.suveng.veng_bike_server.rentalpoint.service.impl;

import lombok.extern.slf4j.Slf4j;
import my.suveng.veng_bike_server.rentalpoint.dao.mysql.RentalPointMapper;
import my.suveng.veng_bike_server.rentalpoint.pojo.mongo.RentalPoint;
import my.suveng.veng_bike_server.rentalpoint.service.RentalPointService;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * create at 2018/10/10
 * author: suveng
 * email: suveng@163.com
 **/
@Service
@Slf4j
public class RentalPointServiceImpl implements RentalPointService {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private RentalPointMapper rentalPointMapper;

    /**
     * 手动创建租车点
     * @param rentalPoint 租车点
     */
    @Override
    public void save(RentalPoint rentalPoint) {
        my.suveng.veng_bike_server.rentalpoint.pojo.mysql.RentalPoint rentalPoint1=rentalPoint.toMySQL(rentalPoint);
        if (rentalPointMapper.insertSelective(rentalPoint1)<1){
            log.error("租赁点插入失败");
        }
        mongoTemplate.insert(rentalPoint);
    }

    /**
     * 查询所有租车点
     * @return 所有租车点
     */
    @Override
    public List<RentalPoint> findAll() {
        return mongoTemplate.findAll(RentalPoint.class, "RentalPoints");
    }

    /**
     * 查询附近10公里内的租车点
     * @param longitude 经度
     * @param latitude 纬度
     * @return GeoResults<RentalPoint>
     */
    @Override
    public GeoResults<RentalPoint> findNear(double longitude, double latitude) {
        //指定nearquery 相当于查询条件
        NearQuery nearQuery=NearQuery.near(new Point(longitude,latitude), Metrics.KILOMETERS);
        //查找附近10公里内的租车点
        nearQuery.maxDistance(10).query(new Query().limit(20));
        //通过mongo 的geohash算法的接口计算出来。
        return mongoTemplate.geoNear(nearQuery, RentalPoint.class);
    }
}

package my.suveng.server.modules.rental.service.impl;

import lombok.extern.slf4j.Slf4j;
import my.suveng.server.modules.rental.dao.RentalPointRepository;
import my.suveng.server.modules.rental.model.po.RentalPoint;
import my.suveng.server.modules.rental.model.po.RentalPointMongo;
import my.suveng.server.modules.rental.service.RentalPointService;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author suwenguang
 *         suveng@163.com
 * since 2019/5/1
 * description:
 **/
@Service
@Slf4j
public class RentalPointServiceImpl implements RentalPointService {
    private static final int LIMIT = 20;
    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    private RentalPointRepository rentalPointRepository;

    /**
     * 手动创建租车点
     * @param rentalPoint 租车点
     */
    @Override
    public boolean save(RentalPoint rentalPoint) {
        try {
            rentalPointRepository.save(rentalPoint);
            mongoTemplate.insert(rentalPoint);
        }catch (Exception e){
            log.error("[rental]:租赁点插入失败,记录为:{}",rentalPoint);
            log.error("[rental]:出现错误",e);
            log.info("[rental]:入库失败,需要回滚");
            try {
                rentalPointRepository.deleteById(rentalPoint.getPointId());
            }catch (Exception error){
                log.error("[rental]:回滚失败,请联系开发者",error);
            }
            return false;
        }
        return true;
    }

    /**
     * 查询所有租车点
     * @return 所有租车点
     */
    @Override
    public List<RentalPointMongo> findAll() {
        return mongoTemplate.findAll(RentalPointMongo.class, "RentalPoints");
    }

    /**
     * 查询附近10公里内的租车点
     * @param longitude 经度
     * @param latitude 纬度
     * @return GeoResults<RentalPoint>
     */
    @Override
    public GeoResults<RentalPointMongo> findNear(Double longitude, Double latitude) {
        //指定nearquery 相当于查询条件
        NearQuery nearQuery = NearQuery.near(new Point(longitude, latitude), Metrics.KILOMETERS);
        //查找附近1公里内的租车点
        nearQuery.maxDistance(1).query(new Query().limit(LIMIT));
        //通过mongo 的geohash算法的接口计算出来。
        return mongoTemplate.geoNear(nearQuery, RentalPointMongo.class);
    }
}

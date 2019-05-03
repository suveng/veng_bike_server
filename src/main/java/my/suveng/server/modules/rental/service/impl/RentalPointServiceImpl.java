package my.suveng.server.modules.rental.service.impl;

import lombok.extern.slf4j.Slf4j;
import my.suveng.server.modules.rental.dao.RentalPointRepository;
import my.suveng.server.modules.rental.model.po.RentalPoint;
import my.suveng.server.modules.rental.model.po.RentalPointMongo;
import my.suveng.server.modules.rental.service.RentalPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

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
    public static final double MAX_DISTANCE = 0.5;
    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    private RentalPointRepository rentalPointRepository;

    /**
     * 手动创建租车点
     * @param rentalPointMongo 租车点
     */
    @Override
    public boolean save(RentalPointMongo rentalPointMongo) {
        try {
            RentalPoint rentalPoint = rentalPointMongo.toMySQL(rentalPointMongo);
            rentalPointRepository.save(rentalPoint);
            mongoTemplate.insert(rentalPointMongo);
        }catch (Exception e){
            log.error("[rental]:租赁点插入失败,记录为:{}",rentalPointMongo);
            log.error("[rental]:出现错误",e);
            log.info("[rental]:入库失败,需要回滚");
            try {
                rentalPointRepository.deleteById(Long.valueOf(rentalPointMongo.getId()));
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
        nearQuery.maxDistance(MAX_DISTANCE).query(new Query().limit(LIMIT));
        //通过mongo 的geohash算法的接口计算出来。
        return mongoTemplate.geoNear(nearQuery, RentalPointMongo.class);
    }

    @Override
    public RentalPoint findLastId() {
        Sort orders = new Sort(Sort.Direction.DESC, "pointId");
        Page<RentalPoint> page = rentalPointRepository.findAll(PageRequest.of(0, 1, orders));
        List<RentalPoint> rentalPointList = page.getContent();
        if (CollectionUtils.isEmpty(rentalPointList)) {
            log.info("[rental]:记录为空");
        }
        if (rentalPointList.size()>0){
            return rentalPointList.get(0);
        }
        return null;
    }
}

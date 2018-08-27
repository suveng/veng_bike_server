package my.suveng.veng_bike_server.service.impl;

import my.suveng.veng_bike_server.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-27 下午10:27
 */
@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void log_ready(String log) {
        mongoTemplate.insert(log,"logs");
    }
}

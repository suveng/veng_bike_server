package my.suveng.server.modules.rental.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import my.suveng.server.modules.rental.dao.RentalRecordRepository;
import my.suveng.server.modules.rental.model.po.RentalRecord;
import my.suveng.server.modules.rental.service.RentalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suwenguang
 *         suveng@163.com
 * since 2019/5/1
 * description:
 **/
@Service
@Slf4j
public class RentalRecordServiceImpl implements RentalRecordService {
    @Autowired
    private RentalRecordRepository rentalRecordRepository;

    @Override
    public List<RentalRecord> getByUserId(String userId, int status) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                        .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                        .withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.exact())
                        .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact());
        RentalRecord rentalRecord = new RentalRecord();
        rentalRecord.setUserId(userId);
        rentalRecord.setStatus(status);
        Example<RentalRecord> example = Example.of(rentalRecord,matcher);
        return rentalRecordRepository.findAll(example);
    }


    @Override
    public boolean save(RentalRecord rentalRecord) {
        try {
            rentalRecordRepository.saveAndFlush(rentalRecord);
        }catch (Exception e){
            log.error("[rental]:租赁记录插入失败",e);
            log.error("[rental]:记录为:{}", JSON.toJSONString(rentalRecord));
            return false;
        }
        log.info("[rental]:租赁记录插入成功");
        return true;
    }
}

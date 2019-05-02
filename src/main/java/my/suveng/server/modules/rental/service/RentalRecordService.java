package my.suveng.server.modules.rental.service;

import my.suveng.server.modules.rental.model.po.RentalRecord;

import java.util.List;

/**
 * @author suwenguang
 * suveng@163.com
 * since 2019/3/19
 * description:
 **/
public interface RentalRecordService {

    List<RentalRecord> getByUserId(Long userId, int status);

    boolean save(RentalRecord rentalRecord);
}

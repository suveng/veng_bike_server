package my.suveng.server.modules.charge.dao;

import my.suveng.server.modules.charge.model.po.RechargeRecord;
import my.suveng.server.modules.rental.model.po.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author suwenguang
 * suveng@163.com
 * since 2019/3/19
 * description:
 **/
public interface RechargeRecordRepository extends JpaRepository<RechargeRecord,Long> {
}

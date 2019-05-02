package my.suveng.server.modules.rental.dao;

import my.suveng.server.modules.rental.model.po.RentalPoint;
import my.suveng.server.modules.user.model.po.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author suwenguang
 * suveng@163.com
 * since 2019/3/19
 * description:
 **/
public interface RentalPointRepository extends JpaRepository<RentalPoint,Long> {
}

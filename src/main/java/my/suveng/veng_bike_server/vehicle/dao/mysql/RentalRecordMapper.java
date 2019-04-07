package my.suveng.veng_bike_server.vehicle.dao.mysql;

import my.suveng.veng_bike_server.vehicle.pojo.mysql.RentalRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RentalRecordMapper {
    int deleteByPrimaryKey(Long rentalid);

    int insert(RentalRecord record);

    int insertSelective(RentalRecord record);

    RentalRecord selectByPrimaryKey(Long rentalid);

    int updateByPrimaryKeySelective(RentalRecord record);

    int updateByPrimaryKey(RentalRecord record);

    List<RentalRecord> selectByUserId(@Param("userId") String userId, @Param("isFinish") Integer isFinish);
}

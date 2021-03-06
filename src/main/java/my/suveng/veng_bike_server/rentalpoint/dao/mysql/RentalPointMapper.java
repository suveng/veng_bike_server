package my.suveng.veng_bike_server.rentalpoint.dao.mysql;

import my.suveng.veng_bike_server.rentalpoint.pojo.mysql.RentalPoint;

public interface RentalPointMapper {
    int deleteByPrimaryKey(String pointid);

    int insert(RentalPoint record);

    int insertSelective(RentalPoint record);

    RentalPoint selectByPrimaryKey(String pointid);

    int updateByPrimaryKeySelective(RentalPoint record);

    int updateByPrimaryKey(RentalPoint record);
}
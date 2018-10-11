package my.suveng.veng_bike_server.dao.mysql;

import my.suveng.veng_bike_server.pojo.mysql.Vehicle;

public interface VehicleMapper {
    int deleteByPrimaryKey(String vehicleid);

    int insert(Vehicle record);

    int insertSelective(Vehicle record);

    Vehicle selectByPrimaryKey(String vehicleid);

    int updateByPrimaryKeySelective(Vehicle record);

    int updateByPrimaryKey(Vehicle record);
}
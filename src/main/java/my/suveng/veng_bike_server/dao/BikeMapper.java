package my.suveng.veng_bike_server.dao;

import my.suveng.veng_bike_server.pojo.Bike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BikeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Bike record);

    int insertSelective(Bike record);

    Bike selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Bike record);

    int updateByPrimaryKey(Bike record);
}
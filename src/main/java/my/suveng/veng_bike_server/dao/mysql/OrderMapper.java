package my.suveng.veng_bike_server.dao.mysql;

import my.suveng.veng_bike_server.pojo.mysql.Order;


public interface OrderMapper {
    int deleteByPrimaryKey(String orderid);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(String orderid);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
}
package my.suveng.veng_bike_server.dao.mysql;

import my.suveng.veng_bike_server.pojo.mysql.User;

public interface UserMapper {
    int insert(User record);

    int insertSelective(User record);
}
package my.suveng.veng_bike_server.user.dao.mysql;

import my.suveng.veng_bike_server.user.pojo.mysql.User;

public interface UserMapper {
    int deleteByPrimaryKey(String userid);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String userid);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}
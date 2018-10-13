package my.suveng.veng_bike_server.user.dao.mysql;

import my.suveng.veng_bike_server.user.pojo.mysql.RechargeRecord;

public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Long rechareid);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Long rechareid);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);
}
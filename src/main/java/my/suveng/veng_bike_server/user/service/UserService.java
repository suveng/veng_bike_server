package my.suveng.veng_bike_server.user.service;

import my.suveng.veng_bike_server.user.pojo.mongo.User;
import my.suveng.veng_bike_server.user.pojo.mysql.RechargeRecord;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-9-8 下午3:08
 */
public interface UserService {
    /**
     * 获取验证码
     * @param nationCode 验证码
     * @param phoneNum 手机号
     * @throws Exception 异常
     */
    void genVerifyCode(String nationCode, String phoneNum) throws Exception;

    /**
     * 校验验证码
     * @param user 用户信息
     * @return true:校验通过，false：校验失败
     */
    boolean verify(User user);

    void deposit(User user);

    /**
     * 实名认证接口
     * @param user 用户信息
     */
    boolean identify(User user);

    User getUserByOpenid(String openid);

    void recharge(User user, double charge, RechargeRecord rechargeRecord);

    boolean checkRentalRecord(String userId);
}

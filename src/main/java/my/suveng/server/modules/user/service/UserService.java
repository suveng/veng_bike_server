package my.suveng.server.modules.user.service;

import my.suveng.server.modules.charge.model.po.RechargeRecord;
import my.suveng.server.modules.user.model.po.User;
import my.suveng.server.modules.user.model.po.UserMongo;
import org.springframework.data.domain.Page;

/**
 * @author suwenguang
 * suveng@163.com
 * since 2019/3/19
 * description:
 **/
public interface UserService {
    Page<User> selectList(User user, int page, int size);

    void save(User user);

    void removeAll();

    void deposit(UserMongo user);

    boolean verify(UserMongo user);

    void genVerifyCode(String nationCode, String phoneNum) throws Exception;

    boolean identify(UserMongo userMo);

    void recharge(UserMongo userMongo, double charge, RechargeRecord rechargeRecord);

    UserMongo getUserByOpenid(String openid);

    boolean checkRentalRecord(String userId);
}

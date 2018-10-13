package my.suveng.veng_bike_server.user.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import my.suveng.veng_bike_server.common.REST.IndustrySMS;
import my.suveng.veng_bike_server.user.dao.mysql.RechargeRecordMapper;
import my.suveng.veng_bike_server.user.dao.mysql.UserMapper;
import my.suveng.veng_bike_server.user.pojo.mongo.User;
import my.suveng.veng_bike_server.user.pojo.mysql.RechargeRecord;
import my.suveng.veng_bike_server.user.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-9-8 下午3:08
 */
@Service
public class UserServiceImpl implements UserService {
    private static Logger logger = Logger.getLogger(UserServiceImpl.class);
    //操作redis中的字符串类型数据
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    MongoTemplate mongoTemplate;
    @Resource
    private IndustrySMS industrySMS;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RechargeRecordMapper rechargeRecordMapper;

    @Override
    public void deposit(User user) {
        mongoTemplate.updateFirst(new Query(Criteria.where("phoneNum").is(user.getPhoneNum())), new Update().set("status", user.getStatus()).set("deposit", 299), User.class);
    }

    @Override
    public boolean verify(User user) {
        boolean flag = false;
        String phoneNum = user.getPhoneNum();
        String verifyCode = user.getVerifyCode();
        String code = stringRedisTemplate.opsForValue().get(phoneNum);
        if (verifyCode != null && verifyCode.equals(code)) {
            mongoTemplate.save(user);
            flag = true;
        }
        return flag;
    }

    @Override
    public void genVerifyCode(String nationCode, String phoneNum) throws Exception {

        //普通单发
        String code = (int) ((Math.random() * 9 + 1) * 1000) + "";
        if (nationCode.equals("86")) {
            String res = industrySMS.send(phoneNum, code);
            JSONObject jres = JSON.parseObject(res);
            String respcode = jres.getString("respCode");
            String resdesc = jres.getString("respDesc");
            if (!respcode.equals("00000")) {
                logger.error("发送验证码失败:  " + resdesc);
                throw new InvalidParameterException("发送验证码失败");
            }
        } else {
            Exception e = new InvalidParameterException("不支持国外手机号码");
            logger.error(e);
            throw e;
        }
        //将数据保存到redis中，redis的key手机号，value是验证码，有效时长120秒
        stringRedisTemplate.opsForValue().set(phoneNum, code, 120, TimeUnit.SECONDS);
    }

    @Override
    public void identify(User user) {
        mongoTemplate.updateFirst(new Query(Criteria.where("phoneNum").is(user.getPhoneNum())), new Update().set("status", user.getStatus()).set("name", user.getName()).set("idNum", user.getIdNum()), User.class);
        List<User> users = mongoTemplate.find(new Query(Criteria.where("phoneNum").is(user.getPhoneNum())), User.class);
        //只有实名认证通过才加入到MySQL
        for (User user1 : users) {
            my.suveng.veng_bike_server.user.pojo.mysql.User user2 = new my.suveng.veng_bike_server.user.pojo.mysql.User();
            user2.setIdnum(user1.getIdNum());
            user2.setBalance(user1.getBalance());
            user2.setDeposit(user1.getDeposit());
            user2.setName(user1.getName());
            user2.setStatus(user1.getStatus());
            user2.setUserid(user1.getId());
            user2.setPhonenum(user1.getPhoneNum());
            userMapper.insertSelective(user2);
        }
    }

    @Override
    public void recharge(User user, double charge, RechargeRecord rechargeRecord) {

        List<User> phoneNum = mongoTemplate.find(new Query(Criteria.where("phoneNum").is(user.getPhoneNum())), User.class);
        user = phoneNum.get(0);
        double new_balance = user.getBalance()+charge;
        user.setBalance(new_balance);
        rechargeRecord.setUserid(user.getId());
        mongoTemplate.updateFirst(
                new Query(Criteria.where("phoneNum").is(user.getPhoneNum())),
                new Update().set("balance", user.getBalance()), User.class);

        my.suveng.veng_bike_server.user.pojo.mysql.User user1 = new my.suveng.veng_bike_server.user.pojo.mysql.User();
        user1.setUserid(user.getId());
        user1.setStatus(user.getStatus());
        user1.setName(user.getName());
        user1.setDeposit(user.getDeposit());
        user1.setBalance(user.getBalance());
        user1.setIdnum(user.getIdNum());
        user1.setPhonenum(user.getPhoneNum());
        userMapper.updateByPrimaryKey(user1);
        rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    @Override
    public User getUserByOpenid(String openid) {
        return mongoTemplate.findById(openid, User.class);
    }
}


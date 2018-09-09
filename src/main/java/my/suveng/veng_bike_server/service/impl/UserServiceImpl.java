package my.suveng.veng_bike_server.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import my.suveng.veng_bike_server.REST.IndustrySMS;
import my.suveng.veng_bike_server.pojo.User;
import my.suveng.veng_bike_server.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
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
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private IndustrySMS industrySMS;

    @Override
    public void deposit(User user) {
        mongoTemplate.updateFirst(new Query(Criteria.where("phoneNum").is(user.getPhoneNum())), new Update().set("status", user.getStatus()).set("deposit", 299),  User.class);
    }

    @Override
    public boolean verify(User user) {
        boolean flag = false;
        String phoneNum = user.getPhoneNum();
        String verifyCode = user.getVerifyCode();
        String code = stringRedisTemplate.opsForValue().get(phoneNum);
        if(verifyCode != null && verifyCode.equals(code)) {
            mongoTemplate.save(user);
            flag = true;
        }
        return flag;
    }

    @Override
    public void genVerifyCode(String nationCode, String phoneNum) throws Exception {

        //普通单发
        String code = (int) ((Math.random() * 9 + 1) * 1000) + "";
        if (nationCode.equals("86")){
            String res=industrySMS.send(phoneNum,code);
            JSONObject jres= JSON.parseObject(res);
            String respcode=jres.getString("respCode");
            String resdesc=jres.getString("respDesc");
            if (!respcode.equals("00000")){
                logger.error("发送验证码失败:  "+resdesc);
                throw new InvalidParameterException("发送验证码失败");
            }
        }else{
            Exception e=new InvalidParameterException("不支持国外手机号码");
            logger.error(e);
            throw e;
        }
        //将数据保存到redis中，redis的key手机号，value是验证码，有效时长120秒
        stringRedisTemplate.opsForValue().set(phoneNum, code, 120, TimeUnit.SECONDS);
    }

    @Override
    public void identify(User user) {
        mongoTemplate.updateFirst(new Query(Criteria.where("phoneNum").is(user.getPhoneNum())), new Update().set("status", user.getStatus()).set("name", user.getName()).set("idNum", user.getIdNum()),  User.class);
    }

    @Override
    public User getUserByOpenid(String openid) {
        return mongoTemplate.findById(openid, User.class);
    }
}


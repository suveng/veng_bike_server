package my.suveng.veng_bike_server.service.impl;


import my.suveng.veng_bike_server.REST.IndustrySMS;
import my.suveng.veng_bike_server.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    private IndustrySMS industrySMS;

    @Override
    public void genVerifyCode(String nationCode, String phoneNum) throws Exception {

        //普通单发
        String code = (int) ((Math.random() * 9 + 1) * 1000) + "";
        if (nationCode.equals("86")){
            industrySMS.send(phoneNum,code);
        }else{
            Exception e=new InvalidParameterException("不支持国外手机号码");
            logger.error(e);
            throw e;
        }
        //将数据保存到redis中，redis的key手机号，value是验证码，有效时长120秒
        stringRedisTemplate.opsForValue().set(phoneNum, code, 120, TimeUnit.SECONDS);
    }
}


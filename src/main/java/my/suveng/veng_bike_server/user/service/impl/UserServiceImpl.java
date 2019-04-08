package my.suveng.veng_bike_server.user.service.impl;


import lombok.extern.slf4j.Slf4j;
import my.suveng.veng_bike_server.common.REST.IndustrySMS;
import my.suveng.veng_bike_server.user.dao.mysql.RechargeRecordMapper;
import my.suveng.veng_bike_server.user.dao.mysql.UserMapper;
import my.suveng.veng_bike_server.user.pojo.mongo.User;
import my.suveng.veng_bike_server.user.pojo.mysql.RechargeRecord;
import my.suveng.veng_bike_server.user.service.UserService;
import my.suveng.veng_bike_server.vehicle.dao.mysql.RentalRecordMapper;
import my.suveng.veng_bike_server.vehicle.pojo.mysql.RentalRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-9-8 下午3:08
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
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
    @Resource
    private RentalRecordMapper rentalRecordMapper;

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

    /**
     * 获取验证码
     *
     * @param nationCode 验证码
     * @param phoneNum   手机号
     *
     * @throws Exception 异常
     */
    @Override
    public void genVerifyCode(String nationCode, String phoneNum) throws Exception {
        //todo://接口恢复
        //普通单发
        //String code = (int) ((Math.random() * 9 + 1) * 1000) + "";
        //if (nationCode.equals("86")) {
        //    String res = industrySMS.send(phoneNum, code);
        //    JSONObject jres = JSON.parseObject(res);
        //    String respcode = jres.getString("respCode");
        //    String resdesc = jres.getString("respDesc");
        //    if (!respcode.equals("00000")) {
        //        log.error("发送验证码失败:  " + resdesc);
        //        throw new InvalidParameterException("发送验证码失败");
        //    }
        //} else {
        //    Exception e = new InvalidParameterException("不支持国外手机号码");
        //    log.error(e.getMessage());
        //    throw e;
        //}
        //将数据保存到redis中，redis的key手机号，value是验证码，有效时长120秒
        stringRedisTemplate.opsForValue().set(phoneNum, "1234", 120, TimeUnit.SECONDS);
    }

    /**
     * 阿里云提供的实名认证接口
     */
    private static boolean idenAuthentication(String idNo, String name) {
        //todo://接口恢复
        return true;
        //String host = "https://idenauthen.market.alicloudapi.com";
        //String path = "/idenAuthentication";
        //String method = "POST";
        //String appcode = "5bb7b38ac53643c995fe48fdc8ee148f";
        //Map<String, String> headers = new HashMap<String, String>();
        ////最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        //headers.put("Authorization", "APPCODE " + appcode);
        ////根据API的要求，定义相对应的Content-Type
        //headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        //Map<String, String> querys = new HashMap<String, String>();
        //Map<String, String> bodys = new HashMap<String, String>();
        //bodys.put("idNo", idNo);
        //bodys.put("name", name);
        //try {
        //    HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        //    PeopleIdDto peopleIdDto = JSON.parseObject(EntityUtils.toString(response.getEntity()), PeopleIdDto.class);
        //    if (!ObjectUtils.allNotNull(peopleIdDto)) {
        //        throw new IllegalArgumentException("返回结果异常");
        //    }
        //    return peopleIdDto.getRespCode().equals("0000");
        //} catch (Exception e) {
        //    log.error("调用接口失败，记录为：{idNo:【{}】,name:【{}】}", idNo, name);
        //    return false;
        //}
    }

    /**
     * 实名认证接口
     *
     * @param user 用户信息
     */
    @Override
    public boolean identify(User user) {
        //参数校验
        if (!ObjectUtils.allNotNull(user)) {
            return false;
        }
        if (!ObjectUtils.allNotNull(user.getIdNum(), user.getName())) {
            return false;
        }
        //调用阿里云接口
        if (idenAuthentication(user.getIdNum(), user.getName())) {
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
            return true;
        }
        return false;
    }

    @Override
    public void recharge(User user, double charge, RechargeRecord rechargeRecord) {

        List<User> phoneNum = mongoTemplate.find(new Query(Criteria.where("phoneNum").is(user.getPhoneNum())), User.class);
        user = phoneNum.get(0);
        double new_balance = user.getBalance() + charge;
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

    @Override
    public boolean checkRentalRecord(String userId) {
        List<RentalRecord> rentalRecords = rentalRecordMapper.selectByUserId(userId, 0);
        if (!CollectionUtils.isEmpty(rentalRecords) && rentalRecords.size() > 0) {
            return false;
        }
        return rentalRecords.size() == 0;
    }
}


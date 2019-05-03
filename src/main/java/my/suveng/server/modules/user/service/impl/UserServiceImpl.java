package my.suveng.server.modules.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import my.suveng.server.common.REST.IndustrySMS;
import my.suveng.server.common.enums.RentalRecordEnums;
import my.suveng.server.modules.charge.dao.RechargeRecordRepository;
import my.suveng.server.modules.charge.model.po.RechargeRecord;
import my.suveng.server.modules.rental.dao.RentalRecordRepository;
import my.suveng.server.modules.rental.model.po.RentalRecord;
import my.suveng.server.modules.rental.service.RentalRecordService;
import my.suveng.server.modules.user.dao.UserRepository;
import my.suveng.server.modules.user.model.po.User;
import my.suveng.server.modules.user.model.po.UserMongo;
import my.suveng.server.modules.user.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
 * @author suwenguang
 * suveng@163.com
 * since 2019/3/19
 * description:
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    MongoTemplate mongoTemplate;
    @Resource
    private IndustrySMS industrySMS;
    @Autowired
    private RechargeRecordRepository rechargeRecordRepository;
    @Autowired
    private RentalRecordRepository rentalRecordRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private RentalRecordService rentalRecordService;

    /**
     * 分页查询user list
     *
     * @return list
     */
    @Override
    public Page<User> selectList(User user, int page, int size) {
        // 校验
        if (!ObjectUtils.allNotNull(user, page, size)) {
            return null;
        }
        // 排序
        Sort orders = new Sort(Sort.Direction.DESC, "id");
        // 构造搜索条件
        Example<User> example = getUserExample(user);

        // 分页查询
        return userRepository.findAll(example, PageRequest.of(page, size, orders));
    }

    /**
     * 构造搜索条件
     * @param user 条件
     * @return Example<User>
     */
    private Example<User> getUserExample(User user) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.exact());

        return Example.of(user, matcher);
    }

    /**
     * 保存一个user
     *
     * @param user user
     */
    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    /**
     * 清除所有数据
     */
    @Override
    public void removeAll() {
        userRepository.deleteAll();
    }


    @Override
    public void deposit(UserMongo user) {
        mongoTemplate.updateFirst(new Query(Criteria.where("phoneNum").is(user.getPhoneNum())), new Update().set("status", user.getStatus()).set("deposit", 299), User.class);
    }

    @Override
    public boolean verify(UserMongo user) {
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
     * @param userMo 用户信息
     */
    @Override
    public boolean identify(UserMongo userMo) {
        //参数校验
        if (!ObjectUtils.allNotNull(userMo)) {
            return false;
        }
        if (!ObjectUtils.allNotNull(userMo.getIdNum(), userMo.getName())) {
            return false;
        }
        //调用阿里云接口
        if (idenAuthentication(userMo.getIdNum(), userMo.getName())) {
            mongoTemplate.updateFirst(new Query(Criteria.where("phoneNum").is(userMo.getPhoneNum())), new Update().set("status", userMo.getStatus()).set("name", userMo.getName()).set("idNum", userMo.getIdNum()), UserMongo.class);
            List<UserMongo> userMongos = mongoTemplate.find(new Query(Criteria.where("phoneNum").is(userMo.getPhoneNum())), UserMongo.class);
            //只有实名认证通过才加入到MySQL
            for (UserMongo userMongo : userMongos) {
                User user = new User();
                user.setIdNum(userMongo.getIdNum());
                user.setBalance(userMongo.getBalance());
                user.setDeposit(userMongo.getDeposit());
                user.setName(userMongo.getName());
                user.setStatus(userMongo.getStatus());
                user.setUserId(userMongo.getId());
                user.setPhone(userMongo.getPhoneNum());
                userRepository.save(user);
            }
            return true;
        }
        return false;
    }

    @Override
    public void recharge(UserMongo userMongo, double charge, RechargeRecord rechargeRecord) {

        List<UserMongo> phoneNum = mongoTemplate.find(new Query(Criteria.where("phoneNum").is(userMongo.getPhoneNum())), UserMongo.class);
        userMongo = phoneNum.get(0);
        double newBalance = userMongo.getBalance() + charge;
        userMongo.setBalance(newBalance);
        rechargeRecord.setUserId(userMongo.getId());
        mongoTemplate.updateFirst(
                new Query(Criteria.where("phoneNum").is(userMongo.getPhoneNum())),
                new Update().set("balance", userMongo.getBalance()), UserMongo.class);

        User user = new User();
        user.setUserId(userMongo.getId());
        user.setStatus(userMongo.getStatus());
        user.setName(userMongo.getName());
        user.setDeposit(userMongo.getDeposit());
        user.setBalance(userMongo.getBalance());
        user.setIdNum(userMongo.getIdNum());
        user.setPhone(userMongo.getPhoneNum());
        userRepository.save(user);
        rechargeRecordRepository.save(rechargeRecord);
    }

    @Override
    public UserMongo getUserByOpenid(String openid) {
        return mongoTemplate.findById(openid, UserMongo.class);
    }

    @Override
    public boolean checkRentalRecord(String userId) {

        List<RentalRecord> rentalRecords = rentalRecordService.getByUserId(userId, RentalRecordEnums.NOT_FINISH.getCode());
        if (!CollectionUtils.isEmpty(rentalRecords) && rentalRecords.size() > 0) {
            return false;
        }
        return rentalRecords.size() == 0;
    }
}

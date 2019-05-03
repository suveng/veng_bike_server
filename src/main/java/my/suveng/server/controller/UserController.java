package my.suveng.server.controller;


import lombok.extern.slf4j.Slf4j;
import my.suveng.server.modules.charge.model.po.RechargeRecord;
import my.suveng.server.modules.user.model.po.UserMongo;
import my.suveng.server.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-29 下午6:14
 */
@Controller
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/host")
    @ResponseBody
    public String host() {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("", e);
        }
        return host;
    }

    /**
     * 发送验证码到指定手机
     * @param nationCode 国际区号
     * @param phoneNum 手机号码
     * @return String
     */
    @PostMapping("/genCode")
    @ResponseBody
    public String genCode(String nationCode, String phoneNum) {
        String msg = "true";
        try {
            //生成4位随机数 -> 调用短信接口发送验证码 -> 将手机号对应的验证码保存到redis中，并且设置这个key的有效时长
            userService.genVerifyCode(nationCode, phoneNum);
        } catch (Exception e) {
            e.printStackTrace();
            msg = "false";
        }
        return msg;
    }

    /**
     * 验证用户信息
     * @param user 用户信息
     * @return boolean
     */
    @PostMapping("/verify")
    @ResponseBody
    public boolean verify(UserMongo user) {
        return userService.verify(user);
    }

    /**
     * 注册用户信息到mongo中
     * @param params 信息
     * @return string
     */
    @PostMapping("/reg")
    @ResponseBody
    public String register(@RequestBody String params) {
        System.out.println(params);
        //userService.register(params);
        return "success";
    }

    /**
     * 交押金
     * @param user 用户信息
     * @return string
     */
    @PostMapping("/deposit")
    @ResponseBody
    public String deposit(UserMongo user) {
        userService.deposit(user);
        return "success";
    }

    /**
     * 实名认证
     * @param user 用户信息
     * @return string
     */
    @PostMapping("/identify")
    @ResponseBody
    public String identify(UserMongo user) {
        if (userService.identify(user)) {
            return "success";
        }
        return "fail";
    }

    /**
     * 根据openid拿到用户信息
     * @param openid 微信openid
     * @return userMongo 信息
     */
    @GetMapping("/phoneNum/{openid}")
    @ResponseBody
    public UserMongo getPhoneNum(@PathVariable("openid") String openid) {
        return userService.getUserByOpenid(openid);
    }

    /**
     * 充值
     * @param phoneNum 手机号码
     * @param charge 充值金额
     * @param province 省
     * @param city 市
     * @param district 街道
     * @return string
     */
    @PostMapping("/user/recharge")
    @ResponseBody
    public String recharge(@RequestParam(value = "phoneNum", required = true) String phoneNum, @RequestParam(value = "charge", required = true) double charge,
                           @RequestParam(value = "province", required = false) String province, @RequestParam(value = "city", required = false) String city,
                           @RequestParam(value = "district", required = false) String district
    ) {
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setCharge(charge);
        rechargeRecord.setProvince(province);
        rechargeRecord.setCity(city);
        rechargeRecord.setDistrict(district);
        rechargeRecord.setAddress(province + city + district);
        UserMongo user = new UserMongo();
        user.setPhoneNum(phoneNum);
        userService.recharge(user, charge, rechargeRecord);
        return "success";
    }
}

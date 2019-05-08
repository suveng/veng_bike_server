package my.suveng.veng_bike_server.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import my.suveng.veng_bike_server.user.pojo.mongo.UserMongo;
import my.suveng.veng_bike_server.user.pojo.mysql.RechargeRecord;
import my.suveng.veng_bike_server.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-29 下午6:14
 */
@Controller
@Api(value = "User",tags = {"User接口模块"},description = "用户相关")
public class UserController {

    @Resource
    UserService userService;

    @GetMapping("/host")
    @ResponseBody
    @ApiOperation(value = "测试负载均衡")
    public String host(){
        String host=null;
        try {
            host= InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return host;
    }

    @PostMapping("/genCode")
    @ResponseBody
    @ApiOperation(value = "发送验证码到指定手机")
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
    @PostMapping("/verify")
    @ResponseBody
    @ApiOperation(value = "验证用户信息")
    public boolean verify(UserMongo userMongo) {
        return userService.verify(userMongo);
    }
    @PostMapping("/reg")
    @ResponseBody
    @ApiOperation(value = "注册用户信息到mongo中")
    public String register(@RequestBody String params) {
        System.out.println(params);
        //userService.register(params);
        return "success";
    }

    @PostMapping("/deposit")
    @ResponseBody
    @ApiOperation(value = "交押金")
    public String deposit(UserMongo userMongo) {
        userService.deposit(userMongo);
        return "success";
    }
    @PostMapping("/identify")
    @ResponseBody
    @ApiOperation(value = "实名认证")
    public String identify(UserMongo userMongo) {
        if (userService.identify(userMongo)) {
            return "success";
        }
        return "fail";
    }
    @GetMapping("/phoneNum/{openid}")
    @ResponseBody
    @ApiOperation(value = "根据openid拿到用户信息")
    public UserMongo getPhoneNum(@PathVariable("openid") String openid) {
        return userService.getUserByOpenid(openid);
    }

    @PostMapping("/user/recharge")
    @ResponseBody
    @ApiOperation(value = "充值")
    public String recharge(@RequestParam(value = "phoneNum",required = true) String phoneNum, @RequestParam(value = "charge",required = true) double charge,
                           @RequestParam(value = "province",required = false)String province,@RequestParam(value = "city",required = false) String city,
                           @RequestParam(value = "district",required = false) String district
    ){
        RechargeRecord rechargeRecord=new RechargeRecord(charge,province,city,district);
        UserMongo userMongo =new UserMongo();
        userMongo.setPhoneNum(phoneNum);
        userService.recharge(userMongo,charge,rechargeRecord);
        return "success";
    }
}

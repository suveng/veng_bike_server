package my.suveng.veng_bike_server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import my.suveng.veng_bike_server.pojo.User;
import my.suveng.veng_bike_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Api(value = "User",tags = {"User接口模块"},description = "用户相关")
public class UserController {
    @Autowired
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
    public boolean verify(User user) {
        boolean flag = userService.verify(user);
        return flag;
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
    @ApiOperation(value = "充值")
    public String deposit(User user) {
        userService.deposit(user);
        return "success";
    }
    @PostMapping("/identify")
    @ResponseBody
    @ApiOperation(value = "实名认证")
    public String identify(User user) {
        userService.identify(user);
        return "success";
    }
    @GetMapping("/phoneNum/{openid}")
    @ResponseBody
    @ApiOperation(value = "根据openid拿到用户信息")
    public User getPhoneNum(@PathVariable("openid") String openid) {
        return userService.getUserByOpenid(openid);
    }
}

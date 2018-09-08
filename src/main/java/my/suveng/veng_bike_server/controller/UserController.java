package my.suveng.veng_bike_server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import my.suveng.veng_bike_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-29 下午6:14
 */
@Controller
@Api(value = "User",tags = {"User"},description = "用户相关")
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
}

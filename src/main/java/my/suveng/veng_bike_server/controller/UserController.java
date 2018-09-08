package my.suveng.veng_bike_server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
public class UserController {
    @GetMapping("/host")
    @ResponseBody
    public String host(){
        String host=null;
        try {
            host= InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return host;
    }
}

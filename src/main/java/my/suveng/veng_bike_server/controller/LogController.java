package my.suveng.veng_bike_server.controller;

import my.suveng.veng_bike_server.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-27 下午10:25
 */
@Controller
public class LogController {
    @Autowired
    private LogService logService;
    @PostMapping("/log/ready")
    @ResponseBody
    public String log_ready(@RequestBody String log){
        logService.log_ready(log);
        return "success";
    }
}

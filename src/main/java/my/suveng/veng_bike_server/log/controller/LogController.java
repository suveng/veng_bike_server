package my.suveng.veng_bike_server.log.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import my.suveng.veng_bike_server.log.service.LogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-8-27 下午10:25
 */
@Api(value = "Log",tags = {"日志接口模块"},description = "日志相关")
@Controller
public class LogController {
    @Resource
    private LogService logService;
    @PostMapping("/log/ready")
    @ResponseBody
    @ApiOperation(value = "测试向mongo插入一条日志")
    public String log_ready(@RequestBody String log){
        logService.log_ready(log);
        return "success";
    }
}

package my.suveng.veng_bike_server.service;

/**
 * author Veng Su
 * email  1344114844@qq.com
 * date   18-9-8 下午3:08
 */
public interface UserService {
    void genVerifyCode(String nationCode, String phoneNum) throws Exception;
}

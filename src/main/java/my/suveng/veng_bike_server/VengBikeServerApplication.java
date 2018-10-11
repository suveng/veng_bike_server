package my.suveng.veng_bike_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("my.suveng.veng_bike_server.dao.mysql")
public class VengBikeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VengBikeServerApplication.class, args);
    }
}

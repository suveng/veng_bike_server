package my.suveng.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("my.suveng.server.*.dao.mysql")
public class VengBikeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VengBikeServerApplication.class, args);
    }
}

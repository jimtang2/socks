package studio.lab9.socks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SocksApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocksApplication.class, args);
    }
}
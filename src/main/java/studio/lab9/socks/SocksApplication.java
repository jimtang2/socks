package studio.lab9.socks;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocksApplication {
    public static void main(String[] args) {
        Dotenv.configure().directory("./").load();
        SpringApplication.run(SocksApplication.class, args);
    }
}
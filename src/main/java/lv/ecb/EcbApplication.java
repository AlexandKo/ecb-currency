package lv.ecb;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class EcbApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(EcbApplication.class).run();
    }
}

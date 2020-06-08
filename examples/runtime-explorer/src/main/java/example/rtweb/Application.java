package example.rtweb;

import org.apache.commons.codec.binary.Hex;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@Configuration
public class Application implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(byte[].class, String.class, Hex::encodeHexString);
    }
}

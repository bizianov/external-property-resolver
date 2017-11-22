package resolver;

import com.google.gson.Gson;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by viacheslav on 5/22/17.
 */
@SpringBootApplication
public class Application {

    private static final String CONFIG_PROPERTY_NAME = "spring.config.name";
    private static final String CONFIG_PROPERTY_VALUE = "application,custom";

    public static void main(String[] args) {

        Properties appProperties = new Properties();
        appProperties.setProperty(CONFIG_PROPERTY_NAME, CONFIG_PROPERTY_VALUE);

        new SpringApplicationBuilder(Application.class)
                .properties(appProperties)
                .run(args);
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }
}

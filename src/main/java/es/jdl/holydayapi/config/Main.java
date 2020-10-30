package es.jdl.holydayapi.config;

import com.dieselpoint.norm.Database;
import es.jdl.web.BasicAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication (scanBasePackages = "es.jdl.holydayapi.services")
@EnableWebMvc
public class Main {

    @Value("${spring.datasource.url}")
    private String dbUrl;


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public Database database() {
        Database db = new Database();
        db.setJdbcUrl(dbUrl);

        return db;
    }

    @Bean
    public FilterRegistrationBean<BasicAuthenticationFilter> authFilter() {
        FilterRegistrationBean<BasicAuthenticationFilter > registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new BasicAuthenticationFilter());
        registrationBean.addInitParameter("realm", "holydayapi");
        registrationBean.addInitParameter("user", "admin");
        registrationBean.addInitParameter("password", System.getenv("ADMIN_PASS"));
        registrationBean.addUrlPatterns("/admin/*");

        return registrationBean;
    }
}

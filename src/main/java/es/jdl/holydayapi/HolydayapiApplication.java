package es.jdl.holydayapi;

import es.jdl.holydayapi.domain.AppStatus;
import es.jdl.web.BasicAuthenticationFilter;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Properties;

@SpringBootApplication
public class HolydayapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HolydayapiApplication.class, args);
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

    @Autowired
    private DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup(ApplicationReadyEvent event) {
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.migrate();
        AppStatus status = event.getApplicationContext().getBean(AppStatus.class);
        status.dbVersion = flyway.info().current().getVersion().toString();
        status.started = LocalDateTime.now();
            Properties versionProps = new Properties();
            URL r = this.getClass().getResource("/META-INF/maven/es/jdl/holydayapi/pom.properties");
            if (r != null) {
                try {
                    versionProps.load(r.toURI().toURL().openStream()); // avoid jdk 9+ modules-classloader
                    status.appVersion = versionProps.getProperty("version", "undefined");
                } catch (IOException  | URISyntaxException e) {
                    status.appVersion = "Faild: " + e.getMessage();
                }
            } else {
                status.appVersion = "LOCAL";
            }
        //status.appVersion
    }


}

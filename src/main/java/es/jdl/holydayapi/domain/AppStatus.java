package es.jdl.holydayapi.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AppStatus {
    public LocalDateTime started;
    public String dbVersion;
    public String appVersion;
}

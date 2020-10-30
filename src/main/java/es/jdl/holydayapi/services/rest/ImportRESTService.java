package es.jdl.holydayapi.services.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Servicio REST de pega para lanzar cargas o importaciones (se puede usar como cron una vez al año)
 */
@RestController
@RequestMapping ("/admin")
public class ImportRESTService {

    @GetMapping (path = "/echo_time")
    public String test() {
        return LocalDateTime.now().toString();
    }

}

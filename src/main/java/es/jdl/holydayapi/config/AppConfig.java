package es.jdl.holydayapi.config;

import com.googlecode.objectify.ObjectifyService;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.domain.Province;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ObjectifyService.init();
        ObjectifyService.register(City.class);
        ObjectifyService.register(Province.class);
        ObjectifyService.register(Country.class);
        ObjectifyService.register(Holyday.class);
        ObjectifyService.register(ConfigEntry.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}

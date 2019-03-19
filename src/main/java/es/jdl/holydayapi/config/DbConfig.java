package es.jdl.holydayapi.config;

import com.googlecode.objectify.ObjectifyService;

import java.util.Properties;

/**
 * Configuration with ConfigEntity entity in Objectify/Google Datastore
 * @author jdlopez
 */
public class DbConfig {

    private Properties prop = new Properties();

    public DbConfig() {
        //.stream().collect(Collectors.toMap(ConfigEntry::getKey, item -> item)); no se puede con GAE
        for (ConfigEntry c: ObjectifyService.ofy().load().type(ConfigEntry.class).list())
            prop.setProperty(c.getKey(), c.getValue());

    }

    public String getProperty(String key, String defValue) {
        return prop.getProperty(key, defValue);
    }

    /** Es incongruente tener un defValue String, pero así se evita string->int->string->int */
    public int getIntProperty(String key, String defValue) {
        return Integer.parseInt(prop.getProperty(key, defValue));
    }
}

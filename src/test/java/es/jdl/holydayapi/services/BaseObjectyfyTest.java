package es.jdl.holydayapi.services;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import es.jdl.holydayapi.config.ConfigEntry;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.domain.Province;

import java.io.FileInputStream;
import java.io.IOException;

public class BaseObjectyfyTest {

    protected final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    //@Before se llama en cada hijo
    protected void setUpBase() throws IOException {
        helper.setUp();
        ObjectifyService.init(new ObjectifyFactory(
                DatastoreOptions.newBuilder()
                        //.setHost("http://localhost:8484") remove this to use
                        .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(System.getProperty("user.home") +"/holydayapi.json")))
                        .setProjectId("holydayapi")
                        .build()
                        .getService()
        ));
        ObjectifyService.register(City.class);
        ObjectifyService.register(Province.class);
        ObjectifyService.register(Country.class);
        ObjectifyService.register(Holyday.class);
        ObjectifyService.register(ConfigEntry.class);
    }

    //@After se llama en cada hijo
    public void tearDownBase() {
        helper.tearDown();
    }

}

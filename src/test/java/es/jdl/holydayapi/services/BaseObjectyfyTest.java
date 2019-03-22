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
import java.net.Socket;

public class BaseObjectyfyTest {

    protected final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    protected boolean isPortInUse(String host, int port) {
        // Assume no connection is possible.
        boolean result = false;

        try {
            (new Socket(host, port)).close();
            result = true;
        } catch(IOException e) {
            System.out.println("Puerto cerrado, conetamos con google: " + e.getMessage());
        }

        return result;
    }

    //@Before se llama en cada hijo
    protected void setUpBase() throws IOException {
        helper.setUp();
        DatastoreOptions.Builder builder = DatastoreOptions.newBuilder();
        if (isPortInUse("localhost", 8484)) // local develop server
            builder = builder.setHost("http://localhost:8484");
        else // connection with google datastore
            builder = builder.setCredentials(ServiceAccountCredentials
                    .fromStream(new FileInputStream(System.getProperty("user.home") +"/holydayapi.json")));
        ObjectifyService.init(new ObjectifyFactory(
                builder
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

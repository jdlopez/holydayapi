package es.jdl.holydayapi.services;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.domain.Province;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestImport {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
        ObjectifyService.init(new ObjectifyFactory(
                DatastoreOptions.newBuilder()
                        .setHost("http://localhost:8484")
                        .setProjectId("my-project")
                        .build()
                        .getService()
        ));
        ObjectifyService.register(City.class);
        ObjectifyService.register(Province.class);
        ObjectifyService.register(Country.class);
        ObjectifyService.register(Holyday.class);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testMadridEs() throws IOException { // prueba del ayuntamiento
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                ImportFromMadridOpenData importHolydays = new ImportFromMadridOpenData();
                try {
                    importHolydays.importHolydays();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}

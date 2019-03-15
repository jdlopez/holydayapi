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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;

public class TestCountriesProvince {

    ImportProvinceCity importProvinceCity = new ImportProvinceCity();
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
    public void testCountries() {
        ObjectifyService.run(new VoidWork() {
             @Override
             public void vrun() {
                 System.out.println(importProvinceCity.importCountries());
             }
         });
    }

    @Test
    public void listCountries() {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                System.out.println(Arrays.toString(ObjectifyService.ofy().load().type(Country.class).list().toArray()));
            }
        });
    }

    @Test
    public void listProvinces() {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                System.out.println(Arrays.toString(ObjectifyService.ofy().load().type(Province.class).list().toArray()));
            }
        });
    }

    @Test
    public void testSpain() {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                System.out.println(importProvinceCity.getSpain());
            }
        });
    }

    @Test
    public void testLoadProvincias() {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                try {
                    System.out.println(importProvinceCity.importESProvinces());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

package es.jdl.holydayapi.services;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import es.jdl.holydayapi.config.DbConfig;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.domain.Province;
import es.jdl.holydayapi.services.importers.EntityImporter;
import es.jdl.holydayapi.services.importers.ImportDataException;
import es.jdl.holydayapi.services.importers.ImporterCountry;
import es.jdl.holydayapi.services.importers.ImporterESCity;
import es.jdl.holydayapi.services.importers.ImporterESProvince;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestCountriesProvince {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() throws IOException {
        helper.setUp();
        ObjectifyService.init(new ObjectifyFactory(
                DatastoreOptions.newBuilder()
                        //.setHost("http://holydayapi.appspot.com:8484")
                        .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream("d:\\.ddj\\holydayapi-ee295d54ec6f.json")))
                        .setProjectId("holydayapi")
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

    private void runImporter(EntityImporter<?> importer) {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                try {
                    importer.readAndSave().forEach(p -> System.out.println(p));
                } catch (ImportDataException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void runList(Class<?> clazz) {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                ObjectifyService.ofy().load().type(clazz).list().forEach(p -> System.out.println(p));
            }
        });
    }

    @Test
    public void testCountries() {
        runImporter(new ImporterCountry());
    }

    @Test
    public void listCountries() {
        runList(Country.class);
    }

    @Test
    public void listProvinces() {
        runList(Province.class);
    }

    @Test
    public void listCities() {
        runList(City.class);
    }

    @Test
    public void testLoadProvincias() {
        ImporterESProvince imp = new ImporterESProvince();
        imp.configure(null, null);
        runImporter(imp);
    }

    @Test
    public void testLoadMunicipios() {
        ImporterESCity imp = new ImporterESCity();
        imp.configure(null, new DbConfig());
        runImporter(imp);
    }

    @Test
    public void filter() {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                Province madrid = ObjectifyService.ofy().load().type(Province.class).id("28").now();
                System.out.println("Madrid? " + madrid);
                System.out.println("Query using value:");
                ObjectifyService.ofy().load().type(City.class)
                        .filter("province.code =", "28").list()
                        .forEach(p -> System.out.println(p));
                System.out.println("Query using object:");
                ObjectifyService.ofy().load().type(City.class)
                        .filter("province", madrid).list()
                        .forEach(p -> System.out.println(p));
            }
        });
    }
}

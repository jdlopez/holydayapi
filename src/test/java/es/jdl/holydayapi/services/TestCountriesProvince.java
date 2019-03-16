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
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

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
                 importProvinceCity.importCountries().forEach(p -> System.out.println(p));
             }
         });
    }

    @Test
    public void listCountries() {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                ObjectifyService.ofy().load().type(Country.class).list().forEach(p -> System.out.println(p));
            }
        });
    }

    @Test
    public void listProvinces() {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                ObjectifyService.ofy().load().type(Province.class).list().forEach(p -> System.out.println(p));
            }
        });
    }

    @Test
    public void listCities() {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                ObjectifyService.ofy().load().type(City.class).list().forEach(p -> System.out.println(p));
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
                    importProvinceCity.importESProvinces().forEach(p -> System.out.println(p));
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

    @Test
    public void testLoadMunicipios() {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                try {
                    importProvinceCity.importESCities().forEach(p -> System.out.println(p));
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (XPathExpressionException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

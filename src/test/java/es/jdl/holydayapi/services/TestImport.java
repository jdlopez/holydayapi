package es.jdl.holydayapi.services;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.domain.Province;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

public class TestImport {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());


    protected Closeable session;

    @BeforeClass
    public static void setUpBeforeClass() {
        ObjectifyService.init(new ObjectifyFactory());
        ObjectifyService.register(Country.class);
        ObjectifyService.register(Province.class);
        ObjectifyService.register(City.class);
        ObjectifyService.register(Holyday.class);
    }

    @Before
    public void setUp() {
        this.session = ObjectifyService.begin();
        this.helper.setUp();
    }

    @After
    public void tearDown() throws IOException {
        AsyncCacheFilter.complete();
        this.session.close();
        this.helper.tearDown();
    }
    @Test
    public void testMadridEs() throws IOException { // prueba del ayuntamiento
        ImportFromMadridOpenData importHolydays = new ImportFromMadridOpenData();
        importHolydays.importHolydays();
    }
}

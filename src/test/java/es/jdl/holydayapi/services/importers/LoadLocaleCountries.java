package es.jdl.holydayapi.services.importers;

import com.dieselpoint.norm.Database;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;
import es.jdl.holydayapi.domain.Region;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class LoadLocaleCountries {

    private Database db = new Database();

    @Before
    public void init() {
        db.setJdbcUrl(System.getenv("JDBC_DATABASE_URL"));
    }

    @Test
    public void insertBasicData() throws IOException {
        insertAllCountries();
        insertESRegions();
        insertESprovince();
        insertESCities();
    }


    @Test
    public void insertAllCountries() {
        ImporterBasicData ibd = new ImporterBasicData();
        ibd.setDb(db);
        for (Country c: ibd.insertAllCountries())
            System.out.println(c);
    }

    @Test
    public void insertESRegions() throws IOException {
        ImporterBasicData ibd = new ImporterBasicData();
        ibd.setDb(db);
        for (Region r: ibd.insertESRegions())
            System.out.println(r);

    }

    @Test
    public void insertESprovince() throws IOException {
        ImporterBasicData ibd = new ImporterBasicData();
        ibd.setDb(db);
        for (Province p: ibd.insertESprovince())
            System.out.println(p);
    }

    @Test
    public void insertESCities() throws IOException {
        ImporterBasicData ibd = new ImporterBasicData();
        ibd.setDb(db);
        for (City c: ibd.insertESCities())
            System.out.println(c);
    }
}

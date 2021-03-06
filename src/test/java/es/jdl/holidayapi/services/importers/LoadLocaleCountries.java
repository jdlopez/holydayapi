package es.jdl.holidayapi.services.importers;

import es.jdl.holidayapi.domain.City;
import es.jdl.holidayapi.domain.Country;
import es.jdl.holidayapi.domain.Province;
import es.jdl.holidayapi.domain.Region;

import java.io.IOException;

public class LoadLocaleCountries {


    public void init() {

    }

    public void insertBasicData() throws IOException {
        insertAllCountries();
        insertESRegions();
        insertESprovince();
        insertESCities();
    }


    public void insertAllCountries() {
        ImporterBasicData ibd = new ImporterBasicData();
        for (Country c: ibd.insertAllCountries())
            System.out.println(c);
    }

    public void insertESRegions() throws IOException {
        ImporterBasicData ibd = new ImporterBasicData();
        for (Region r: ibd.insertESRegions())
            System.out.println(r);

    }

    public void insertESprovince() throws IOException {
        ImporterBasicData ibd = new ImporterBasicData();
        for (Province p: ibd.insertESprovince())
            System.out.println(p);
    }

    public void insertESCities() throws IOException {
        ImporterBasicData ibd = new ImporterBasicData();
        for (City c: ibd.insertESCities())
            System.out.println(c);
    }
}

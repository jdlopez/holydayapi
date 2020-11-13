package es.jdl.holidayapi.services.importers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jdl.holidayapi.domain.City;
import es.jdl.holidayapi.domain.Country;
import es.jdl.holidayapi.domain.Province;
import es.jdl.holidayapi.domain.Region;
import es.jdl.holidayapi.persistence.BasicDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
// TODO: chage this for java-migration? in flyway
public class ImporterBasicData {

    @Autowired
    private BasicDataMapper dataMapper;

    public List<Country> insertAllCountries() {
        ArrayList<Country> ret = new ArrayList<>();
        for (String countryStr: Locale.getISOCountries()) {
            Country c = new Country();
            c.setCode(countryStr);
            Locale l = new Locale("", countryStr);
            c.setName(l.getDisplayName());
            c.setLocale(l);
            dataMapper.insertCountry(c);
            ret.add(c);
        }
        return ret;

    }

    public List<Region> insertESRegions() throws IOException {
        ArrayList<Region> ret = new ArrayList<>();
        ObjectMapper om = new ObjectMapper();
        URL url = new URL("https://raw.githubusercontent.com/jdlopez/holydayapi/master/data/comunidades_autonomas.json");
        JsonNode tree = om.readTree(url.openStream());
        if (tree.isArray()) {
            for (JsonNode ccaa: tree) {
                if (ccaa.isObject()) {
                    Region r = new Region();
                    r.setCode(ccaa.get("id").asText());
                    r.setName(ccaa.get("nm").asText());
                    r.setCountryCode("ES");
                    dataMapper.insertRegion(r);
                    ret.add(r);
                }
            }
        }
        return ret;
    }

    public List<Province> insertESprovince() throws IOException {
        ArrayList<Province> ret = new ArrayList<>();
        // fuentes:
        // https://www.ine.es/daco/daco42/codmun/cod_provincia_estandar.htm
        // https://www.ine.es/daco/daco42/codmun/cod_ccaa.htm
        // codigos ISO https://es.wikipedia.org/wiki/ISO_3166-2:ES
        ObjectMapper om = new ObjectMapper();
        URL url = new URL("https://raw.githubusercontent.com/jdlopez/holydayapi/master/data/provincias.json");
        /*
        Array node like this:
        "codCA": "16",
        "id": "01",
        "nm": "Álava",
        "iso": "ES-VI"
         */
        JsonNode tree = om.readTree(url.openStream());
        if (tree.isArray()) {
            for (JsonNode prov: tree) {
                if (prov.isObject()) {
                    Province p = new Province();
                    p.setCode(prov.get("id").asText());
                    p.setName(prov.get("nm").asText());
                    p.setIso(prov.get("iso").asText());
                    p.setCountryCode("ES");
                    p.setRegionCode(prov.get("codCA").asText());
                    dataMapper.insertProvince(p);
                   ret.add(p);
                }
            }
        }
        return ret;
    }

    public List<City> insertESCities() throws IOException {
        ArrayList<City> ret = new ArrayList<>();
        ObjectMapper om = new ObjectMapper();
        // fuente: https://www.ine.es/daco/daco42/codmun/codmun20/20codmun.xlsx
        /* json array con estos datos:
            "cpro": "01",
            "id": "01051",
            "nm": "Agurain/Salvatierra"
         */
        URL url = new URL("https://raw.githubusercontent.com/jdlopez/holydayapi/master/data/municipios.json");
        JsonNode tree = om.readTree(url.openStream());
        if (tree.isArray()) {
            for (JsonNode city: tree) {
                if (city.isObject()) {
                    City c = new City();
                    c.setCode(city.get("id").asText());
                    c.setName(city.get("nm").asText());
                    c.setProvinceCode(city.get("cpro").asText());
                    dataMapper.insertCity(c);
                    ret.add(c);
                }
            }
        }
        return ret;
    }

}

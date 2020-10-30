package es.jdl.holydayapi;

import com.dieselpoint.norm.Database;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;
import es.jdl.holydayapi.domain.Region;
import es.jdl.holydayapi.services.importers.ImporterUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class LoadLocaleCountries {

    private Database db = new Database();

    @Before
    public void init() {
        db.setJdbcUrl(System.getenv("JAWSDB_URL"));
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
        for (String countryStr: Locale.getISOCountries()) {
            Country c = new Country();
            c.setCode(countryStr);
            Locale l = new Locale("", countryStr);
            c.setName(l.getDisplayName());
            c.setLocale(l);
            System.out.println(c);
            db.insert(c);
        }
    }

    @Test
    public void insertESRegions() throws IOException {
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
                    System.out.println(r);
                    db.insert(r);
                }
            }
        }

    }

    @Test
    public void insertESprovince() throws IOException {
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
                    System.out.println(p);
                    db.insert(p);
                }
            }
        }
    }

    @Test
    public void insertESCities() throws IOException {
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
                    System.out.println(c);
                    db.insert(c);
                }
            }
        }

    }
}

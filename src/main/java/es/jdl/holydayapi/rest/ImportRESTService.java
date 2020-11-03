package es.jdl.holydayapi.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jdl.holydayapi.domain.AppStatus;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.persistence.HolydayMapper;
import es.jdl.holydayapi.services.importers.*;
import es.jdl.holydayapi.persistence.ConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Servicio REST de pega para lanzar cargas o importaciones (se puede usar como cron una vez al año)
 */
@RestController
@RequestMapping ("/admin")
public class ImportRESTService {

    @Autowired
    private ImporterBasicData basicData;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private ImporterUtils utils;
    @Autowired
    private HolydayMapper dao;
    @Autowired
    private AppStatus status;

    @ExceptionHandler
    public ResponseEntity<Exception> handleException(Exception e) {
        return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<Exception> handleRuntimException(RuntimeException e) {
        return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping (path = "/echo_time")
    public String test() {
        return LocalDateTime.now().toString();
    }

    @GetMapping (path = "/status")
    public AppStatus status() throws SQLException {
        return status;
    }

    @GetMapping (path = "/import_basic")
    public Properties importBasicData(@RequestParam(name = "ent", required = false) String ent) throws IOException {
        Properties ret = new Properties();
        if (ent != null) {
            if ("countries".equals(ent))
                ret.setProperty(ent, String.valueOf(basicData.insertAllCountries().size()));
            else if ("regions".equals(ent))
                ret.setProperty(ent, String.valueOf(basicData.insertESRegions().size()));
            else if ("province".equals(ent))
                ret.setProperty(ent, String.valueOf(basicData.insertESprovince().size()));
            else if ("cities".equals(ent))
                ret.setProperty(ent, String.valueOf(basicData.insertESCities().size()));
        } else {
            ret.setProperty("countries", String.valueOf(basicData.insertAllCountries().size()));
            ret.setProperty("regions", String.valueOf(basicData.insertESRegions().size()));
            ret.setProperty("province", String.valueOf(basicData.insertESprovince().size()));
            ret.setProperty("cities", String.valueOf(basicData.insertESCities().size()));
        }
        return ret;
    }

    @GetMapping (path = "/import_csv/{prefix}")
    public Map<String, String> importHolydaysCSV(@PathVariable String prefix,
                                        @RequestParam(name = "debug", required = false, defaultValue = "false") boolean debug) throws IOException, ImportException {
        TreeMap<String, String> ret = readConfig(prefix);

        if (!ret.isEmpty()) {
            int holydayCount = 0;
            ObjectMapper om = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ConfigCSV conf = om.convertValue(ret, ConfigCSV.class);
            String csvUrl = null;
            String rfd = ret.get("rfd");
            if (rfd != null) {
                csvUrl = utils.getCsvURLFromRFD(rfd);
            } else {
                csvUrl = ret.get("csvUrl");
            }
            if (csvUrl == null)
                throw new RuntimeException("NOt found URL in RFD: " + rfd);
            ImporterCSV importerCSV = new ImporterCSV(csvUrl, conf);
            List<Holyday> holydays = importerCSV.readFromSource();
            if (holydays == null || holydays.isEmpty())
                throw new RuntimeException("Holydays not found at: " + csvUrl);
            Counters counts = saveAllHolydays(holydays, ret.get("country"), ret.get("region"), ret.get("city"), debug);
            ret.put("holydays_read", String.valueOf(holydays.size()));
            ret.put("holydays_CountLocal", String.valueOf(counts.holydayCountLocal));
            ret.put("holydays_CountRegional", String.valueOf(counts.holydayCountRegional));
            ret.put("holydays_CountCountry", String.valueOf(counts.holydayCountCountry));
            ret.put("holydays_CountInserted", String.valueOf(counts.holydayCountInserted));

        }
        return ret;
    }

    private Counters saveAllHolydays(List<Holyday> holydays, String country, String region, String city, boolean debug) {
        Counters c = new Counters();
        for (Holyday h: holydays) {
            h.setCountry(country);
            if (h.getType().equals(HolydayType.LOCAL)) {
                c.holydayCountLocal++;
                h.setRegion(region);
                if (h.getCity() == null)
                    h.setCity(city);
            } else if (h.getType().equals(HolydayType.REGION)) {
                h.setRegion(region);
                c.holydayCountRegional++;
            } else
                c.holydayCountCountry++;
            if (!debug) {
                int inserted = insertHolyday(h);
                if (inserted == 1)
                    c.holydayCountInserted++;
            }
        } // end for

        return c;
    }

    @GetMapping (path = "/import_web/{prefix}")
    public Map<String, String> importHolydaysScraper(@PathVariable String prefix,
                                                 @RequestParam(name = "debug", required = false, defaultValue = "false") boolean debug) throws IOException, ImportException {
        TreeMap<String, String> ret = readConfig(prefix);

        if (!ret.isEmpty()) {
            ObjectMapper om = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ConfigWebScraper conf = om.convertValue(ret, ConfigWebScraper.class);
            ImporterWebScraper importer = new ImporterWebScraper(conf);
            List<Holyday> holydays = importer.readFromSource();
            if (holydays == null || holydays.isEmpty())
                throw new RuntimeException("Holydays not found at: " + conf.getUrl());
            Counters counts = saveAllHolydays(holydays, conf.getCountry(), conf.getRegion(), conf.getCity(), debug);
            ret.put("holydays_read", String.valueOf(holydays.size()));
            ret.put("holydays_CountLocal", String.valueOf(counts.holydayCountLocal));
            ret.put("holydays_CountRegional", String.valueOf(counts.holydayCountRegional));
            ret.put("holydays_CountCountry", String.valueOf(counts.holydayCountCountry));
            ret.put("holydays_CountInserted", String.valueOf(counts.holydayCountInserted));
        }
        return ret;
    }

    private int insertHolyday(Holyday h) {
        // todo no funciona ON CONFLICT DO NOTHING y el unique index :-(
        int count = dao.checkExists(h);
        if (count == 0)
            return dao.insertHolyday(h);
        else
            return 0;
    }

    private TreeMap<String, String> readConfig(String prefix) {
        Map<String, ConfigDao.KeyValueEntry> subset = configDao.selectSubset(prefix);
        TreeMap<String, String> ret = new TreeMap<String, String>();
        for (String k: subset.keySet())
            ret.put(k.substring(prefix.length() + 1), subset.get(k).entryValue);
        return ret;
    }

    class Counters {
        public int holydayCountLocal = 0;
        public int holydayCountRegional = 0;
        public int holydayCountCountry = 0;
        public int holydayCountInserted = 0;
    }
}

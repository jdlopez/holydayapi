package es.jdl.holydayapi.services.rest;

import com.dieselpoint.norm.Database;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.services.importers.ConfImport;
import es.jdl.holydayapi.services.importers.HolydayType;
import es.jdl.holydayapi.services.importers.ImporterBasicData;
import es.jdl.holydayapi.services.importers.ImporterUtils;
import es.jdl.holydayapi.services.persistence.ConfigDao;
import es.jdl.holydayapi.services.persistence.HolydayDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Servicio REST de pega para lanzar cargas o importaciones (se puede usar como cron una vez al año)
 */
@RestController
@RequestMapping ("/admin")
public class ImportRESTService {

    @Autowired
    private Database db;
    @Autowired
    private ImporterBasicData basicData;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private ImporterUtils utils;
    @Autowired
    private HolydayDao dao;

    @GetMapping (path = "/echo_time")
    public String test() {
        return LocalDateTime.now().toString();
    }

    @GetMapping (path = "/db")
    public Properties dbInfo() throws SQLException {
        if (db == null)
            return null;
        else {
            Connection conn = null;
            try {
                conn = db.getConnection();
                return conn.getClientInfo();
            } finally {
                if (conn != null)
                    conn.close();
            }
        }
    }

    @GetMapping (path = "/import_basic")
    public Properties importBasicData() throws IOException {
        Properties ret = new Properties();
        ret.setProperty("countries", String.valueOf(basicData.insertAllCountries().size()));
        ret.setProperty("regions", String.valueOf(basicData.insertESRegions().size()));
        ret.setProperty("province", String.valueOf(basicData.insertESprovince().size()));
        ret.setProperty("cities", String.valueOf(basicData.insertESCities().size()));
        return ret;
    }

    @GetMapping (path = "/import_csv/{prefix}")
    public Properties importHolydaysCSV(@PathVariable String prefix,
                                        @RequestParam(name = "debug", required = false, defaultValue = "false") boolean debug) throws IOException {
        Properties p = configDao.getSubset(prefix);
        if (!p.isEmpty()) {
            ObjectMapper om = new ObjectMapper();
            ConfImport conf = om.convertValue(p, ConfImport.class);
            String csvUrl = null;
            if (p.getProperty("rfd") != null) {
                csvUrl = utils.getCsvURLFromRFD(p.getProperty("rfd"));
            } else {
                csvUrl = p.getProperty("csvUrl");
            }
            List<Holyday> holydays = utils.readCsv((new URL(csvUrl)).openStream(), conf);
            p.setProperty("read_holydays", String.valueOf(holydays.size()));
            for (Holyday h: holydays) {
                h.setCountry(p.getProperty("country"));
                if (h.getType().equals(HolydayType.LOCAL)) {
                    h.setRegion(p.getProperty("region"));
                    h.setCity(p.getProperty("city"));
                } else if (h.getType().equals(HolydayType.REGION)) {
                    h.setRegion(p.getProperty("region"));
                }
                if (!debug)
                    dao.insertHoyday(h);
            }

        }
        return p;
    }

}

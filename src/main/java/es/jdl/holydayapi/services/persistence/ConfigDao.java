package es.jdl.holydayapi.services.persistence;

import com.dieselpoint.norm.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class ConfigDao {
    @Autowired
    private Database db;

    public Properties getSubset(String prefix) {
        Properties p = new Properties();
        List<ConfigEntry> list = db.sql(
                "select entryKey as k, entryValue as v from configuration ")
                .where("prefix like ?", prefix + ".%").results(ConfigEntry.class);
        if (list != null && !list.isEmpty()) {
            for (ConfigEntry row: list) {
                p.setProperty(row.entryKey.substring(prefix.length() + 1), row.entryValue);
            }
        }
        return p;
    }

    public class ConfigEntry {
        public String entryKey;
        public String entryValue;
    }
}

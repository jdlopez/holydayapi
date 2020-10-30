package es.jdl.holydayapi.services.importers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jdl.holydayapi.domain.Holyday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ImporterUtils {

    private ObjectMapper om = new ObjectMapper();

    public String getCsvURLFromRFD(String urlJson) throws IOException {
        URL url = new URL(urlJson);
        JsonNode tree = om.readTree(url.openStream());
        List<JsonNode> urls = tree.findValues("accessURL");
        String csvURL = null;
        for (JsonNode n: urls) {
            if (n.textValue().endsWith(".csv"))
                csvURL = n.textValue();
        }
        return csvURL;
    }

    public List<Holyday> readCsv(InputStream is, ConfImport conf) throws IOException {
        List<Holyday> ret = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat(conf.getDateFormat());

        BufferedReader reader = new BufferedReader(new InputStreamReader( is, conf.getCharSet() ));
        String line;
        int lineNum = 0;
        while ((line = reader.readLine()) != null) {
            lineNum++;
            //log.info("Header: " + line);
            if (lineNum <= conf.getSkipRows()) // skip header
                continue;
            String[] row = line.split(conf.getDelimiter(), conf.getMinColumns());
            // Dia;Dia_semana;laborable / festivo / domingo festivo;Tipo de Festivo;Festividad
            // 0   1          2                                     3               4
            Holyday h = new Holyday();
            try {
                h.setDay(df.parse(row[conf.getDayIndex()]));
                if (row.length > conf.getNameIndex())
                    h.setName(row[conf.getNameIndex()]);
            } catch (ParseException e) {
                System.out.println(row[0] + " fecha no valida: " + e.getLocalizedMessage());
                continue;
            }
            if ( conf.isCheckHolydayRow() && !row[conf.getHolydayRowIndex()].toLowerCase().contains(conf.getHolydayRowTest()) )
                continue;
            HolydayType holydayType = conf.calcType(row);
            if (holydayType == null) {
                if (conf.getDefaultType() != null)
                    holydayType = HolydayType.valueOf(conf.getDefaultType());
                else  // fiesta sin tipo!!!
                    continue;
            }
            h.setType(holydayType);
            if (holydayType.equals(HolydayType.LOCAL) && conf.isCheckGetLocal())
                h.setCity(row[conf.getCityIndex()]);
            ret.add(h);
        }
        reader.close();

        return ret;
    }
}

package es.jdl.holydayapi.services.importers;

import es.jdl.holydayapi.domain.Holyday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ImporterCSV implements EntityImporter<Holyday> {

    private final String csvUrl;
    private final ConfigCSV conf;

    public ImporterCSV(String csvUrl, ConfigCSV config) {
        this.csvUrl = csvUrl;
        this.conf = config;
    }

    @Override
    public List<Holyday> readFromSource() throws ImportException {
        List<Holyday> ret = new ArrayList<>();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(conf.getDateFormat());

            BufferedReader reader = new BufferedReader(new InputStreamReader( (new URL(csvUrl)).openStream(), conf.getCharSet() ));
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (lineNum <= conf.getSkipRows()) // skip header
                    continue;
                String[] row = line.split(conf.getDelimiter(), conf.getMinColumns());
                Holyday h = new Holyday();
                try {
                    h.setDay(LocalDate.parse(row[conf.getDayIndex()], formatter));
                    if (row.length > conf.getNameIndex())
                        h.setName(row[conf.getNameIndex()]);
                } catch (DateTimeParseException e) {
                    // FIXME change non-fatal errors handling
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
        } catch (IOException e) {
            throw new ImportException("Have read " + ret.size() + ". " + e.getMessage(), e);
        }

        return ret;
    }
}

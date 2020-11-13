package es.jdl.holidayapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jdl.holidayapi.domain.Holiday;
import es.jdl.holidayapi.persistence.HolidayMapper;
import es.jdl.holidayapi.services.importers.ConfigCSV;
import es.jdl.holidayapi.services.importers.HolidayType;
import es.jdl.holidayapi.services.importers.ImporterUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TestImporters {

    private static final String SPAIN = "ES";
    private static final String MADRID = "28079";
    private static final String MADRID_REGION = "13";
    private static final String GALICIA_REGION = "12";
    private static final String EUSKADI_REGION = "16";

    private HolidayMapper dao;

    public void init() {
    }

    public void testEuskadi() throws IOException, URISyntaxException {
        ImporterUtils utils = new ImporterUtils();
        /*
         Fecha/Data;Descripcion;Deskribapena;Lugar;Tokia;Territorio/lurraldea;Codigo Eustat;Latitud;Longitud
         */
        ConfigCSV conf = new ConfigCSV();
        conf.setSkipRows(1);
        conf.setCharSet("iso-8859-1");
        conf.setCheckGetLocal(true);
        conf.setCityIndex(3);
        conf.setCheckHolydayRow(false);
        //not need: conf.setHolydayRowIndex(2);
        //conf.setHolydayRowTest("festivo");
        conf.setDateFormat("dd/MM/yyyy");
        conf.setNameIndex(1);
        conf.setDayIndex(0);
        conf.setDelimiter(";");
        conf.setMinColumns(6);
        conf.setTypeColIndex(5);
        conf.setTypeCountryTest("todos");
        conf.setDefaultType(HolidayType.LOCAL.toString());
        //conf.setTypeRegionalTest("autonómico");
        //conf.setTypeLocalTest("Local");
        String csvUrl = "https://opendata.euskadi.eus/contenidos/ds_eventos/calendario_laboral_2020/opendata/calendario_laboral_2020.csv";

        List<Holiday> holidays = null; //utils.readCsv(new FileInputStream("./data/calendars/calendario_laboral_2020.csv"), conf);
        for (Holiday h: holidays) {
            System.out.println(h);
            h.setCountry(SPAIN);
            if (h.getType().equals(HolidayType.LOCAL) && (
                    h.getCity().equalsIgnoreCase("Bizkaia") ||
                            h.getCity().equalsIgnoreCase("Gipuzkoa") ||
                            h.getCity().equalsIgnoreCase("Álava - Araba")
                    ) ) {
                h.setType(HolidayType.REGION);
                h.setRegion(EUSKADI_REGION);
            }
            /*
            if (h.getType().equals(HolydayType.LOCAL) || h.getType().equals(HolydayType.REGION)) {
                h.setRegion(GALICIA_REGION);
            }
            insertHoyday(h);

             */
        }
    }

    public void testLoadGalicia() throws IOException {
        ImporterUtils utils = new ImporterUtils();
        /*
Data;Descrici�n;�mbito;id_municipio;concello
01/01/2020;A�o Nuevo;estatal;;
         */
        ConfigCSV conf = new ConfigCSV();
        conf.setSkipRows(2);
        conf.setCharSet("iso-8859-1");
        conf.setCheckGetLocal(true);
        conf.setCityIndex(3);
        conf.setCheckHolydayRow(false);
        //not need: conf.setHolydayRowIndex(2);
        //conf.setHolydayRowTest("festivo");
        conf.setDateFormat("dd/MM/yyyy");
        conf.setNameIndex(1);
        conf.setDayIndex(0);
        conf.setDelimiter(";");
        conf.setMinColumns(5);
        conf.setTypeColIndex(2);
        conf.setTypeCountryTest("estatal");
        conf.setTypeRegionalTest("autonómico");
        conf.setTypeLocalTest("Local");
        String csvUrl = "https://abertos.xunta.gal/catalogo/economia-empresa-emprego/-/dataset/0403/calendario-laboral-2020/002/descarga-directa-ficheiro.csv";
        List<Holiday> holidays = null; //utils.readCsv((new URL(csvUrl)).openStream(), conf);
        for (Holiday h: holidays) {
            System.out.println(h);
            h.setCountry(SPAIN);
            if (h.getType().equals(HolidayType.LOCAL) || h.getType().equals(HolidayType.REGION)) {
                h.setRegion(GALICIA_REGION);
            }
            dao.insertHolyday(h);
        }
    }

    public void testLoadCityMadrid() throws IOException {
        ImporterUtils utils = new ImporterUtils();
        String csvUrl = utils.getCsvURLFromRFD("https://datos.madrid.es/egob/catalogo/title/Calendario%20laboral.json");

        // Dia;Dia_semana;laborable / festivo / domingo festivo;Tipo de Festivo;Festividad
        // 0   1          2                                     3               4
        ConfigCSV conf = new ConfigCSV();
        conf.setCharSet("iso-8859-1");
        conf.setCheckGetLocal(false);
        conf.setCheckHolydayRow(true);
        conf.setDateFormat("dd/MM/yyyy");
        conf.setDayIndex(0);
        conf.setDelimiter(";");
        conf.setMinColumns(5);
        conf.setNameIndex(4);
        conf.setHolydayRowIndex(2);
        conf.setHolydayRowTest("festivo");
        conf.setTypeColIndex(3);
        conf.setTypeCountryTest("nacional");
        conf.setTypeRegionalTest("Comunidad de Madrid");
        conf.setTypeLocalTest("ciudad de Madrid");
        List<Holiday> holidays = null; //utils.readCsv((new URL(csvUrl)).openStream(), conf);
        for (Holiday h: holidays) {
            h.setCountry(SPAIN);
            if (h.getType().equals(HolidayType.LOCAL)) {
                h.setRegion(MADRID_REGION);
                h.setCity(MADRID);
            } else if (h.getType().equals(HolidayType.REGION)) {
                h.setRegion(MADRID_REGION);
            }
            System.out.println(h);
            dao.insertHolyday(h);
        }
    }

    public void importMadridOld() throws Exception {
        Driver drv = (Driver) Class.forName("org.postgresql.Driver").getConstructor().newInstance();
        DriverManager.registerDriver(drv);
        ObjectMapper om = new ObjectMapper();
        URL url = new URL("https://datos.madrid.es/egob/catalogo/title/Calendario%20laboral.json");
        /*
            "format": "linked-data-api",
            "version": "0.2",
            ...
            "accessURL": "https://datos.madrid.es/egob/catalogo/300082-0-calendario_laboral.csv",
         */
        JsonNode tree = om.readTree(url.openStream());
        List<JsonNode> urls = tree.findValues("accessURL");
        String csvURL = null;
        for (JsonNode n: urls) {
            if (n.textValue().endsWith(".csv"))
                csvURL = n.textValue();
        }
        if (csvURL != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader( (new URL(csvURL)).openStream(), "iso-8859-1" ));
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                //log.info("Header: " + line);
                if (lineNum == 1) // skip header
                    continue;
                String[] row = line.split(";", 5);
                // Dia;Dia_semana;laborable / festivo / domingo festivo;Tipo de Festivo;Festividad
                // 0   1          2                                     3               4
                Holiday h = new Holiday();
                try {
                    h.setDay(LocalDate.parse(row[0], formatter));
                    if (row.length > 4)
                        h.setName(row[4]);
                } catch (DateTimeParseException e) {
                    System.out.println(row[0] + " fecha no valida: " + e.getLocalizedMessage());
                    continue;
                }
                if (row[2].contains("festivo")) {
                    if (row.length >= 2) {
                        if ("Festivo nacional".equalsIgnoreCase(row[3])) {
                            h.setCountry(SPAIN);
                        } else if ("Festivo de la Comunidad de Madrid".equalsIgnoreCase(row[3])) {
                            h.setCountry(SPAIN);
                            h.setRegion(MADRID_REGION);
                        } else if ("Festivo local de la ciudad de Madrid".equalsIgnoreCase(row[3])) {
                            h.setCountry(SPAIN);
                            h.setRegion(MADRID_REGION);
                            h.setCity(MADRID);
                        } else {
                            System.out.println("Festivo sin 'tipo' " + line);
                            continue;
                        }
                        System.out.println(h);
                        dao.insertHolyday(h);
                    } // existe festivo
                }
            }
            reader.close();            
        }

    }

}

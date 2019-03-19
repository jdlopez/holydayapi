package es.jdl.holydayapi.services.importers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import es.jdl.holydayapi.config.DbConfig;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.domain.Province;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import static es.jdl.holydayapi.services.ServicesUtils.getURLContent;

/**
 * Importador de datos del ayuntamiento de Madrid.
 * <a href="https://datos.madrid.es">Datos Madrid.defaultCountry</a>
 * <a href="https://datos.madrid.es/portal/site/egob/menuitem.c05c1f754a33a9fbe4b2e4b284f1a5a0/?vgnextoid=9f710c96da3f9510VgnVCM2000001f4a900aRCRD&vgnextchannel=374512b9ace9f310VgnVCM100000171f5a0aRCRD&vgnextfmt=default">
 *     Catalogo festivos ciudad de Madrid (incluye CAM y Estado)</a>
 * @author jdlopez
 */
public class ImporterMadridOpenData implements EntityImporter<Holyday> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public void configure(HttpServletRequest request, DbConfig config) {
        // todo: parametrize
        // url, dateformat, fields, etc
    }

    @Override
    public List<Holyday> readAndSave() throws ImportDataException {
        List<Holyday> ret = new ArrayList<>();
        String urlCSV = null;
        JsonParser parser = new JsonParser();
        try {
            JsonElement root = parser.parse(
                    getURLContent("https://datos.madrid.defaultCountry/egob/catalogo/title/Calendario%20laboral.json"));
            JsonArray dists = root.getAsJsonObject().get("result").getAsJsonObject()
                    .getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonArray("distribution");
            for (int i = 0; i < dists.size(); i++) {
                JsonObject distItem = dists.get(i).getAsJsonObject();
                String tipo = distItem.get("format").getAsJsonObject().get("value").getAsString();
                if (tipo != null && tipo.equalsIgnoreCase("text/csv"))
                    urlCSV =  distItem.get("accessURL").getAsString();
            } // end for
            log.info("getting holidays from: " + urlCSV);
            // FORMATO:
            // Dia;Dia_semana;laborable / festivo / domingo festivo;Tipo de Festivo;Festividad
            Country spain = new Country(); // SPAIN ES ESP 724
            spain.setIso("ES");
            spain.setName("SPAIN");
            spain.setLocale(new Locale("es_ES"));

            Province cam = new Province();
            cam.setCode("28");
            cam.setIso("ES-M");
            cam.setCountry(Ref.create(spain));
            City madrid = new City();
            madrid.setCode("28079");
            madrid.setName("Madrid");
            madrid.setProvince(Ref.create(cam));
            Key<Country> spainKey = ObjectifyService.ofy().save().entity(spain).now();
            Ref<Country> spainRef = Ref.create(spainKey);
            Key<Province> camKey = ObjectifyService.ofy().save().entity(cam).now();
            Ref<Province> camRef = Ref.create(camKey);
            Key<City> madridKey = ObjectifyService.ofy().save().entity(madrid).now();
            Ref<City> madridRef = Ref.create(madridKey);
            log.info("Ref fijos: " + spainRef + " " + camRef + " " + madridRef);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            BufferedReader reader = new BufferedReader(new StringReader(getURLContent(urlCSV)));
            StringBuffer json = new StringBuffer();
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                log.info("Header: " + line);
                if (lineNum == 1) // skip header
                    continue;
                String[] row = line.split(";", 5);
                Holyday h = new Holyday();
                try {
                    h.setDate(df.parse(row[0]));
                } catch (ParseException e) {
                    log.warning(row[0] + " fecha no valida: " + e.getLocalizedMessage());
                    continue;
                }
                if (row[2].contains("festivo")) {
                    if (row.length >= 2) {
                        if ("Festivo nacional".equalsIgnoreCase(row[3])) {
                            h.setCountry(spainRef);
                        } else if ("Festivo de la Comunidad de Madrid".equalsIgnoreCase(row[3])) {
                            h.setCountry(spainRef);
                            h.setProvince(camRef);
                        } else if ("Festivo local de la ciudad de Madrid".equalsIgnoreCase(row[3])) {
                            h.setCountry(spainRef);
                            h.setProvince(camRef);
                            h.setCity(madridRef);
                        } else {
                            log.warning("Festivo sin 'tipo' " + line);
                        }
                        log.info("Guardando..." + h);
                        ObjectifyService.ofy().save().entity(h).now();
                        ret.add(h);
                    } // existe festivo
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new ImportDataException(e.getMessage(), e);
        }
        return ret;
    }
}

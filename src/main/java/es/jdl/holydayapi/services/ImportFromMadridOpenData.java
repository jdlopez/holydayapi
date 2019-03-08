package es.jdl.holydayapi.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.GsonBuildConfig;
import com.googlecode.objectify.Ref;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.domain.Province;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Importador de datos del ayuntamiento de Madrid.
 * <a href="https://datos.madrid.es">Datos Madrid.es</a>
 * <a href="https://datos.madrid.es/portal/site/egob/menuitem.c05c1f754a33a9fbe4b2e4b284f1a5a0/?vgnextoid=9f710c96da3f9510VgnVCM2000001f4a900aRCRD&vgnextchannel=374512b9ace9f310VgnVCM100000171f5a0aRCRD&vgnextfmt=default">
 *     Catalogo festivos ciudad de Madrid (incluye CAM y Estado)</a>
 * @author jdlopez
 */
public class ImportFromMadridOpenData {

    private static final Logger log = Logger.getLogger(ImportFromMadridOpenData.class.getName());

    public void importHolydays() throws IOException {

        String urlCSV = null;
        JsonParser parser = new JsonParser();
        // todo: parametrize
        JsonElement root = parser.parse(getURLContent("https://datos.madrid.es/egob/catalogo/title/Calendario%20laboral.json"));
        JsonArray dists = root.getAsJsonObject().get("result").getAsJsonObject().getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonArray("distribution");
        for (int i = 0; i < dists.size(); i++) {
            JsonObject distItem = dists.get(i).getAsJsonObject();
            String tipo = distItem.get("format").getAsJsonObject().get("value").getAsString();
            if (tipo != null && tipo.equalsIgnoreCase("text/csv"))
                urlCSV =  distItem.get("accessURL").getAsString();
        } // end for
        log.info("getting holidays from: " + urlCSV);
        BufferedReader reader = new BufferedReader(new InputStreamReader((new URL(urlCSV)).openStream()));
        StringBuffer json = new StringBuffer();
        String line;
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

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        while ((line = reader.readLine()) != null) {
            String[] row = line.split(";");
            Holyday h = new Holyday();
            try {
                h.setDate(df.parse(row[0]));
            } catch (ParseException e) {
                log.warning(row[0] + " fecha no valida: " + e.getLocalizedMessage());
                continue;
            }
            if (row[2].contains("festivo")) {
                if ("Festivo nacional".equalsIgnoreCase(row[3])) {
                    h.setCountry(Ref.create(spain));
                } else if ("Festivo de la Comunidad de Madrid".equalsIgnoreCase(row[3])) {
                    h.setCountry(Ref.create(spain));
                    h.setProvince(Ref.create(cam));
                } else if ("Festivo local de la ciudad de Madrid".equalsIgnoreCase(row[3])) {
                    h.setCountry(Ref.create(spain));
                    h.setProvince(Ref.create(cam));
                    h.setCity(Ref.create(madrid));
                }
                System.out.println(h);
            }
            // tipos:
            // sabado
            // domingo
            // Festivo nacional
            // Festivo de la Comunidad de Madrid
            // festivo;Festivo local de la ciudad de Madrid

        }
        reader.close();


    }

    /**
     * Lee linea a linea ¿util o mejor getContent?
     * @param url
     * @return
     * @throws IOException
     */
    protected String getURLContent(String url) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader((new URL(url)).openStream()));
        StringBuffer json = new StringBuffer();
        String line;

        while ((line = reader.readLine()) != null) {
            json.append(line);
        }
        reader.close();
        return json.toString();
    }
}

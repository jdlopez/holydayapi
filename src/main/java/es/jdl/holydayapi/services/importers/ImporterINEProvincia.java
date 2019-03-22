package es.jdl.holydayapi.services.importers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import es.jdl.holydayapi.config.DbConfig;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;
import es.jdl.holydayapi.services.ServicesUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utiliza las tablas de INE. Accesibles como JSON gracias a : https://github.com/IagoLast/pselect
 */
public class ImporterINEProvincia implements EntityImporter<Province> {

    @Override
    public void configure(HttpServletRequest request, DbConfig config) throws ImportDataException {

    }

    @Override
    public List<Province> readAndSave() throws ImportDataException {
        List<Province> ret = new ArrayList<>();
        try {
            String provincias = ServicesUtils.getURLContent("https://raw.githubusercontent.com/IagoLast/pselect/master/data/provincias.json");
            /*
            {
            "id": "04",
            "nm": "Almería"
            }
             */
            Country spain = ObjectifyService.ofy().load().type(Country.class).id("ES").now();
            Ref<Country> esRef = Ref.create(spain);
            JsonElement jsonProv = new JsonParser().parse(provincias);
            if (jsonProv.isJsonArray()) { // es correcto
                JsonArray arr = jsonProv.getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject provNode = arr.get(i).getAsJsonObject();
                    Province p = new Province();
                    p.setCode(provNode.getAsJsonPrimitive("id").getAsString());
                    p.setName(provNode.getAsJsonPrimitive("nm").getAsString().toUpperCase());
                    p.setCountry(esRef);
                    ObjectifyService.ofy().save().entity(p).now();
                    ret.add(p);
                }
            }
        } catch (IOException e) {
            throw new ImportDataException(e.getMessage(), e);
        }

        return ret;
    }
}

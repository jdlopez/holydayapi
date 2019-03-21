package es.jdl.holydayapi.services.importers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import es.jdl.holydayapi.config.DbConfig;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;
import es.jdl.holydayapi.services.ServicesUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ImporterINEMunicipios implements EntityImporter<City> {
    @Override
    public void configure(HttpServletRequest request, DbConfig config) throws ImportDataException {

    }

    @Override
    public List<City> readAndSave() throws ImportDataException {
        Logger log = Logger.getLogger(this.getClass().getName());
        List<City> ret = new ArrayList<>();
        try {
            String municipios = ServicesUtils.getURLContent("https://raw.githubusercontent.com/IagoLast/pselect/master/data/municipios.json");
            /*
            {
            "id": "01002",
            "nm": "Amurrio"
            }
             */
            Country spain = ObjectifyService.ofy().load().type(Country.class).id("ES").now();
            Ref<Country> esRef = Ref.create(spain);
            JsonElement jsonMunicipios = new JsonParser().parse(municipios);
            if (jsonMunicipios.isJsonArray()) { // es correcto
                JsonArray arr = jsonMunicipios.getAsJsonArray();
                log.info("Vamos a leer " + arr.size() + " entradas...");
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject provNode = arr.get(i).getAsJsonObject();
                    City c = new City();
                    c.setCode(provNode.getAsJsonPrimitive("id").getAsString());
                    c.setName(provNode.getAsJsonPrimitive("nm").getAsString().toUpperCase());
                    Province p = ObjectifyService.ofy().load().type(Province.class).id(c.getCode().substring(0, 2)).now();
                    if (p != null) {
                        c.setProvince(Ref.create(p));
                        ObjectifyService.ofy().save().entity(p).now();
                        ret.add(c);
                    } else {
                        log.warning("No existe la provincia para: " + c.getCode());
                    }
                }
            }
        } catch (IOException e) {
            throw new ImportDataException(e.getMessage(), e);
        }

        return ret;
    }
}

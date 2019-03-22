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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ImporterINEMunicipios implements EntityImporter<City> {

    private boolean porProvincia = false;
    private String provincia;

    @Override
    public void configure(HttpServletRequest request, DbConfig config) throws ImportDataException {
        if (request != null) {
            this.porProvincia = Boolean.valueOf(request.getParameter("porProvincia"));
            this.provincia = request.getParameter("porProvincia");
        }
    }

    @Override
    public List<City> readAndSave() throws ImportDataException {
        Logger log = Logger.getLogger(this.getClass().getName());
        List<City> ret = new ArrayList<>();
        try {
            String municipios = ServicesUtils.getURLContent("https://raw.githubusercontent.com/jdlopez/holydayapi/master/data/municipios.json");
            /*
            {
            "id": "01002",
            "nm": "Amurrio"
            }
             */
            Country spain = ObjectifyService.ofy().load().type(Country.class).id("ES").now();
            Map<String, Ref<Province>> mapProvincias = new HashMap<>();
            Ref<Country> esRef = Ref.create(spain);
            JsonElement jsonMunicipios = new JsonParser().parse(municipios);
            List<City> pendientes = new ArrayList<>();
            if (jsonMunicipios.isJsonArray()) { // es correcto
                JsonArray arr = jsonMunicipios.getAsJsonArray();
                log.info("Vamos a leer " + arr.size() + " entradas...");
                int blockSize = 150;
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject provNode = arr.get(i).getAsJsonObject();
                    String code = provNode.getAsJsonPrimitive("id").getAsString();
                    if (porProvincia && provincia != null && code != null && !code.startsWith(provincia)) {
                        continue;
                    }
                    City c = new City();
                    c.setCode(code);
                    c.setName(provNode.getAsJsonPrimitive("nm").getAsString().toUpperCase());
                    String keyProv = c.getCode().substring(0, 2);
                    Ref<Province> p = null;
                    if (mapProvincias.containsKey(keyProv))
                        p = mapProvincias.get(keyProv);
                    else { // ojo, ref de null -> exception
                        Province prov = ObjectifyService.ofy().load().type(Province.class).id(keyProv).now();
                        if (prov == null)
                            throw new ImportDataException("Provincia " + keyProv + " no encontrada!", null);
                        p = Ref.create(prov);
                        mapProvincias.put(p.get().getCode(), p);
                    }
                    c.setProvince(p);
                    pendientes.add(c);
                    if (pendientes.size() == blockSize) {
                        log.info("Guardando pendientes: " + pendientes.size());
                        ObjectifyService.ofy().save().entities(pendientes); // no es necesario q sea sincrono (.now)
                        pendientes.clear();
                    }
                    ret.add(c);
                } // for
                if (!pendientes.isEmpty())
                    ObjectifyService.ofy().save().entities(pendientes);
            }
        } catch (IOException e) {
            throw new ImportDataException(e.getMessage(), e);
        }

        return ret;
    }
}
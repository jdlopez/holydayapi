package es.jdl.holydayapi.services;

import com.google.appengine.api.taskqueue.DeferredTask;
import es.jdl.holydayapi.domain.Province;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Importador de municipios 'deferred'
 * @see ImportProvinceCity
 * @author jdlopez
 */
public class ImportCityDeferred implements DeferredTask {

    private final Province province;

    public ImportCityDeferred(Province province) {
        this.province = province;
    }

    @Override
    public void run() {
        try {
            ImportProvinceCity importer = new ImportProvinceCity();
            importer.importESCitiesByProvince(province);
        } catch (ParserConfigurationException  | IOException  | SAXException e) {
            throw new RuntimeException("Loading " + province + " " + e.getMessage(), e);
        }
    }
}

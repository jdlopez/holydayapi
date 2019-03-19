package es.jdl.holydayapi.services;

import com.google.appengine.api.taskqueue.DeferredTask;
import es.jdl.holydayapi.services.importers.EntityImporter;
import es.jdl.holydayapi.services.importers.ImportDataException;
import es.jdl.holydayapi.services.importers.ImporterCountry;

/**
 * Importador de municipios 'deferred' (ahora de cualquiera)
 * @see ImporterCountry
 * @author jdlopez
 */
public class ImportDeferred implements DeferredTask {

    private final EntityImporter imp;

    public ImportDeferred(EntityImporter imp) {
        this.imp = imp;
    }

    @Override
    public void run() {
        try {
            imp.readAndSave();
        } catch (ImportDataException e) {
            throw new RuntimeException("Loading " + imp + " " + e.getMessage(), e);
        }
    }
}

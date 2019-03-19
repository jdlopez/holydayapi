package es.jdl.holydayapi.services.importers;

import es.jdl.holydayapi.config.DbConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Import behavior common to any entity
 * @param <T>
 */
public interface EntityImporter<T> {

    void configure(HttpServletRequest request, DbConfig config);

    List<T> readAndSave() throws ImportDataException;
}

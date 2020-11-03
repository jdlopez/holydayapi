package es.jdl.holydayapi.services.importers;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Import behavior common to any entity
 * @param <T>
 */
public interface EntityImporter<T> {

    List<T> readFromSource() throws ImportException;
}

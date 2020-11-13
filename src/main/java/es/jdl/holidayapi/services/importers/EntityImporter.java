package es.jdl.holidayapi.services.importers;

import java.util.List;

/**
 * Import behavior common to any entity
 * @param <T>
 */
public interface EntityImporter<T> {

    List<T> readFromSource() throws ImportException;
}

package es.jdl.holydayapi.services.importers;

/**
 * Exception for Import operations
 * @author jdlopez
 */
public class ImportDataException extends Exception {
    public ImportDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

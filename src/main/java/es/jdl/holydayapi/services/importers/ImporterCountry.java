package es.jdl.holydayapi.services.importers;

import com.googlecode.objectify.ObjectifyService;
import es.jdl.holydayapi.config.DbConfig;
import es.jdl.holydayapi.domain.Country;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Create localization data from locale and APIs
 */
public class ImporterCountry implements EntityImporter<Country> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public void configure(HttpServletRequest request, DbConfig config) {
        // none? maybe language
    }

    @Override
    public List<Country> readAndSave() {
        String[] countries = Locale.getISOCountries();
        List<Country> ret = new ArrayList<>(countries.length);
        log.info("countries: " + Arrays.toString(countries));
        for (String c: countries) {
            Locale countryLoc = new Locale("", c);
            Country country = new Country();
            country.setIso(c);
            country.setName(countryLoc.getDisplayName(Locale.ENGLISH)); // force english?
            country.setLocale(countryLoc);
            ObjectifyService.ofy().save().entity(country).now();
            ret.add(country);
        }
        return ret;
    }
}

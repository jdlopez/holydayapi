package es.jdl.holydayapi.services;

import com.googlecode.objectify.ObjectifyService;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;

/**
 * Pseodo DAO. ObjectifyService.ofy() as field??
 * @author jdlopez
 */
public class SharedDataDao {

    public Province findProvinceById(String code) {
        return ObjectifyService.ofy().load().type(Province.class).id(code).now();
    }

    public Country findCountryById(String iso) {
        return ObjectifyService.ofy().load().type(Country.class).id(iso).now();
    }

    public City findCityById(String code) {
        return ObjectifyService.ofy().load().type(City.class).id(code).now();
    }
}

package es.jdl.holydayapi.services.rest;

import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;
import es.jdl.holydayapi.services.persistence.HolydayDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * REST Service (servlet) list datasotore entities: country, province, city (filtered by prov)
 * @author jdlopez
 */
@RestController
@RequestMapping("/list")
public class ListDataService {

    @Autowired
    private HolydayDao holydayDao;

    @GetMapping (path = "/country")
    public List<Country> getAllCountries() {
        return holydayDao.selectAllCountries();
    }

    @GetMapping (path = "/province/country/{countryCode}")
    public List<Province> getProvinceFromCountry(@NotNull @PathVariable String countryCode) {
        return holydayDao.selectProvinceByCountryCode(countryCode.toUpperCase());
    }

    @GetMapping (path = "/province/name/{name}")
    public Province getProvinceFromName(@NotNull @PathVariable String name) {
        return holydayDao.selectProvinceByName(name.toUpperCase());
    }

    @GetMapping (path = "/city/privince/{provinceCode}")
    public List<City> getCitiesFromProvince(@Digits (integer = 2 , fraction = 0) @PathVariable String provinceCode) {
        return holydayDao.selectCityByProvCode(provinceCode);
    }

    @GetMapping (path = "/city/name/{name}")
    public City findCityByName(@NotNull @PathVariable String name) {
        return holydayDao.selectCityByName(name);
    }
}

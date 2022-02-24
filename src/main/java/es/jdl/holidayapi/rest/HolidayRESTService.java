package es.jdl.holidayapi.rest;

import es.jdl.holidayapi.domain.*;
import es.jdl.holidayapi.persistence.HolidayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/holidays")
public class HolidayRESTService {

    @Autowired
    private HolidayMapper dao;

    @GetMapping (path = {
            "/country_code/{countryCode}",
            "/country_code/{countryCode}/year/{year}",
    })
    public List<Holiday> findByCountry(@NotNull @PathVariable String countryCode, @PathVariable(required = false) Integer year) {
        Country country = dao.selectCountryByCode(countryCode.toUpperCase());
        if (country == null)
            throw new RuntimeException(countryCode + " not found");
        return findHolidaysByCountry(country, year);
    }

    @GetMapping (path = {
            "/city/{cityName}",
            "/city/{cityName}/year/{year}",
    })
    public List<Holiday> findByCity(@NotNull @PathVariable String cityName, @PathVariable(required = false) Integer year) {
        City city = dao.selectCityByLowerName(cityName.toLowerCase());
        if (city == null)
            throw new RuntimeException(cityName + " not found");
        return findHolidays(city, year);
    }

    @GetMapping (path = {
            "/city_code/{cityCode}",
            "/city_code/{cityCode}/year/{year}",
    })
    public List<Holiday> findByCityCode(@NotNull @PathVariable String cityCode, @PathVariable(required = false) Integer year) {
        City city = dao.selectCityByCode(cityCode);
        if (city == null)
            throw new RuntimeException(cityCode + " not found");
        return findHolidays(city, year);
    }


    protected List<Holiday> findHolidays(City city, Integer year) {
        Region region = dao.selectRegionByProvinceCode(city.getProvinceCode());
        if (region == null)
            throw new RuntimeException(city.getProvinceCode() + " not found");

        if (year == null) {
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
        }

        return dao.selectHolidayByCityAndDate(city, LocalDate.of(year,1, 1),
                LocalDate.of(year,12, 31), region.getCode(), region.getCountryCode());
    }

    protected List<Holiday> findHolidaysByCountry(Country country, Integer year) {

        if (year == null) {
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
        }
        DateInterval interval = new DateInterval (LocalDate.of(year,1, 1), LocalDate.of(year,12, 31));
        return dao.selectHolidayByCountryAndDate(country.getCode(), interval);
    }
}

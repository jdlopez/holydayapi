package es.jdl.holydayapi.services.rest;

import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.domain.Region;
import es.jdl.holydayapi.services.persistence.HolydayDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping ("/holyday")
public class HolydayRESTService {
    @Autowired
    private HolydayDao holydayDao;

    @GetMapping (path = {
            "/city/name/{cityName}",
            "/year/{year}/city/name/{cityName}",
    })
    public List<Holyday> findByCity(@NotNull @PathVariable String cityName, @PathVariable(required = false) Integer year) {
        City city = holydayDao.selectCityByName(cityName);
        if (city == null)
            throw new RuntimeException(cityName + " not found");
        //String country = holydayDao.selectCountryCodeByProvinceCode(city.getProvinceCode());
        Region region = holydayDao.selectRegionByProvinceCode(city.getProvinceCode());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        if (year != null) {
            cal.set(Calendar.YEAR, year);
        }
        Date fromDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        return holydayDao.selectHolydayByCity(city, fromDate, cal.getTime(), region.getCode(), region.getCountryCode());
    }
}

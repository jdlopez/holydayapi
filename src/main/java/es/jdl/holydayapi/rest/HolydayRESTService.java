package es.jdl.holydayapi.rest;

import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.domain.Region;
import es.jdl.holydayapi.persistence.HolydayMapper;
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
@RequestMapping("/holyday")
public class HolydayRESTService {

    @Autowired
    private HolydayMapper dao;

    @GetMapping (path = {
            "/city/name/{cityName}",
            "/year/{year}/city/name/{cityName}",
    })
    public List<Holyday> findByCity(@NotNull @PathVariable String cityName, @PathVariable(required = false) Integer year) {
        City city = dao.selectCityByLowerName(cityName.toLowerCase());
        if (city == null)
            throw new RuntimeException(cityName + " not found");
        //String country = holydayDao.selectCountryCodeByProvinceCode(city.getProvinceCode());
        Region region = dao.selectRegionByProvinceCode(city.getProvinceCode());
        if (region == null)
            throw new RuntimeException(city.getProvinceCode() + " not found");

        if (year == null) {
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
        }

        return dao.selectHolydayByCityAndDate(city, LocalDate.of(year,1, 1),
                LocalDate.of(year,12, 31), region.getCode(), region.getCountryCode());
    }
}

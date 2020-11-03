package es.jdl.holydayapi.rest;

import es.jdl.holydayapi.persistence.HolydayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@RestController
public class InfoRestService {

    @Autowired
    private HolydayMapper mapper;

    @GetMapping (path = "/info")
    public HashMap<String, Object> info() {
        HashMap<String, Object> ret = new HashMap<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        ret.put("region", mapper.selectCountHolydaysRegion(year));
        ret.put("city", mapper.selectCountHolydaysCities(year));
        ret.put("country", mapper.selectCountHolydaysCountry(year));
        return ret;
    }

}

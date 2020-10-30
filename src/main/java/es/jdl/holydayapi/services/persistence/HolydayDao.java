package es.jdl.holydayapi.services.persistence;

import com.dieselpoint.norm.Database;
import com.dieselpoint.norm.Query;
import es.jdl.holydayapi.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class HolydayDao {

    @Autowired
    private Database database;

    public List<Holyday> selectHolydayByCity(City city, Date from, Date to, String regionCode, String countryCode) {
        Query q = database.table("Holyday")
                .where("day >=? and day <=? and " +
                        "( (city=?) " +
                        "or (region=? and city is null) " +
                        "or (country=? and region is null and city is null) )",
                        from, to, city.getCode(), regionCode, countryCode)
                .orderBy("day");
        return q.results(Holyday.class);
    }

    public List<Country> selectAllCountries() {
        return database.table("Country").results(Country.class);
    }

    public List<Province> selectProvinceByCountryCode(String countryCode) {
        return database.table("Province").where("UPPER(countryCode)=?", countryCode).results(Province.class);
    }

    public City selectCityByName(String name) {
        return database.table("City").where("UPPER(name)=?", name.toUpperCase()).first(City.class);
    }

    public List<City> selectCityByProvCode(String provinceCode) {
        return database.table("City").where("province=?", provinceCode).results(City.class);
    }

    public Province selectProvinceByName(String name) {
        return database.table("Province").where("UPPER(name)=?", name).first(Province.class);
    }

    public String selectCountryCodeByProvinceCode(String provinceCode) {
        return database.sql("select countryCode from Province where code = ?", provinceCode).first(String.class);
    }

    public Region selectRegionByProvinceCode(String provinceCode) {
        return database.sql("select distinct r.* from region r join province p on r.code = p.regionCode where p.code = ?",
                provinceCode).first(Region.class);
    }
}

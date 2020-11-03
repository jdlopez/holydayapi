package es.jdl.holydayapi.persistence;

import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;
import es.jdl.holydayapi.domain.Region;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BasicDataMapper {

    @Insert("insert into country (code, name) values (#{c.code}, #{c.name})")
    int insertCountry(@Param("c") Country c);

    @Insert("insert into region (code, name, countrycode) values (#{r.code}, #{r.name}, #{r.countryCode})")
    int insertRegion(@Param("r") Region r);

    @Insert("insert into province (code, name, iso, countrycode, regioncode) " +
            "values (#{p.code}, #{p.name}, #{p.iso}, #{p.countryCode}, #{p.regionCode})")
    int insertProvince(@Param("p") Province p);

    @Insert("insert into city (code, name, provincecode) values (#{c.code}, #{c.name}, #{c.provinceCode})")
    int insertCity(@Param("c") City c);

    @Select("select * from country")
    List<Country> selectAllCountries();

    @Select("select * from province where UPPER(countrycode) = #{countryCode}")
    List<Province> selectProvinceByCountryCode(@Param("countryCode") String countryCode);

    @Select("select * from province where UPPER(name) = #{provinceName}")
    Province selectProvinceByName(@Param("provinceName") String provinceName);

    @Select("select * from city where UPPER(provincecode) = #{provinceCode}")
    List<City> selectCityByProvCode(@Param("provinceCode") String provinceCode);

    @Select("select * from city where UPPER(name) like '%${name}%'")
    City selectCityLikeName(@Param("name") String name);
}

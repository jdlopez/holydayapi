package es.jdl.holydayapi.persistence;

import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.domain.Region;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HolydayMapper {

    @Insert(value = "insert into holyday (day, name, city, region, country) " +
            "values (#{day}, #{name}, #{city}, #{region}, #{country})" +
            " ON CONFLICT DO NOTHING ") // databaseId = "postgresql") TODO check this
    int insertHolyday(Holyday h);

    @Select("select * from holyday where day >=#{from} " +
            "and day <=#{to} and ( (city=#{city.code}) " +
            "or (region=#{regionCode} and city is null) " +
            "or (country=#{countryCode} and region is null and city is null) ) " +
            "order by day")
    List<Holyday> selectHolydayByCityAndDate(City city, LocalDate from, LocalDate to, String regionCode, String countryCode);

    @Select("select distinct r.* from region r join province p on r.code = p.regionCode where p.code = #{provinceCode}")
    Region selectRegionByProvinceCode(String provinceCode);

    @Select("select * from city where lower(name) like #{name}")
    City selectCityByLowerName(String cityName);

    @Select({"<script>",
            "select count(*) from holyday where day = #{day} and country = #{country}",
            "<if test='region != null'> and region = #{region}</if>",
            "<if test='region == null'> and region is null</if>",
            "<if test='city != null'> and city = #{city}</if>",
            "<if test='city == null'> and city is null</if>",
            "</script>"
    })
    int checkExists(Holyday h);
}

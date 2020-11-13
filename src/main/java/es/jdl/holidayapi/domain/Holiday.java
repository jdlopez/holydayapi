package es.jdl.holidayapi.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.jdl.holidayapi.services.importers.HolidayType;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Holiday {

    @JsonIgnore
    private Integer id;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate day;
    private String name;
    private String city;
    private String region;
    private String country;
    private HolidayType type;

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public LocalDate getDay() {
        return day;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Holyday{" +
                "id=" + id +
                ", day=" + day +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", type=" + type +
                '}';
    }

    public void setType(HolidayType type) {
        this.type = type;
    }

    public HolidayType getType() {
        if (type == null) {
            if (city != null)
                type = HolidayType.LOCAL;
            if (city == null && region != null)
                type = HolidayType.REGION;
            else if (city == null && region == null && country != null)
                type = HolidayType.COUNTRY;
            // else ERROR!!
        }
        return type;
    }
}

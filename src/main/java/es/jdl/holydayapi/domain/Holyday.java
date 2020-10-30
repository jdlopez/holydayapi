package es.jdl.holydayapi.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.jdl.holydayapi.services.importers.HolydayType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class Holyday {

    @JsonIgnore
    @Id
    @GeneratedValue
    private Integer id;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date day;
    private String name;
    private String city;
    private String region;
    private String country;
    private HolydayType type;

    public void setDay(Date day) {
        this.day = day;
    }

    public Date getDay() {
        return day;
    }

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    @Id
    @GeneratedValue
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

    @Transient
    public void setType(HolydayType type) {
        this.type = type;
    }

    @Transient
    public HolydayType getType() {
        if (type == null) {
            if (city != null)
                type = HolydayType.LOCAL;
            if (city == null && region != null)
                type = HolydayType.REGION;
            else if (city == null && region == null && country != null)
                type = HolydayType.COUNTRY;
            // else ERROR!!
        }
        return type;
    }
}

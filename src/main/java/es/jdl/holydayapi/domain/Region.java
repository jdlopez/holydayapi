package es.jdl.holydayapi.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Main region of a country, sub-divided in provinces. <i>Spanish: Comunidades autónomas</i>
 * @author jdlopez
 */
@Entity
public class Region {
    @Id
    private String code;
    private String name;
    private String countryCode;

    @Id
    public String getCode() {
        return code;
    }

    @Id
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}

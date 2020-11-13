package es.jdl.holidayapi.domain;

/**
 * Main region of a country, sub-divided in provinces. <i>Spanish: Comunidades autónomas</i>
 * @author jdlopez
 */
public class Region {
    private String code;
    private String name;
    private String countryCode;

    public String getCode() {
        return code;
    }

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

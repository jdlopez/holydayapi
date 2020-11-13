package es.jdl.holidayapi.domain;

public class Province {

    // codigo del INE
    private String code;
    private String name;
    // ISO 3166-2:ES
    private String iso;
    private String countryCode;
    private String regionCode;

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

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    @Override
    public String toString() {
        return "Province{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", iso='" + iso + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", regionCode='" + regionCode + '\'' +
                '}';
    }
}

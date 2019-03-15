package es.jdl.holydayapi.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Province {

    // codigo del INE
    @Id
    private String code;
    private String name;
    // ISO 3166-2:ES
    private String iso;
    private Ref<Country> country;

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

    public Ref<Country> getCountry() {
        return country;
    }

    public void setCountry(Ref<Country> country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Province{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", iso='" + iso + '\'' +
                ", country=" + country +
                '}';
    }
}

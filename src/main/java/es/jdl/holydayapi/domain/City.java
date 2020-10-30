package es.jdl.holydayapi.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class City {

    @Id
    private String code;
    private String name;
    private String provinceCode;

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

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    @Override
    public String toString() {
        return "City{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", province=" + provinceCode +
                '}';
    }
}

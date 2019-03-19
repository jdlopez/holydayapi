package es.jdl.holydayapi.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class City {

    @Id
    private String code;
    private String name;
    @Index
    private Ref<Province> province;

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

    public Ref<Province> getProvince() {
        return province;
    }

    public void setProvince(Ref<Province> province) {
        this.province = province;
    }

    @Override
    public String toString() {
        return "City{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", province=" + province +
                '}';
    }
}

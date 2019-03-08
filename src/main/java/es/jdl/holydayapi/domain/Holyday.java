package es.jdl.holydayapi.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Stringify;
import com.googlecode.objectify.stringifier.Stringifier;

import java.util.Date;

@Entity
public class Holyday {
    @Id
    private Long id;// just for data storage
    private Date date;
    private Ref<City> city;
    private Ref<Province> province;
    private Ref<Country> country;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Ref<City> getCity() {
        return city;
    }

    public void setCity(Ref<City> city) {
        this.city = city;
    }

    public Ref<Province> getProvince() {
        return province;
    }

    public void setProvince(Ref<Province> province) {
        this.province = province;
    }

    public Ref<Country> getCountry() {
        return country;
    }

    public void setCountry(Ref<Country> country) {
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Holyday{");
        sb.append("date=").append(date);
        sb.append(", city=").append(city);
        sb.append(", province=").append(province);
        sb.append(", country=").append(country);
        sb.append('}');
        return sb.toString();
    }
}

package es.jdl.holidayapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Locale;

public class Country {

    private String code;
    private String name;
    @JsonIgnore
    private Locale locale;

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

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return "Country{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", locale=" + locale +
                '}';
    }
}

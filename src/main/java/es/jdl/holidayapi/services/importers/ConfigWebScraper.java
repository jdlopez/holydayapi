package es.jdl.holidayapi.services.importers;

public class ConfigWebScraper {
    private String url;
    private String agentName = "Mozilla/5.0";
    private int timeout = 10000;
    private String cityHolydayClass = "bm-calendar-state-local";
    private String monthClass = "bm-calendar-month-title";
    private Integer predefinedYear;
    private String regionHolydayClass = "bm-calendar-state-autonomico";
    private String country;
    private String region;
    private String city;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getCityHolydayClass() {
        return cityHolydayClass;
    }

    public void setCityHolydayClass(String cityHolydayClass) {
        this.cityHolydayClass = cityHolydayClass;
    }

    public String getMonthClass() {
        return monthClass;
    }

    public void setMonthClass(String monthClass) {
        this.monthClass = monthClass;
    }

    public Integer getPredefinedYear() {
        return predefinedYear;
    }

    public void setPredefinedYear(Integer predefinedYear) {
        this.predefinedYear = predefinedYear;
    }

    public String getRegionHolydayClass() {
        return regionHolydayClass;
    }

    public void setRegionHolydayClass(String regionHolydayClass) {
        this.regionHolydayClass = regionHolydayClass;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}

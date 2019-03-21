package es.jdl.holydayapi.services.importers;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import es.jdl.holydayapi.config.DbConfig;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.services.ServicesUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class ImporterCityScrapper implements EntityImporter<Holyday> {

    private String url;
    private String userAgent;
    private int timeout;
    private String cityHolydayClass;
    private String monthClass;
    private City city;
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private String provinceHolydayClass;

    @Override
    public void configure(HttpServletRequest request, DbConfig config) throws ImportDataException {
        this.url  = config.getProperty("base_url", "https://calendarios.ideal.es/laboral/")
                + request.getParameter("uriSuffix");
        this.userAgent = config.getProperty("user_agent", "Mozilla/5.0");
        this.timeout = config.getIntProperty("timeout", "100000");
        this.cityHolydayClass = config.getProperty("cityHolydayClass", "bm-calendar-state-local");
        this.provinceHolydayClass = config.getProperty("provinceHolydayClass", "bm-calendar-state-autonomico");
        this.monthClass = config.getProperty("monthClass", "bm-calendar-month-title");
        this.city = ObjectifyService.ofy().load().type(City.class).id(request.getParameter("city")).now();
        if (city == null)
            throw new ImportDataException("City not found!! " + request.getParameter("city"), null);

    }

    @Override
    public List<Holyday> readAndSave() throws ImportDataException {
        List<Holyday> ret = new ArrayList<>();
        log.info("Leyendo y procesando: " + city);
        try {
            String content = ServicesUtils.getURLContent(url);
            if (content != null) {
                Document doc = Jsoup.parse(content);
                Holyday h = new Holyday();
                h.setCity(Ref.create(city));
                h.setProvince(city.getProvince());
                h.setCountry(city.getProvince().get().getCountry());
                ret.addAll(parseHtml(h, doc.getElementsByClass(cityHolydayClass)));
                h.setCity(null);
                ret.addAll(parseHtml(h, doc.getElementsByClass(provinceHolydayClass)));
            }
        } catch (IOException e) {
            throw new ImportDataException(e.getMessage(), e);
        }
        return ret;
    }

    private List<Holyday> parseHtml(Holyday baseDay, Elements days) {
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", new Locale("ES"));
        List<Holyday> ret = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (Element e: days) {
            String nombre = e.attr("title");
            String dia = e.getElementsByTag("a").text();
            if (dia == null)
                dia = e.text();
            Elements tagsMes = e.parent().parent().parent().getElementsByClass(this.monthClass);
            String mes = null;
            if (tagsMes.size() > 0)
                mes = tagsMes.get(0).text();
            if (dia != null && !"".equals(dia) && mes != null && !"".equals(mes)) {
                Holyday h = new Holyday();
                h.setCountry(baseDay.getCountry());
                h.setProvince(baseDay.getProvince());
                h.setCity(baseDay.getCity());
                h.setName(nombre);
                try {
                    h.setDate(df.parse(String.format("%s %s %d", dia, mes, year)));
                    ObjectifyService.ofy().save().entity(h).now();
                    ret.add(h);
                } catch (ParseException e1) {
                    log.warning("Fecha incorrcta: " + dia + " " + mes + " " + year + ": " + e1.getMessage());
                }
            } else {
                log.warning("Fecha incompleta: " + dia + " " + mes + " " + year + " " + nombre);
            }
        }
        return ret;
    }


}

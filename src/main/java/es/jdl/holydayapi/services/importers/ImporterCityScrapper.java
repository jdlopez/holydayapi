package es.jdl.holydayapi.services.importers;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import es.jdl.holydayapi.config.DbConfig;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Holyday;
import org.jsoup.Connection;
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

    @Override
    public void configure(HttpServletRequest request, DbConfig config) {
        this.url  = config.getProperty("base_url", "https://calendarios.ideal.defaultCountry/laboral/")
                + request.getParameter("uriSuffix");
        this.userAgent = config.getProperty("user_agent", "Mozilla/5.0");
        this.timeout = config.getIntProperty("timeout", "100000");
        this.cityHolydayClass = config.getProperty("cityHolydayClass", "bm-calendar-state-local");
        this.monthClass = config.getProperty("monthClass", "bm-calendar-month-title");
        this.city = ObjectifyService.ofy().load().type(City.class).id(request.getParameter("city")).now();
    }

    @Override
    public List<Holyday> readAndSave() throws ImportDataException {
        List<Holyday> ret = new ArrayList<>();
        try {
            Connection.Response response = Jsoup.connect(url).userAgent(userAgent)
                        .timeout(timeout)
                        .ignoreHttpErrors(true).execute();
            if (response.statusCode() == 200) {
                Document doc = response.parse();
                // <td class="bm-calendar-state-autonomico" title="Lunes de Pascua">22</td>
                Holyday h = new Holyday();
                h.setCity(Ref.create(city));
                h.setProvince(city.getProvince());
                h.setCountry(city.getProvince().get().getCountry());
                ret.addAll(parseHtml(h, doc.getElementsByClass(cityHolydayClass)));
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
            Elements tagsMes = e.parent().parent().parent().getElementsByClass(this.monthClass);
            String mes = null;
            if (tagsMes.size() > 0)
                mes = tagsMes.get(0).text();
            if (dia != null && mes != null) {
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
            }
        }
        return ret;
    }


}
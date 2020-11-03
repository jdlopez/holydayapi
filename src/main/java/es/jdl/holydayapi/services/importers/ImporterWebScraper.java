package es.jdl.holydayapi.services.importers;

import es.jdl.holydayapi.domain.Holyday;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ImporterWebScraper implements EntityImporter<Holyday> {

    private final ConfigWebScraper conf;

    public ImporterWebScraper(ConfigWebScraper conf) {
        this.conf = conf;
    }

    @Override
    public List<Holyday> readFromSource() throws ImportException {
        List<Holyday> ret = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(conf.getUrl())
                    //.data("query", "Java")
                    .userAgent(conf.getAgentName())
                    //.cookie("auth", "token")
                    .timeout(conf.getTimeout())
                    .get();
            ret.addAll(parseHtml(HolydayType.LOCAL, doc.getElementsByClass(conf.getCityHolydayClass())));
            ret.addAll(parseHtml(HolydayType.REGION, doc.getElementsByClass(conf.getRegionHolydayClass())));

        } catch (IOException e) {
            throw new ImportException(e.getMessage(), e);
        }
        return ret;
    }

    private List<Holyday> parseHtml(HolydayType holydayType, Elements days) {
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", new Locale("ES"));
        List<Holyday> ret = new ArrayList<>();

        int year = conf.getPredefinedYear() == null? Calendar.getInstance().get(Calendar.YEAR): conf.getPredefinedYear();
        for (Element e: days) {
            String nombre = e.attr("title");
            String dia = e.getElementsByTag("a").text();
            if (dia == null || "".equals(dia))
                dia = e.text();
            Elements tagsMes = e.parent().parent().parent().getElementsByClass(conf.getMonthClass());
            String mes = null;
            if (tagsMes.size() > 0)
                mes = tagsMes.get(0).text();
            if (dia != null && !"".equals(dia) && mes != null && !"".equals(mes)) {
                Holyday h = new Holyday();
                h.setType(holydayType);
                h.setName(nombre);
                try {
                    h.setDay(df.parse(String.format("%s %s %d", dia, mes, year)).toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate());
                    ret.add(h);
                } catch (ParseException e1) {
                    System.out.println("Fecha incorrcta: " + dia + " " + mes + " " + year + ": " + e1.getMessage());
                }
            } else { // FIXME error handling
                System.out.println("Fecha incompleta: " + dia + " " + mes + " " + year + " " + nombre);
            }
        }
        return ret;
    }
}

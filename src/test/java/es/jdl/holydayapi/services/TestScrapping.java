package es.jdl.holydayapi.services;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestScrapping {

    public static void main(String[] args) throws IOException {

        String url = "https://calendarios.ideal.es/laboral/catalunya/barcelona/barcelona";
        Connection.Response response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
        if (response.statusCode() == 200) {
            Document doc = response.parse();
            // <td class="bm-calendar-state-autonomico" title="Lunes de Pascua">22</td>
            Elements fiestasLocales = doc.getElementsByClass("bm-calendar-state-local");
            for (Element e: fiestasLocales) {
                String nombre = e.attr("title");
                String dia = e.getElementsByTag("a").text();
                Elements tagsMes = e.parent().parent().parent().getElementsByClass("bm-calendar-month-title");
                String mes ="";
                if (tagsMes.size() > 0)
                    mes = tagsMes.get(0).text();

                System.out.printf("Fiesta: %s-%s-%s \n", dia, mes, "2019");
            }
        }
    }

    @Test
    public void parseDate() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", new Locale("ES"));
        String[] tests = {"22 diciembre 2011", "01 Enero 1966", "35 MARZO 99", "3 FEBRERO 2016"};
        for (String f: tests) {
            Date dia = df.parse(f);
            System.out.println(f + " = " + dia);
        }
    }
}

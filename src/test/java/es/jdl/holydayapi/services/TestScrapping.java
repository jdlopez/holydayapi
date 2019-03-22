package es.jdl.holydayapi.services;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import es.jdl.holydayapi.config.DbConfig;
import es.jdl.holydayapi.services.importers.ImportDataException;
import es.jdl.holydayapi.services.importers.ImporterCityScrapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestScrapping extends BaseObjectyfyTest {

    @Test
    public void testJsoup() throws IOException {

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

    @Before
    public void setUp() throws IOException {
        super.setUpBase();
    }

    @After
    public void tearDown() {
        super.tearDownBase();
    }

    @Test
    public void testImporterScrapping() {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                ImporterCityScrapper importerCityScrapper = new ImporterCityScrapper();
                MockHttpServletRequest mockReq = new MockHttpServletRequest();
                mockReq.addParam("city", "47186"); // 08019
                mockReq.addParam("uriSuffix", "castilla-y-leon/valladolid/valladolid"); // catalunya/barcelona/barcelona
                //mockReq.addParam("year", "2018");
                try {
                    importerCityScrapper.configure(mockReq, new DbConfig());
                    importerCityScrapper.readAndSave().forEach(holyday -> System.out.println(holyday));
                } catch (ImportDataException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void testManyScrappers() {

        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                String[] suffix = {"galicia/a-coruna/a-coruna", "galicia/a-coruna/naron", "pais-vasco/bizkaia/bilbao",
                "galicia/pontevedra/vigo", "asturias/asturias/oviedo", "asturias/asturias/gijon",
                "comunidad-valenciana/valencia/valencia", "comunidad-valenciana/alicante/alicante-alacant", "andalucia/almeria/almeria",
                "andalucia/malaga/malaga", "region-de-murcia/murcia/murcia", "aragon/zaragoza/zaragoza",
                "castilla-y-leon/valladolid/valladolid", "catalunya/barcelona/sabadell", "castilla-y-leon/avila/avila"};
                String[] codes  = {"15030", "15054", "48020",
                        "36057", "33044", "33024",
                        "46250", "03014", "04013",
                        "29067", "30030", "50297",
                        "47186", "08187", "05019"
                };
                for (int i = 0; i < suffix.length; i++) {
                    MockHttpServletRequest mockReq = new MockHttpServletRequest();
                    mockReq.addParam("city", codes[i]);
                    mockReq.addParam("uriSuffix", suffix[i]);
                    //mockReq.addParam("save", "false");
                    ImporterCityScrapper importerCityScrapper = new ImporterCityScrapper();
                    try {
                        importerCityScrapper.configure(mockReq, new DbConfig());
                        importerCityScrapper.readAndSave().forEach(holyday -> System.out.println(holyday));
                    } catch (ImportDataException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }
}

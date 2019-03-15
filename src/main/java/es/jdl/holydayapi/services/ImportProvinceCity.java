package es.jdl.holydayapi.services;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Create localization data from locale and APIs
 */
public class ImportProvinceCity {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    /** Create countries from java.util.locale */
    public List<Country> importCountries() {
        String[] countries = Locale.getISOCountries();
        List<Country> ret = new ArrayList<>(countries.length);
        log.info("countries: " + Arrays.toString(countries));
        for (String c: countries) {
            Locale countryLoc = new Locale("", c);
            Country country = new Country();
            country.setIso(c);
            country.setName(countryLoc.getDisplayName(Locale.ENGLISH)); // force english?
            country.setLocale(countryLoc);
            ObjectifyService.ofy().save().entity(country).now();
            ret.add(country);
        }
        return ret;
    }

    public Country getSpain() {
        return ObjectifyService.ofy().load().type(Country.class).id("ES").now();
    }

    public List<Province> importESProvinces() throws IOException, ParserConfigurationException, SAXException {
        Country es = getSpain();
        Ref<Country> esRef = Ref.create(es);
        List<Province> ret = new ArrayList<>();
        String url = "http://ovc.catastro.meh.es/ovcservweb/ovcswlocalizacionrc/ovccallejerocodigos.asmx/ConsultaProvincia";
        Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse((new URL(url)).openStream());
        NodeList provincias = dom.getElementsByTagName("prov");
        for (int i = 0; i < provincias.getLength(); i++) {
            NodeList codes = provincias.item(i).getChildNodes();
            if (codes.getLength() >= 2) {
                Province p = new Province();
                p.setCountry(esRef);
                for (int j = 0; j < codes.getLength(); j++) {
                    Node n = codes.item(j);
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        if ("cpine".equalsIgnoreCase(codes.item(j).getNodeName()))
                            p.setCode(codes.item(j).getTextContent());
                        else if ("np".equalsIgnoreCase(codes.item(j).getNodeName()))
                            p.setName(codes.item(j).getTextContent());
                        else
                            log.warning("Esto no deberia ocurrir: " + codes.item(j));
                    }
                } // end for j
                if (p.getCode() != null) {
                    ObjectifyService.ofy().save().entity(p).now();
                    ret.add(p);
                }
            } // endif hay 2 atributos
        } // for provincias
        return ret;
    }
}

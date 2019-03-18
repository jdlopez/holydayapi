package es.jdl.holydayapi.services;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    private String getChildValue(Element e, String tagName) {
        NodeList nl = e.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0)
            return nl.item(0).getTextContent();
        else
            return null;
    }

    public List<Province> importESProvinces() throws IOException, ParserConfigurationException, SAXException {
        Country es = getSpain();
        Ref<Country> esRef = Ref.create(es);
        List<Province> ret = new ArrayList<>();
        String url = "http://ovc.catastro.meh.es/ovcservweb/ovcswlocalizacionrc/ovccallejerocodigos.asmx/ConsultaProvincia";
        /*
        <prov>
        <cpine>33</cpine>
        <np>ASTURIAS</np>
        </prov>
         */
        Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse((new URL(url)).openStream());
        NodeList provincias = dom.getElementsByTagName("prov");
        for (int i = 0; i < provincias.getLength(); i++) {
            Element e = (Element) provincias.item(i);
            Province p = new Province();
            p.setCountry(esRef);
            p.setCode(getChildValue(e, "cpine"));
            p.setName(getChildValue(e, "np"));
            if (p.getCode() != null && p.getName() != null) {
                ObjectifyService.ofy().save().entity(p).now();
                ret.add(p);
            }
        } // for provincias
        return ret;
    }

    public List<City> importESCities() throws ParserConfigurationException, IOException, SAXException {
        ArrayList<City> ret = new ArrayList<>();
        List<Province> provincias = ObjectifyService.ofy().load().type(Province.class).list();
        log.info("Encontradas provincias: " + provincias.size());
        for (Province prov: provincias) {
            ret.addAll( importESCitiesByProvince(prov) );
        } // for provincias
        return ret;
    }

    public List<City> importESCitiesByProvince(Province prov) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<City> ret = new ArrayList<>();
        log.info("Buscando municipios con : " + prov.getCode());
        Map<String, Object> params = new HashMap<>(3);
        params.put("CodigoMunicipio", "");
        params.put("CodigoMunicipioIne", "");
        params.put("CodigoProvincia", prov.getCode());
        Ref<Province> provinceRef = Ref.create(prov);
        String url = "http://ovc.catastro.meh.es/ovcservweb/ovcswlocalizacionrc/ovccallejerocodigos.asmx/ConsultaMunicipioCodigos";
        String content = ServicesUtils.getPostContent(url, params);
        Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(content.getBytes()));
            /*
            <muni>
                <nm>LA ACEBEDA</nm>
                <locat>
                    <cd>28</cd>
                    <cmc>1</cmc>
                </locat>
                <loine>
                    <cp>28</cp>
                    <cm>1</cm>
                </loine>
            </muni>
             */
        NodeList munis = dom.getElementsByTagName("muni");
        log.info("Localizados " + munis.getLength() + " munis");
        for (int i = 0; i < munis.getLength(); ++i) {
            Element e = (Element) munis.item(i);
            City city = new City();
            city.setProvince(provinceRef);
            NodeList nlINE = e.getElementsByTagName("loine");
            if (nlINE != null && nlINE.getLength() > 0) {
                Element nINE = (Element) nlINE.item(0);
                city.setName(getChildValue(e, "nm"));
                city.setCode(prov.getCode() + String.format("%03d", Integer.parseInt(getChildValue(nINE, "cm"))));
                if (city.getCode() != null && city.getName() != null) {
                    ObjectifyService.ofy().save().entity(city).now();
                    ret.add(city);
                    log.info("Guardado: " + city);
                } else {
                    log.warning("No encontrado datos para " + e);
                }
            }
        } // for municipios
        return ret;
    }

}

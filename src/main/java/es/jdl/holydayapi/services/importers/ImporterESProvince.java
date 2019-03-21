package es.jdl.holydayapi.services.importers;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import es.jdl.holydayapi.config.DbConfig;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;
import es.jdl.holydayapi.services.ServicesUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static es.jdl.holydayapi.services.ServicesUtils.getChildValue;

/**
 * Importador de provincias españolas usando API de catastro
 * @author jdlopez
 */
public class ImporterESProvince implements EntityImporter<Province> {

    private Country defaultCountry;
    private String provinceESurl;

    @Override
    public void configure(HttpServletRequest request, DbConfig config) throws ImportDataException {
        provinceESurl = "http://ovc.catastro.meh.es/ovcservweb/ovcswlocalizacionrc/ovccallejerocodigos.asmx/ConsultaProvincia";
        if (config != null) {
            provinceESurl = config.getProperty("catastro_provincia", provinceESurl);
            // defaultCountry
        }
    }

    @Override
    public List<Province> readAndSave() throws ImportDataException {
        this.defaultCountry = ObjectifyService.ofy().load().type(Country.class).id("ES").now();
        Ref<Country> esRef = Ref.create(defaultCountry);
        List<Province> ret = new ArrayList<>();
        /*
        <prov>
        <cpine>33</cpine>
        <np>ASTURIAS</np>
        </prov>
         */
        Document dom = null;
        try {
            dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new ByteArrayInputStream(ServicesUtils.getURLContent(this.provinceESurl).getBytes()));
        } catch (SAXException |IOException | ParserConfigurationException e) {
            throw new ImportDataException(e.getMessage(), e);
        }
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
}

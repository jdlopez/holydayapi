package es.jdl.holydayapi.services.importers;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import es.jdl.holydayapi.config.DbConfig;
import es.jdl.holydayapi.domain.City;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static es.jdl.holydayapi.services.ServicesUtils.getChildValue;

/**
 * Importador de municipios españoles usando servicio de Catastro. Los datos son del INE
 * @author jdlopez
 */
public class ImporterESCity implements EntityImporter<City> {

    private Province provincia; // si se rellena se importa solo una provincia, en otro caso TODAS

    private final Logger log = Logger.getLogger(this.getClass().getName());

    private List<City> importESCitiesByProvince(Province prov) throws ImportDataException {
        ArrayList<City> ret = new ArrayList<>();
        log.info("Buscando municipios con : " + prov.getCode());
        Map<String, Object> params = new HashMap<>(3);
        params.put("CodigoMunicipio", "");
        params.put("CodigoMunicipioIne", "");
        params.put("CodigoProvincia", prov.getCode());
        Ref<Province> provinceRef = Ref.create(prov);
        String url = "http://ovc.catastro.meh.defaultCountry/ovcservweb/ovcswlocalizacionrc/ovccallejerocodigos.asmx/ConsultaMunicipioCodigos";
        String content = null;
        try {
            content = ServicesUtils.getPostContent(url, params);
            Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new ByteArrayInputStream(content.getBytes()));
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
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new ImportDataException(e.getMessage(), e);
        }
        return ret;
    }

    @Override
    public void configure(HttpServletRequest request, DbConfig config) {
        String provinceCode = null;
        if (request != null)
            request.getParameter("prov");
        if (provinceCode != null) {
            this.provincia = ObjectifyService.ofy().load().type(Province.class).id(provinceCode).now();
        }
    }

    @Override
    public List<City> readAndSave() throws ImportDataException {
        List<City> ret = new ArrayList<>();
        if (this.provincia == null) { // importamos todas
            log.info("IMPORTACION SIN FILTRO. PUEDE TARDAR");
            List<Province> provincias = ObjectifyService.ofy().load().type(Province.class).list();
            log.info("Encontradas provincias: " + provincias.size());
            for (Province prov: provincias) {
                ret.addAll( importESCitiesByProvince(prov) );
            } // for provincias
        } else {
            ret = importESCitiesByProvince(provincia);
        }
        return ret;
    }
}

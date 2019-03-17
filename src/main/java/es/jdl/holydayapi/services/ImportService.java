package es.jdl.holydayapi.services;

import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ImportService extends HttpServlet {

    private ImportProvinceCity importer = new ImportProvinceCity();
    private ImportFromMadridOpenData importerMadrid = new ImportFromMadridOpenData();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String entity = req.getParameter("ent");
        try {
            if ("country".equalsIgnoreCase(entity))
                ServicesUtils.writeJSONResponse(resp, importer.importCountries());
            else if ("province".equalsIgnoreCase(entity))
                ServicesUtils.writeJSONResponse(resp, importer.importESProvinces());
            else if ("city".equalsIgnoreCase(entity))
                ServicesUtils.writeJSONResponse(resp, importer.importESCities());
            else if ("madrid".equalsIgnoreCase(entity)) {
                importerMadrid.importHolydays();
                ServicesUtils.writeJSONResponse(resp, "OK");
            }
        } catch (ParserConfigurationException | SAXException e) {
            log(e.getMessage(), e);
        }
    }
}

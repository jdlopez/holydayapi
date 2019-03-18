package es.jdl.holydayapi.services;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.ObjectifyService;
import es.jdl.holydayapi.domain.Province;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Servicio REST de pega para lanzar cargas o importaciones (se puede usar como cron una vez al año)
 */
public class ImportService extends HttpServlet {

    private ImportProvinceCity importer = new ImportProvinceCity();
    private ImportFromMadridOpenData importerMadrid = new ImportFromMadridOpenData();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String entity = req.getParameter("ent");
        try {
            if ("country".equalsIgnoreCase(entity))
                ServicesUtils.writeJSONResponse(resp, importer.importCountries());
            else if ("province".equalsIgnoreCase(entity)) {
                ServicesUtils.writeJSONResponse(resp, importer.importESProvinces());
            } else if ("city".equalsIgnoreCase(entity)) {
                // opciones:
                // https://cloud.google.com/appengine/docs/standard/java/taskqueue/push/creating-tasks#using_the_instead_of_a_worker_service
                String province = req.getParameter("prov");
                String salida = null;
                if (province == null) {
                    salida = "NEED PROVINCE (prov)";
                    //cities = importer.importESCities();
                } else {
                    Queue queue = QueueFactory.getDefaultQueue();
                    Province prov = ObjectifyService.ofy().load().type(Province.class).id(province).now();
                    queue.add(
                            TaskOptions.Builder.withPayload(new ImportCityDeferred(prov)));

                }
                ServicesUtils.writeJSONResponse(resp, salida);

            } else if ("madrid".equalsIgnoreCase(entity)) {
                importerMadrid.importHolydays();
                ServicesUtils.writeJSONResponse(resp, "OK");
            }
        } catch (ParserConfigurationException | SAXException e) {
            log(e.getMessage(), e);
        }
    }
}

package es.jdl.holydayapi.services.rest;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import es.jdl.holydayapi.config.DbConfig;
import es.jdl.holydayapi.services.ImportDeferred;
import es.jdl.holydayapi.services.SharedDataDao;
import es.jdl.holydayapi.services.importers.ImportDataException;
import es.jdl.holydayapi.services.importers.ImporterCityScrapper;
import es.jdl.holydayapi.services.importers.ImporterCountry;
import es.jdl.holydayapi.services.importers.ImporterESCity;
import es.jdl.holydayapi.services.importers.ImporterESProvince;
import es.jdl.holydayapi.services.importers.ImporterMadridOpenData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static es.jdl.holydayapi.services.ServicesUtils.writeJSONResponse;

/**
 * Servicio REST de pega para lanzar cargas o importaciones (se puede usar como cron una vez al año)
 */
public class ImportRESTService extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String entity = req.getParameter("ent");
        try {
            if ("country".equalsIgnoreCase(entity))
                writeJSONResponse(resp, new ImporterCountry().readAndSave());
            else if ("province".equalsIgnoreCase(entity)) {
                ImporterESProvince imp = new ImporterESProvince();
                imp.configure(req, null);
                writeJSONResponse(resp, imp.readAndSave());
            } else if ("city".equalsIgnoreCase(entity)) {
                // opciones:
                // https://cloud.google.com/appengine/docs/standard/java/taskqueue/push/creating-tasks#using_the_instead_of_a_worker_service
                ImporterESCity imp = new ImporterESCity();
                imp.configure(req, new DbConfig());

                Queue queue = QueueFactory.getDefaultQueue();
                queue.add( TaskOptions.Builder.withPayload(new ImportDeferred(imp)) );

                writeJSONResponse(resp, "Enqueued");
            } else if ("madrid".equalsIgnoreCase(entity)) {
                writeJSONResponse(resp, new ImporterMadridOpenData().readAndSave());
            } else if ("holydays".equalsIgnoreCase(entity)) {
                ImporterCityScrapper imp = new ImporterCityScrapper();
                imp.configure(req, new DbConfig());
                /* deferred??
                imp.configure(req, new DbConfig());
                Queue queue = QueueFactory.getDefaultQueue();
                queue.add( TaskOptions.Builder.withPayload(new ImportDeferred(imp)) );
                 */
                writeJSONResponse(resp, imp.readAndSave());
            }
        } catch (ImportDataException e) {
            log(e.getMessage(), e);
        }
    }
}

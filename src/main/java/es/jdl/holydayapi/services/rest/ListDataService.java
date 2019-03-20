package es.jdl.holydayapi.services.rest;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import es.jdl.holydayapi.services.ServicesUtils;
import static es.jdl.holydayapi.services.ServicesUtils.writeJSONResponse;
import es.jdl.holydayapi.services.SharedDataDao;

/**
 * REST Service (servlet) list datasotore entities: country, province, city (filtered by prov)
 * @author jdlopez
 */
public class ListDataService extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String entity = uri.substring(uri.lastIndexOf('/') + 1);
        if ("country".equalsIgnoreCase(entity))
            writeJSONResponse(resp,
                ObjectifyService.ofy().load().type(Country.class).list()
            );
        else if ("province".equalsIgnoreCase(entity)) {
            writeJSONResponse(resp,
                    ObjectifyService.ofy().load().type(Province.class).list()
            );
        } else if ("city".equalsIgnoreCase(entity)) {
            String provCode = req.getParameter("prov");
            Province prov = ObjectifyService.ofy().load().type(Province.class).id(provCode).now();
            if (prov != null) {
                Query<City> query = ObjectifyService.ofy().load().type(City.class);
                writeJSONResponse(resp,
                    query.filter("province", prov).list()
                );
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Province " + provCode + " not found");
            }
        } else {
            // maybe invalid input? 400?
            writeJSONResponse(resp, null);
        }

    }
}

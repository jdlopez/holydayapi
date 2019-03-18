package es.jdl.holydayapi.services;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import es.jdl.holydayapi.domain.City;
import es.jdl.holydayapi.domain.Country;
import es.jdl.holydayapi.domain.Province;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author jdlopez
 */
public class ListDataService extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String entity = uri.substring(uri.lastIndexOf('/') + 1);
        if ("country".equalsIgnoreCase(entity))
            ServicesUtils.writeJSONResponse(resp,
                ObjectifyService.ofy().load().type(Country.class).list()
            );
        else if ("province".equalsIgnoreCase(entity)) {
            ServicesUtils.writeJSONResponse(resp,
                    ObjectifyService.ofy().load().type(Province.class).list()
            );
        } else if ("city".equalsIgnoreCase(entity)) {
            String province = req.getParameter("prov");
            Query<City> query = ObjectifyService.ofy().load().type(City.class);
            ServicesUtils.writeJSONResponse(resp,
                query.filter("province", province).list()
            );
        } else {
            // maybe invalid input? 400?
            ServicesUtils.writeJSONResponse(resp, null);
        }

    }
}

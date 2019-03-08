package es.jdl.holydayapi.services;

import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;
import es.jdl.holydayapi.domain.Holyday;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class HolydayService extends HttpServlet {

    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] uri = req.getRequestURI().split("/");
        super.doGet(req, resp);
    }

    protected List<Holyday> findHolydays() {
        //ObjectifyService.ofy().load().
        return null;
    }
}

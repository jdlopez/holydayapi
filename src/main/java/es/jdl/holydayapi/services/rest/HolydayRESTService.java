package es.jdl.holydayapi.services.rest;

import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.services.ServicesUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HolydayRESTService extends HttpServlet {

    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String year = req.getParameter("year");
        Calendar cal = Calendar.getInstance();
        int actualYear = cal.get(Calendar.YEAR);
        cal.setTimeInMillis(0); // hora a 0
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        if (year != null) {
            cal.set(Calendar.YEAR, Integer.parseInt(year));
        } else {
            cal.set(Calendar.YEAR, actualYear);
        }
        ServicesUtils.writeJSONResponse(resp, findHolydays(cal.getTime()));
    }

    protected List<Holyday> findHolydays(Date since) {
        Query query = ObjectifyService.ofy().load().type(Holyday.class);
        if (since != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(since);
            cal.add(Calendar.YEAR, 1);
            Date nextYear = cal.getTime();
            query = query.filter("date >=", since).filter("date <", nextYear);
        }
        return query.list();
    }
}

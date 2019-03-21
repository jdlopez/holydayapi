package es.jdl.holydayapi.services.rest;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.services.ServicesUtils;
import es.jdl.holydayapi.services.SharedDataDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HolydayRESTService extends HttpServlet {

    private SharedDataDao dao = new SharedDataDao();

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
        ServicesUtils.writeJSONResponse(resp,
                findHolydays(cal.getTime(), req.getParameter("country"),
                        req.getParameter("province"), req.getParameter("city")));
    }

    protected List<Holyday> findHolydays(Date since, String country, String province, String city) {
        Query query = ObjectifyService.ofy().load().type(Holyday.class);
        if (since != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(since);
            cal.add(Calendar.YEAR, 1);
            Date nextYear = cal.getTime();
            query = query.filter("date >=", since).filter("date <", nextYear);
        }
        List<Holyday> ret = query.list();
        /*
        Key<Country> countryKey = null;
        Key<Province> provKey = null;
        Key<City> cityKey = null;
        Key<Province> provCityKey = null;
        if (country != null)
            countryKey = Key.create(country);
        if (province != null)
            provKey = Key.create(province);
        if (city != null)
            cityKey = Key.create(city);
        if (city != null)
            provCityKey = Key.create(city.substring(0, 2));
        for (Holyday h: ret) {
            if (country != null && !h.getCountry().equivalent(countryKey))
                ret.remove(h);
            else if (province != null &&
                    (!h.getProvince().equivalent(provKey) || !h.getProvince().equivalent(provCityKey)))
                ret.remove(h);
            else if (city != null && !h.getCity().equivalent(cityKey))
                ret.remove(h);
        }
        */
        return ret;
    }
}

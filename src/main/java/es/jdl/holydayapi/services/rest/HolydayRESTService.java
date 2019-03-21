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
        for (int i = ret.size() - 1; i > 0; i--) { // preorden para no afectar a los indices al borrar
            Holyday h = ret.get(i);
            if (country != null && h.getCountry() != null && !h.getCountry().get().getIso().equalsIgnoreCase(country))
                ret.remove(h);
            else if (province != null) { // x provincia
                String hProv = h.getProvince() != null?h.getProvince().get().getCode():province;
                // si h tiene prov tb tiene city
                if (!province.equalsIgnoreCase(hProv))
                    ret.remove(h);
            } else if (city != null) {
                String cityProv = city.substring(0, 2);
                String hCity = h.getCity() != null?h.getCity().get().getCode():city;
                String hProv = h.getProvince() != null?h.getProvince().get().getCode():cityProv;
                if (!city.equalsIgnoreCase(hCity) || !cityProv.equalsIgnoreCase(hProv))
                    ret.remove(h);
            }
        }
        return ret;
    }
}

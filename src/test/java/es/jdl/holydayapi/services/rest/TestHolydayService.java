package es.jdl.holydayapi.services.rest;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import es.jdl.holydayapi.domain.Holyday;
import es.jdl.holydayapi.services.BaseObjectyfyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class TestHolydayService extends BaseObjectyfyTest {

    @Before
    public void setUp() throws IOException {
        super.setUpBase();
    }

    @After
    public void tearDown() {
        super.tearDownBase();
    }

    @Test
    public void testQueryHolydays() {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                HolydayRESTService rest = new HolydayRESTService();
                Calendar cal = Calendar.getInstance();
                int actualYear = cal.get(Calendar.YEAR);
                cal.setTimeInMillis(0); // hora a 0
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.YEAR, actualYear);
                List<Holyday> holydays = rest.findHolydays(cal.getTime(), "ES", null, null);
                System.out.println(">>>>>>>>>>>>>>>holydays in ES> " + holydays.size());
                //holydays.forEach(h -> System.out.println(h));
                holydays = rest.findHolydays(cal.getTime(), "ES", "28", null);
                System.out.println(">>>>>>>>>>>>>>>holydays in ES province:Madrid>" + holydays.size());
                //holydays.forEach(h -> System.out.println(h));
                holydays = rest.findHolydays(cal.getTime(), "ES", null, "28079");
                System.out.println(">>>>>>>>>>>>>>>holydays in ES city:Madrid>" + holydays.size());
                //holydays.forEach(h -> System.out.println(h));
                holydays = rest.findHolydays(cal.getTime(), "ES", "08", null);
                System.out.println(">>>>>>>>>>>>>>>holydays in ES bcn> " + holydays.size());
            }
        });
    }
}


package ec.edu.uce.titulacion.servicio;

import ec.edu.uce.titulacion.dao.PlanDaoImpl;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

@Singleton
public class ServicioEmail implements ServicioEmailLocal {

    //@EJB
    private PlanDaoImpl plan= new PlanDaoImpl();

    //@Schedule(second = "0", minute = "0", hour = "8")
    //@Schedule(second = "0", minute = "*/5", hour = "*")
    public void BarrerPlan() throws Exception{
        System.out.println("empieza el servicio");
        plan.BarrerPlan();
    }
}

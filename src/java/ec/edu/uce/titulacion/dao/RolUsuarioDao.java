package ec.edu.uce.titulacion.dao;

import ec.edu.uce.titulacion.entidades.Plan;
import ec.edu.uce.titulacion.entidades.RolUsuario;
import java.util.List;
import javax.ejb.Local;

@Local
public interface RolUsuarioDao {

    public List<RolUsuario> listarIntegrantesByPlan(Plan plan) throws Exception;
    
}

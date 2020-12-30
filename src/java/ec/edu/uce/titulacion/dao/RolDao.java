package ec.edu.uce.titulacion.dao;

import ec.edu.uce.titulacion.entidades.Rol;
import ec.edu.uce.titulacion.entidades.Usuario;
import java.util.List;
import javax.ejb.Local;

@Local
public interface RolDao {
    List<Rol> buscarRolByUser(Usuario user) throws Exception;
}

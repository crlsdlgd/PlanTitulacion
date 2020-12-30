package ec.edu.uce.titulacion.dao;

import ec.edu.uce.titulacion.entidades.Plan;
import ec.edu.uce.titulacion.entidades.PlanUsuario;
import ec.edu.uce.titulacion.entidades.Rol;
import ec.edu.uce.titulacion.entidades.RolUsuario;
import ec.edu.uce.titulacion.entidades.Usuario;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;

@Stateless
public class RolUsuarioDaoImpl extends DAO implements RolUsuarioDao {

    @Override
    public List<RolUsuario> listarIntegrantesByPlan(Plan plan) throws Exception {
        List<RolUsuario> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT u.id_usuario, u.nombre, u.email, pu.postulado, r.id_rol, r.rol\n"
                    + "FROM usuario u, plan_usuario pu, plan p, rol r, rol_usuario ru\n"
                    + "WHERE pu.id_usuario=u.id_usuario  AND\n"
                    + "pu.id_plan=p.id_plan AND\n"
                    + "ru.id_usuario=u.id_usuario  AND\n"
                    + "ru.id_rol=r.id_rol AND\n"
                    + "p.aprobado='FALSE' AND\n"
                    + "pu.postulado='TRUE' AND\n"
                    + "p.id_plan=?");
            st.setInt(1, plan.getIdPlan());
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Usuario user = new Usuario();
                Rol rol = new Rol();
                RolUsuario rolUser = new RolUsuario();
                user.setIdUsuario(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("nombre"));
                user.setEmail(rs.getString("email"));
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setRol(rs.getString("rol"));
                rolUser.setUsuario(user);
                rolUser.setRol(rol);
                lista.add(rolUser);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;
    }

}

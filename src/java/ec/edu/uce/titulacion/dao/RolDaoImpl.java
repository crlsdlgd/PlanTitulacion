package ec.edu.uce.titulacion.dao;

import ec.edu.uce.titulacion.entidades.Rol;
import ec.edu.uce.titulacion.entidades.Usuario;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;

@Stateless
public class RolDaoImpl extends DAO implements RolDao {

    @Override
    public List<Rol> buscarRolByUser(Usuario user) throws Exception {
        List<Rol> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT r.id_rol, r.rol\n"
                    + "FROM rol r, usuario u, rol_usuario ru\n"
                    + "WHERE ru.id_usuario = u.id_usuario AND\n"
                    + "r.id_rol= ru.id_rol AND u.id_usuario= ? ");

            st.setInt(1, user.getIdUsuario());
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setRol(rs.getString("rol"));
   
                lista.add(rol);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;
    }

}

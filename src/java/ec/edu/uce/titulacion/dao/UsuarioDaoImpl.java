package ec.edu.uce.titulacion.dao;

import ec.edu.uce.titulacion.entidades.Plan;
import ec.edu.uce.titulacion.entidades.Usuario;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;

@Stateless
public class UsuarioDaoImpl extends DAO implements UsuarioDao {

    @Override
    public void registrar(Usuario usuario) throws Exception {
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareStatement("INSERT INTO usuario (nombre, email, password, nick) VALUES(?,?,?,?)");
            st.setString(1, usuario.getNombre());
            st.setString(2, usuario.getEmail());
            st.setString(3, usuario.getPassword());
            st.setString(4, usuario.getNick());
            st.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
    }

    @Override
    public List<Usuario> listarUsuario() throws Exception {
        List<Usuario> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT id_usuario, nombre, email, nick FROM usuario");
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));
                usuario.setNick(rs.getString("nick"));
                lista.add(usuario);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;
    }

    @Override
    public List<Usuario> listarUserByPlan(Plan plan) throws Exception {
        List<Usuario> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT u.id_usuario, u.nombre, u.email, u.nick\n"
                    + "FROM plan p, usuario u, plan_usuario pu\n"
                    + "WHERE pu.id_usuario = u.id_usuario AND\n"
                    + " p.id_plan= pu.id_plan AND p.id_plan=?");

            st.setInt(1, plan.getIdPlan());
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));
                usuario.setNick(rs.getString("nick"));
                lista.add(usuario);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;
    }

    @Override
    public Usuario buscarUsuarioLogin(String nick, String pass) throws Exception {
        Usuario usuario;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT id_usuario, nombre, email, nick FROM usuario WHERE nick = ? AND password = ? ");
            st.setString(1, nick);
            st.setString(2, pass);
            rs = st.executeQuery();
            usuario = new Usuario();

            while (rs.next()) {
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));
                usuario.setNick(rs.getString("nick"));
            }

            if (usuario.getIdUsuario() == null) {
                return null;
            }

        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return usuario;

    }

    @Override
    public List<String> autoCompletarEstudiante(String query) throws Exception {
        List<String> lista;
        ResultSet rs;
        query = "%" + query + "%";
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT u.nombre\n"
                    + "FROM usuario u, rol r, rol_usuario ru\n"
                    + "WHERE UPPER(u.nombre) LIKE UPPER( ? ) AND\n"
                    + "u.id_usuario=ru.id_usuario AND\n"
                    + "ru.id_rol = r.id_rol AND\n"
                    + "r.rol='Estudiante'");
            st.setString(1, query);
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                String aux = rs.getString("nombre");
                lista.add(aux);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;

    }

    @Override
    public boolean existeEstudiante(String txtEstudiante) throws Exception {
        ResultSet rs;
        List<Usuario> lista;
        boolean flag = true;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT u.id_usuario FROM usuario u, rol_usuario ru, rol r\n"
                    + "WHERE ru.id_usuario=u.id_usuario AND\n"
                    + "ru.id_rol = r.id_rol AND\n"
                    + "r.rol = 'Estudiante' AND\n"
                    + "u.nombre= ? ");
            st.setString(1, txtEstudiante);
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                lista.add(u);
            }
            if (lista.isEmpty()) {
                flag = false;
            }
        } catch (Exception e) {
            return false;

        } finally {
            this.Cerrar();
        }
        return flag;
    }

    @Override
    public Usuario buscarUsuarioPrecursor(Plan plan) throws Exception {
        Usuario user = new Usuario();
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT id_usuario, nombre FROM usuario WHERE id_usuario= ?");
            st.setInt(1, plan.getPropuestoPor());
            rs = st.executeQuery();
            while (rs.next()) {
                user.setIdUsuario(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("nombre"));
            }
        } catch (Exception e) {
            throw e;
        }finally{
            this.Cerrar();
        }
        return user;
        
    }

}

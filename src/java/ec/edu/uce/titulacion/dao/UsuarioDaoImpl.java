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
    public boolean registrar(Usuario usuario) throws Exception {
        boolean cedulaCorrecta = comprobarCedula(usuario.getCedula());
        boolean correoCorrecto = comprobarCorreoInstitucional(usuario.getEmail());
        String nick = sacarNick(usuario.getEmail());
        System.out.println("Cedula correcta: "+cedulaCorrecta+" Correo Correcto: "+correoCorrecto+" Nick: "+nick);
        boolean flag=false;
        if (cedulaCorrecta && correoCorrecto) {
            try {
                this.Conectar();
                this.getCn().setAutoCommit(false);
                System.out.println("Antes del primer insert 1");
                PreparedStatement st = this.getCn().prepareStatement("INSERT INTO usuario (nombre, email, password, nick, cedula) VALUES(?,?,?,?,?)");
                st.setString(1, usuario.getNombre());
                st.setString(2, usuario.getEmail());
                st.setString(3, usuario.getPassword());
                st.setString(4, nick);
                st.setString(5, usuario.getCedula());
                System.out.println("Antes del primer insert 2");
                st.executeUpdate();
                System.out.println("Despues del primer insert");
                st.close();
                
                System.out.println("hizo insert usuario y cerro conexion");
                
                PreparedStatement st2 = this.getCn().prepareStatement("SELECT MAX(id_usuario) AS id_usuario FROM usuario");
                ResultSet rs;
                int idUsuario = 0;
                rs = st2.executeQuery();
                while (rs.next()) {
                    idUsuario = rs.getInt("id_usuario");
                }
                rs.close();
                st2.close();
                
                System.out.println("consulto id de usaurio");
                
                PreparedStatement st3 = this.getCn().prepareStatement("INSERT INTO rol_usuario (id_rol,id_usuario) VALUES(1,?)");
                st3.setInt(1, idUsuario);
                st3.executeUpdate();
                st3.close();
                this.getCn().commit();
                flag=true;
                
                System.out.println("Inserto el rol del usuario");
                
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("entro al rollback");
                this.getCn().rollback();
                return false;
         
            } finally {
                this.Cerrar();
            }
        }
        return flag;
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
        } finally {
            this.Cerrar();
        }
        return user;

    }

    private boolean comprobarCedula(String cedula) {
        System.out.println("Cedula: "+cedula);
        boolean flag = false;
        int cont = 0;
        if (cedula.length() == 10) {
            for (int i = 0; i < cedula.length(); i++) {
                if ((int) cedula.charAt(i) < 48 && (int) cedula.charAt(i) > 57) {
                    return false;
                }
            }

            int[] matriz = new int[10];
            matriz[0] = Integer.parseInt(cedula.charAt(0) + "");
            matriz[1] = Integer.parseInt(cedula.charAt(1) + "");
            matriz[2] = Integer.parseInt(cedula.charAt(2) + "");
            matriz[3] = Integer.parseInt(cedula.charAt(3) + "");
            matriz[4] = Integer.parseInt(cedula.charAt(4) + "");
            matriz[5] = Integer.parseInt(cedula.charAt(5) + "");
            matriz[6] = Integer.parseInt(cedula.charAt(6) + "");
            matriz[7] = Integer.parseInt(cedula.charAt(7) + "");
            matriz[8] = Integer.parseInt(cedula.charAt(8) + "");
            matriz[9] = Integer.parseInt(cedula.charAt(9) + "");

            cont = (2 * (matriz[1] + matriz[3] + matriz[5] + matriz[7]))
                    + matriz[0] + matriz[2] + matriz[4] + matriz[6] + matriz[8];

            while (cont >= 10) {
                cont -= 9;
            }
            if (cont == matriz[9]) {
                flag = true;
            }
        }
        else if(cedula.length()<10){
            System.out.println("cedula con menos de 10 digitos");
        }
          else  {
            System.out.println("cedula con mas de 10 digitos");
        }
        return flag;
    }

    private boolean comprobarCorreoInstitucional(String correo) {
        System.out.println("Correo: "+correo);
        String aux = "";
        boolean flag = false;
        if (correo.length() > 11) {
            for (int i = correo.length() - 11; i < correo.length(); i++) {
                aux = aux + correo.charAt(i);
            }
        }
        else{
            System.out.println("correo con menos de 11 caracteres");
        }
        if (aux.equals("@uce.edu.ec")) {
            flag = true;
        }
        return flag;
    }

    private String sacarNick(String correo) {
        String aux = "";
        if (correo.length() > 11) {
            for (int i = 0; i < correo.length() - 11; i++) {
                aux = aux + correo.charAt(i);
            }
        }
        return aux;
    }

}

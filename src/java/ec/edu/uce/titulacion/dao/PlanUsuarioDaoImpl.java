package ec.edu.uce.titulacion.dao;

import ec.edu.uce.titulacion.entidades.Plan;
import ec.edu.uce.titulacion.entidades.PlanUsuario;
import ec.edu.uce.titulacion.entidades.Usuario;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;

@Stateless
public class PlanUsuarioDaoImpl extends DAO implements PlanUsuarioDao {

    @Override
    public List<PlanUsuario> listarPostulantesByPlan(Plan plan) throws Exception {
        List<PlanUsuario> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT u.id_usuario, u.nombre, u.email, pu.postulado \n"
                    + "FROM usuario u, plan_usuario pu, plan p\n"
                    + "WHERE pu.id_usuario=u.id_usuario AND\n"
                    + "pu.id_plan=p.id_plan AND\n"
                    + "p.aprobado='FALSE' AND\n"
                    + "pu.id_plan= ? ");
            st.setInt(1, plan.getIdPlan());
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Usuario user = new Usuario();
                PlanUsuario planUser = new PlanUsuario();
                user.setIdUsuario(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("nombre"));
                user.setEmail(rs.getString("email"));
                planUser.setPlan(plan);
                planUser.setUsuario(user);
                planUser.setPostulado(rs.getBoolean("postulado"));
                lista.add(planUser);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;

    }

    @Override
    public List<PlanUsuario> listarIntegrantesByPlan(Plan plan) throws Exception {
        List<PlanUsuario> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT u.id_usuario, u.nombre, u.email, pu.postulado \n"
                    + "FROM usuario u, plan_usuario pu, plan p\n"
                    + "WHERE pu.id_usuario=u.id_usuario  AND\n"
                    + "pu.id_plan=p.id_plan AND\n"
                    + "p.aprobado='TRUE' AND\n"
                    + "pu.postulado='TRUE' AND\n"
                    + "p.id_plan= ?");
            st.setInt(1, plan.getIdPlan());
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Usuario user = new Usuario();
                PlanUsuario planUser = new PlanUsuario();
                user.setIdUsuario(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("nombre"));
                user.setEmail(rs.getString("email"));
                planUser.setPlan(plan);
                planUser.setUsuario(user);
                planUser.setPostulado(rs.getBoolean("postulado"));
                lista.add(planUser);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;

    }

    @Override
    public void guardarProyecto(List<PlanUsuario> listaPostulantes) throws Exception {

        try {
            this.Conectar();
            this.getCn().setAutoCommit(false);
            PreparedStatement st = this.getCn().prepareStatement("UPDATE plan_usuario\n"
                    + "SET postulado = ?\n"
                    + "WHERE id_plan=? AND id_usuario=?;");
            for (int i = 0; i < listaPostulantes.size(); i++) {
                st.setBoolean(1, listaPostulantes.get(i).isPostulado());
                st.setInt(2, listaPostulantes.get(i).getPlan().getIdPlan());
                st.setInt(3, listaPostulantes.get(i).getUsuario().getIdUsuario());
                st.executeUpdate();
            }
            st.close();

            this.getCn().commit();
        } catch (Exception e) {
            this.getCn().rollback();
            throw e;
        } finally {
            this.Cerrar();
        }

    }

}

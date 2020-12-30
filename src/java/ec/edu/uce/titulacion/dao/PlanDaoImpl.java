package ec.edu.uce.titulacion.dao;

import ec.edu.uce.titulacion.entidades.Plan;
import ec.edu.uce.titulacion.entidades.Usuario;
import ec.edu.uce.titulacion.utilities.SendMailGmail;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.Stateless;

@Stateless
public class PlanDaoImpl extends DAO implements PlanDao {

    @Override
    public void registrar(Plan plan) throws Exception {
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareStatement("INSERT INTO plan (tema,fecha) VALUES(?,?)");
            st.setString(1, plan.getTema());
            st.setDate(2, (Date) plan.getFecha());
            st.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
    }

    @Override
    public List<Plan> listarPlan() throws Exception {
        List<Plan> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT id_plan, tema, fecha FROM plan ");
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Plan plan = new Plan();
                plan.setIdPlan(rs.getInt("id_plan"));
                plan.setTema(rs.getString("tema"));
                plan.setFecha(rs.getDate("fecha"));
                lista.add(plan);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;
    }

    @Override
    public List<Plan> listarPlanesAprobados() throws Exception {
        List<Plan> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT id_plan, tema, fecha FROM plan WHERE aprobado = 'TRUE' ");
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Plan plan = new Plan();
                plan.setIdPlan(rs.getInt("id_plan"));
                plan.setTema(rs.getString("tema"));
                plan.setFecha(rs.getDate("fecha"));
                lista.add(plan);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;
    }

    @Override
    public List<Plan> listarPlanesNoAprobadosNiPostulados(Usuario user) throws Exception {
        List<Plan> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT p.id_plan, p.tema, p.detalle, p.propuesto_por \n"
                    + "FROM plan p, plan_usuario pu\n"
                    + "WHERE p.id_plan=pu.id_plan AND\n"
                    + "p.aprobado='FALSE' AND\n"
                    + "pu.postulado='FALSE' AND\n"
                    + "pu.id_usuario!= ? AND\n"
                    + "p.propuesto_por!= ?\n"
                    + "EXCEPT (SELECT p.id_plan, p.tema, p.detalle, p.propuesto_por \n"
                    + "FROM plan p, plan_usuario pu\n"
                    + "WHERE pu.id_plan=p.id_plan AND\n"
                    + "pu.id_usuario =?)");
            st.setInt(1, user.getIdUsuario());
            st.setInt(2, user.getIdUsuario());
            st.setInt(3, user.getIdUsuario());
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Plan plan = new Plan();
                plan.setIdPlan(rs.getInt("id_plan"));
                plan.setTema(rs.getString("tema"));
                plan.setDetalle(rs.getString("detalle"));
                plan.setPropuestoPor(rs.getInt("propuesto_por"));
                lista.add(plan);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;
    }

    public Plan findPlanById(Integer idPlan) throws Exception {
        Plan plan = new Plan();
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT id_plan, tema, fecha FROM plan WHERE id_plan = ? ");
            st.setInt(1, idPlan);
            rs = st.executeQuery();
            while (rs.next()) {
                plan.setIdPlan(rs.getInt("id_plan"));
                plan.setTema(rs.getString("tema"));
                plan.setFecha(rs.getDate("fecha"));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return plan;
    }

    @Override
    public List<Plan> listarPlanByUser(Usuario usuario) throws Exception {
        List<Plan> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT p.id_plan, p.tema, p.fecha\n"
                    + "FROM plan p, usuario u, plan_usuario pu\n"
                    + "WHERE p.id_plan= pu.id_plan AND\n"
                    + "pu.id_usuario = u.id_usuario AND u.id_usuario=?");

            st.setInt(1, usuario.getIdUsuario());
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Plan plan = new Plan();
                plan.setIdPlan(rs.getInt("id_plan"));
                plan.setTema(rs.getString("tema"));
                plan.setFecha(rs.getDate("fecha"));
                lista.add(plan);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;
    }

    @Override
    public void guardarPlan(String tema, Date fecha, List<String> listIntegrantes, Usuario user) throws Exception {

        try {
            this.Conectar();
            this.getCn().setAutoCommit(false);
            PreparedStatement st = this.getCn().prepareStatement("INSERT INTO plan (tema,fecha) VALUES(?,?)");
            st.setString(1, tema);
            st.setDate(2, (Date) fecha);
            st.executeUpdate();
            st.close();

            PreparedStatement st2 = this.getCn().prepareStatement("SELECT MAX(id_plan) AS id_plan FROM plan");
            ResultSet rs;
            int idPlan = 0;
            rs = st2.executeQuery();
            while (rs.next()) {
                idPlan = rs.getInt("id_plan");
            }
            rs.close();
            st2.close();

            PreparedStatement st5 = this.getCn().prepareStatement("INSERT INTO plan_usuario (id_plan, id_usuario) VALUES(?,?)");
            st5.setInt(1, idPlan);
            st5.setInt(2, user.getIdUsuario());
            st5.executeUpdate();
            st5.close();

            List<Usuario> listUsuario = new ArrayList();
            for (int i = 0; i < listIntegrantes.size(); i++) {
                PreparedStatement st3 = this.getCn().prepareStatement("SELECT id_usuario FROM usuario WHERE nombre = ?");
                ResultSet rs2;
                st3.setString(1, listIntegrantes.get(i));
                rs2 = st3.executeQuery();
                while (rs2.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs2.getInt("id_usuario"));
                    listUsuario.add(usuario);
                }
                rs2.close();
                st3.close();
            }

            for (int i = 0; i < listUsuario.size(); i++) {
                PreparedStatement st4 = this.getCn().prepareStatement("INSERT INTO plan_usuario (id_plan,id_usuario) VALUES (?,?)");
                st4.setInt(1, idPlan);
                st4.setInt(2, listUsuario.get(i).getIdUsuario());
                st4.executeUpdate();
                st4.close();
            }

            this.getCn().commit();
            SendMailGmail servicio = new SendMailGmail();
            servicio.enviarPrimerMail(this.findPlanById(idPlan));
        } catch (Exception e) {
            this.getCn().rollback();
            throw e;
        } finally {
            this.Cerrar();
        }
    }

    public void BarrerPlan() throws Exception {
        List<Plan> lista, lista2;
        int caso;
        try {
            lista = this.listarPlan();
            lista2 = lista;
            SendMailGmail servicio = new SendMailGmail();
            System.out.println("qqqqqqqqqqqqqqqqqqqqqq: " + lista.size());
            String fechaPlan = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < lista.size(); i++) {
                System.out.println("qqqqqqqqqqqqqqqqqqqqqqqq: " + lista.get(i).getFecha());
                fechaPlan = sdf.format(lista.get(i).getFecha());
                caso = tipoEmail(lista2.get(i).getFecha());
                lista.get(i).setFecha(sdf.parse(fechaPlan));
                System.out.println("qqqqqqqqqqqqqqqqqqqqqqqq2222222: " + lista.get(i).getFecha());
                switch (caso) {
                    case 1:
                        System.out.println("paso x aqui!!!");
                        servicio.enviarSegundoMail(lista.get(i));
                        break;
                    case 2:
                        System.out.println("paso x aqui222!!!");
                        servicio.enviarTercerMail(lista.get(i));
                        break;
                    default:
                        System.out.println("paso x aqui default!!!");
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private int tipoEmail(java.util.Date fecha) {
        int caso = 0;
        SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd");
        String stringDate = DateFor.format(fecha);
        java.util.Date fechaAux = fecha;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date fechaActual = new java.util.Date();
            fechaActual = sdf.parse(sdf.format(fechaActual));
            //fechaActual = sdf.parse(fechaActual.getYear()+"-"+fechaActual.getMonth()+"-"+fechaActual.getDate());
            System.out.println("Fecha auxiliar" + fechaAux);
            fechaAux.setDate(fechaAux.getDate() + 15);
            System.out.println("Fecha auxiliar +15 dias: " + fechaAux);
            System.out.println("Fecha Actual: " + fechaActual);
            if (fechaAux.compareTo(fechaActual) == 0) {
                fechaAux.setDate(fechaAux.getDate() - 15);
                System.out.println(fechaAux.compareTo(fechaActual));
                caso = 1;
            } else {
                fechaAux.setDate(fechaAux.getDate() - 15);
                fechaAux.setMonth(fechaAux.getMonth() + 5);
                System.out.println("Fecha auxiliar +5 meses: " + fechaAux);
                if (fechaAux.compareTo(fechaActual) == 0) {
                    System.out.println(fechaAux.compareTo(fechaActual));
                    caso = 2;
                }
                //fechaAux.setMonth(fechaAux.getMonth() - 5);
                fecha = DateFor.parse(stringDate);
                fechaAux = DateFor.parse(stringDate);
            }
//            fecha = DateFor.parse(stringDate);
//            fechaAux=fecha;
        } catch (Exception e) {

        }
        return caso;
    }

    @Override
    public void guardarPropuestaPlan(String txtTema, String txtDetalle, Usuario user) throws Exception {
        try {
            this.Conectar();
            this.getCn().setAutoCommit(false);
            PreparedStatement st = this.getCn().prepareStatement("INSERT INTO plan (tema,detalle,propuesto_por, aprobado) VALUES(?,?,?,FALSE)");
            st.setString(1, txtTema);
            st.setString(2, txtDetalle);
            st.setInt(3, user.getIdUsuario());
            st.executeUpdate();
            st.close();

            PreparedStatement st2 = this.getCn().prepareStatement("SELECT MAX(id_plan) AS id_plan FROM plan");
            ResultSet rs;
            int idPlan = 0;
            rs = st2.executeQuery();
            while (rs.next()) {
                idPlan = rs.getInt("id_plan");
            }
            rs.close();
            st2.close();

            PreparedStatement st3 = this.getCn().prepareStatement("INSERT INTO plan_usuario (id_plan, id_usuario, postulado) VALUES(?,?,FALSE)");
            st3.setInt(1, idPlan);
            st3.setInt(2, user.getIdUsuario());
            st3.executeUpdate();
            st3.close();
            this.getCn().commit();
        } catch (Exception e) {
            this.getCn().rollback();
            throw e;
        } finally {
            this.Cerrar();
        }

    }

    @Override
    public void postular(Plan plan, Usuario user) throws Exception {
        try {

            this.Conectar();
            this.getCn().setAutoCommit(false);
            PreparedStatement st = this.getCn().prepareStatement("INSERT INTO plan_usuario (id_plan,id_usuario,postulado) VALUES(?,?,FALSE)");
            st.setInt(1, plan.getIdPlan());
            st.setInt(2, user.getIdUsuario());
            st.executeUpdate();
            st.close();
            this.getCn().commit();
        } catch (Exception e) {
            this.getCn().rollback();
            throw e;
        } finally {
            this.Cerrar();
        }
    }

    @Override
    public List<Plan> cargarMisPostulaciones(Usuario usuario) throws Exception {
        List<Plan> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT p.id_plan, p.tema, p.detalle, p.propuesto_por"
                    + " FROM plan p, plan_usuario pu\n"
                    + "WHERE p.id_plan=pu.id_plan AND\n"
                    + "p.aprobado='FALSE' AND\n"
                    + "p.listo='FALSE' AND\n"
                    + "pu.id_usuario=?");

            st.setInt(1, usuario.getIdUsuario());
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Plan plan = new Plan();
                plan.setIdPlan(rs.getInt("id_plan"));
                plan.setTema(rs.getString("tema"));
                plan.setDetalle(rs.getString("detalle"));
                plan.setPropuestoPor(rs.getInt("propuesto_por"));
                lista.add(plan);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;

    }

    @Override
    public List<Plan> cargarMisProyectos(Usuario usuario) throws Exception {
        List<Plan> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT p.id_plan, p.tema, p.detalle, p.propuesto_por \n"
                    + "FROM plan p, plan_usuario pu\n"
                    + "WHERE p.id_plan=pu.id_plan AND\n"
                    + "p.aprobado='TRUE' AND\n"
                    + "pu.id_usuario=?");

            st.setInt(1, usuario.getIdUsuario());
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Plan plan = new Plan();
                plan.setIdPlan(rs.getInt("id_plan"));
                plan.setTema(rs.getString("tema"));
                plan.setDetalle(rs.getString("detalle"));
                plan.setPropuestoPor(rs.getInt("propuesto_por"));
                lista.add(plan);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;

    }

    @Override
    public void listoRevision(Plan plan) throws Exception {
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareStatement("UPDATE plan \n"
                    + "SET listo = TRUE\n"
                    + "WHERE id_plan= ? ");
            st.setInt(1, plan.getIdPlan());
            st.executeUpdate();
            st.close();
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }

    }
    
    @Override
    public void noListoRevision(Plan plan) throws Exception {
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareStatement("UPDATE plan \n"
                    + "SET listo = FALSE\n"
                    + "WHERE id_plan= ? ");
            st.setInt(1, plan.getIdPlan());
            st.executeUpdate();
            st.close();
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }

    }

    @Override
    public List<Plan> cargarPlanesPorAprobar(Usuario user) throws Exception{
        List<Plan> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT p.id_plan, p.tema, p.detalle, p.propuesto_por \n"
                    + "FROM plan p, plan_usuario pu\n"
                    + "WHERE p.id_plan=pu.id_plan AND\n"
                    + "p.aprobado='FALSE' AND\n"
                    + "p.listo='TRUE' AND\n"
                    + "pu.id_usuario=?");

            st.setInt(1, user.getIdUsuario());
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Plan plan = new Plan();
                plan.setIdPlan(rs.getInt("id_plan"));
                plan.setTema(rs.getString("tema"));
                plan.setDetalle(rs.getString("detalle"));
                plan.setPropuestoPor(rs.getInt("propuesto_por"));
                lista.add(plan);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;
    }

    @Override
    public void aprobarTema(Plan plan) throws Exception {
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareStatement("UPDATE plan \n"
                    + "SET listo = FALSE, aprobado= TRUE, observaciones='N/A'\n"
                    + "WHERE id_plan= ? ");
            st.setInt(1, plan.getIdPlan());
            st.executeUpdate();
            st.close();
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
    }

    @Override
    public List<Plan> cargarPlanesPorRevisar() throws Exception {
        List<Plan> lista;
        ResultSet rs;
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareCall("SELECT id_plan, tema, detalle, propuesto_por \n"
                    + "FROM plan p\n"
                    + "WHERE listo='TRUE'");
            rs = st.executeQuery();
            lista = new ArrayList();
            while (rs.next()) {
                Plan plan = new Plan();
                plan.setIdPlan(rs.getInt("id_plan"));
                plan.setTema(rs.getString("tema"));
                plan.setDetalle(rs.getString("detalle"));
                plan.setPropuestoPor(rs.getInt("propuesto_por"));
                lista.add(plan);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
        return lista;
    }

    @Override
    public void noAprobarTema(Plan plan) throws Exception {
        try {
            this.Conectar();
            PreparedStatement st = this.getCn().prepareStatement("UPDATE plan \n"
                    + "SET listo = FALSE, observaciones= ?\n"
                    + "WHERE id_plan= ? ");
            st.setString(1, plan.getObservaciones());
            st.setInt(2, plan.getIdPlan());
            st.executeUpdate();
            st.close();
        } catch (Exception e) {
            throw e;
        } finally {
            this.Cerrar();
        }
    }
    
}

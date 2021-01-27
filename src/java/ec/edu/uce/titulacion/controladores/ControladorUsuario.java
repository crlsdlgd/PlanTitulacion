package ec.edu.uce.titulacion.controladores;

import ec.edu.uce.titulacion.dao.PlanUsuarioDao;
import ec.edu.uce.titulacion.dao.RolDao;
import ec.edu.uce.titulacion.dao.RolUsuarioDao;
import ec.edu.uce.titulacion.entidades.Usuario;
import ec.edu.uce.titulacion.dao.UsuarioDao;
import ec.edu.uce.titulacion.entidades.Plan;
import ec.edu.uce.titulacion.entidades.PlanUsuario;
import ec.edu.uce.titulacion.entidades.Rol;
import ec.edu.uce.titulacion.entidades.RolUsuario;
import ec.edu.uce.titulacion.utilities.VerificarRecaptcha;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;


import org.apache.commons.codec.digest.DigestUtils;
import org.primefaces.context.RequestContext;

@Named(value = "controladorUsuario")
@SessionScoped
//@RequestScoped
public class ControladorUsuario implements Serializable {

    @EJB
    private RolDao rolDao;
    @EJB
    private UsuarioDao usuarioDao;
    @EJB
    private PlanUsuarioDao planUsuarioDao;
    @EJB
    private RolUsuarioDao rolUsuarioDao;

    public static Usuario user;
    private Usuario usuarioPrecursor;
    private List<String> listaRolUser;
    private List<PlanUsuario> listaPostulantes;
    private String nick, password, nombre, cedula, correo, password2;
    private String rolSelect;
    private Rol usuarioPrecursorRol;
    private List<RolUsuario> listaIntegrantes;

    public void login() throws Exception {
        String ecripted = DigestUtils.md5Hex(password);
        password = ecripted;
        Usuario usr = usuarioDao.buscarUsuarioLogin(nick, password);

        try {
            String gRecaptchaResponse = FacesContext.getCurrentInstance().
                    getExternalContext().getRequestParameterMap().get("g-recaptcha-response");
            boolean verify = VerificarRecaptcha.verificar(gRecaptchaResponse);
            if (verify) {
                if (usr == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario y/o Contraseña incorrectos", null));
                } else {
                    this.user = usr;
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usr);

                    this.listaRolUser = new ArrayList<String>();
                    List<Rol> lista = rolDao.buscarRolByUser(user);

                    for (int i = 0; i < lista.size(); i++) {
                        this.listaRolUser.add(lista.get(i).getRol());
                    }
                    this.setListaRolUser(listaRolUser);
                    rolSelect = listaRolUser.get(0);
                    RequestContext.getCurrentInstance().execute("PF('wlgRol').show();");
                }
            }
        } catch (Exception e) {
        }

//        try {
//            if (usr == null) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario y/o Contraseña incorrectos", null));
//            } else {
//                this.user = usr;
//                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usr);
//
//                this.listaRolUser = new ArrayList<String>();
//                List<Rol> lista = rolDao.buscarRolByUser(user);
//
//                for (int i = 0; i < lista.size(); i++) {
//                    this.listaRolUser.add(lista.get(i).getRol());
//                }
//                this.setListaRolUser(listaRolUser);
//                rolSelect = listaRolUser.get(0);
//                RequestContext.getCurrentInstance().execute("PF('wlgRol').show();");
//            }
//        } catch (IOException e) {
//        }
    }

    public String redireccionRol() {
        String redireccion = "";
        try {
            FacesContext contex = FacesContext.getCurrentInstance();
            //contex.getExternalContext().getSessionMap().put("usuario", user);
            switch (rolSelect) {
                case "Estudiante":
                    redireccion = "irHomeEstudiante";
//                    contex.getExternalContext().redirect("homeEstudiante.xhtml");
                    break;
                case "Docente":
                    redireccion = "irHomeDocente";
//                    contex.getExternalContext().redirect("irHomeDocente");
                    break;
                case "Consejo":
                    redireccion = "irHomeConsejo";
//                    contex.getExternalContext().redirect("homeConsejo.xhtml");
                    break;
                default:
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se encuentra implementado tal rol", null));
            }
        } catch (Exception e) {
        }
        return redireccion;
    }

    public void verUsuarioPrecursor(Plan plan) throws Exception {
        setUsuarioPrecursor(usuarioDao.buscarUsuarioPrecursor(plan));
        setUsuarioPrecursorRol(rolDao.buscarRolByUser(getUsuarioPrecursor()).get(0));
        RequestContext.getCurrentInstance().execute("PF('wdlWare2').show();");
    }

    public void cerrarSession() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }

    public void verPostulantes(Plan plan) throws Exception {
        setListaPostulantes(planUsuarioDao.listarPostulantesByPlan(plan));
        RequestContext.getCurrentInstance().execute("PF('wdlWare3').show();");
    }

    public void verIntegrantes(Plan plan) throws Exception {
        setListaIntegrantes(rolUsuarioDao.listarIntegrantesByPlan(plan));
        RequestContext.getCurrentInstance().execute("PF('wdlWare3').show();");
    }

    public void guardarProyecto() throws Exception {
        planUsuarioDao.guardarProyecto(getListaPostulantes());
    }

    public void verificarCatpchaRegistro() {
        System.out.println("verificarCaptcha pass: "+password);
        try {
            String gRecaptchaResponse = FacesContext.getCurrentInstance().
                    getExternalContext().getRequestParameterMap().get("g-recaptcha-response");
            boolean verify = VerificarRecaptcha.verificar(gRecaptchaResponse);
            if (verify) {
                System.out.println("Captcha correcto");
                RequestContext.getCurrentInstance().execute("PF('wlgRegistro').show();");
            } else {
                System.out.println("Captcha incorrecto");
            }
        } catch (Exception e) {

        }
    }

    public String guardarUsuario() throws Exception {

        System.out.println("di algo "+password);

        String aux = "";
        boolean flag = false;

        if (password.equals(password2)) {
            String encripted = DigestUtils.md5Hex(password);
            password = encripted;
            System.out.println("1......." + password);
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(correo);
            usuario.setCedula(cedula);
            usuario.setPassword(password);
            flag = usuarioDao.registrar(usuario);
            System.out.println("1.1......se registro");
        } else {
            System.out.println("2......." + password);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Las contraseñas no coinciden", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        if (flag) {
            addMessage("Registro Exitoso!");
            aux = "inicio";
            System.out.println("3.......yea" + password);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Existe un error en su número de Cédula o Correo", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
            System.out.println("4.......mal" + password);
        }

        return aux;
    }

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public ControladorUsuario() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Usuario getUsuarioPrecursor() {
        return usuarioPrecursor;
    }

    public void setUsuarioPrecursor(Usuario usuarioPrecursor) {
        this.usuarioPrecursor = usuarioPrecursor;
    }

    public Rol getUsuarioPrecursorRol() {
        return usuarioPrecursorRol;
    }

    public void setUsuarioPrecursorRol(Rol usuarioPrecursorRol) {
        this.usuarioPrecursorRol = usuarioPrecursorRol;
    }

    public void setListaRolUser(List<String> listaRolUser) {
        this.listaRolUser = listaRolUser;
    }

    public List<String> getListaRolUser() throws Exception {

        return listaRolUser;
    }

    public String getRolSelect() {
        return rolSelect;
    }

    public void setRolSelect(String rolSelect) {
        this.rolSelect = rolSelect;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public List<PlanUsuario> getListaPostulantes() {
        return listaPostulantes;
    }

    public void setListaPostulantes(List<PlanUsuario> listaPostulantes) {
        this.listaPostulantes = listaPostulantes;
    }

    public List<RolUsuario> getListaIntegrantes() {
        return listaIntegrantes;
    }

    public void setListaIntegrantes(List<RolUsuario> listaIntegrantes) {
        this.listaIntegrantes = listaIntegrantes;
    }
}

package ec.edu.uce.titulacion.controladores;

import ec.edu.uce.titulacion.entidades.Usuario;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@Named(value = "controladorTemplate")
@SessionScoped
public class ControladorTemplate implements Serializable{

    public void verificarSession() {
        System.out.println("veririca sesion");
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            Usuario us = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
            System.out.println("usuario recuperado: "+us.getNick());
            if(us==null){
                context.getExternalContext().redirect("error.xhtml");
            }   
        } catch (Exception e) {
        }
    }
    
}

package ec.edu.uce.titulacion.entidades;

public class PlanUsuario {
    private Plan plan;
    private Usuario usuario;
    private boolean postulado;

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isPostulado() {
        return postulado;
    }

    public void setPostulado(boolean postulado) {
        this.postulado = postulado;
    }
    
    
}

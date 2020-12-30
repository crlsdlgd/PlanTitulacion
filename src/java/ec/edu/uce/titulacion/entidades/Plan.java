package ec.edu.uce.titulacion.entidades;

import java.util.Date;

public class Plan {
    private Integer idPlan,propuestoPor;
    private String tema,detalle,observaciones;
    private Date fecha;
    private boolean aprobado;
    
    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    

    public Integer getPropuestoPor() {
        return propuestoPor;
    }

    public void setPropuestoPor(Integer propuestoPor) {
        this.propuestoPor = propuestoPor;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}

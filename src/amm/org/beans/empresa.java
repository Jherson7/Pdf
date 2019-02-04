package amm.org.beans;

import java.util.LinkedList;

/**
 *
 * @author jortiz
 */
public class empresa {
    public String empresa;
    String representante;
    int    cod_rep;
    LinkedList<String>correos;
    public LinkedList<objeto_reporte> registros;

    public empresa(String codigo) {
        this.empresa= codigo;
        this.representante ="";
        this.registros = new  LinkedList<>();
        this.correos = new LinkedList<>();
    }

    public int getCod_rep() {
        return cod_rep;
    }

    public void setCod_rep(int cod_rep) {
        this.cod_rep = cod_rep;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        this.representante = representante;
    }

    public LinkedList<String> getCorreos() {
        return correos;
    }

    public void setCorreos(LinkedList<String> correos) {
        this.correos = correos;
    }

    public LinkedList<objeto_reporte> getRegistros() {
        return registros;
    }

    public void setRegistros(LinkedList<objeto_reporte> registros) {
        this.registros = registros;
    }

    @Override
    public String toString() {
        return "empresa{" + "empresa=" + empresa + ", representante=" + representante + ", cod_rep=" + cod_rep +", correos="+ correos+'}';
    }
    
    
}

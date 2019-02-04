
package amm.org.beans;

import java.sql.Timestamp;

/**
 *
 * @author jortiz
 */
public class objeto_reporte {
   private String agente;
   private int canal;
   private String nombre;
   private Timestamp lectura;
   private String tel  ;
   private String id_gu;
   private int ke ;

    public objeto_reporte() {
    }

    public objeto_reporte(String agente, int canal, String nombre, Timestamp lectura, String tel, String id_gu, int ke) {
        this.agente = agente;
        this.canal = canal;
        this.nombre = nombre;
        this.lectura = lectura;
        this.tel = tel;
        this.id_gu = id_gu;
        this.ke = ke;
    }

    public String getAgente() {
        return agente;
    }

    public void setAgente(String agente) {
        this.agente = agente;
    }

    public int getCanal() {
        return canal;
    }

    public void setCanal(int canal) {
        this.canal = canal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Timestamp getLectura() {
        return lectura;
    }

    public void setLectura(Timestamp lectura) {
        this.lectura = lectura;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getId_gu() {
        return id_gu;
    }

    public void setId_gu(String id_gu) {
        this.id_gu = id_gu;
    }

    public int getKe() {
        return ke;
    }

    public void setKe(int ke) {
        this.ke = ke;
    }

    @Override
    public String toString() {
        return "objeto_reporte{" + "agente=" + agente + ", canal=" + canal + ", nombre=" + nombre + ", lectura=" + lectura + ", tel=" + tel + ", id_gu=" + id_gu + ", ke=" + ke + '}';
    }
 
}

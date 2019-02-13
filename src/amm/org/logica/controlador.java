package amm.org.logica;

import amm.org.beans.correo;
import amm.org.beans.empresa;
import amm.org.beans.objeto_reporte;
import amm.org.db.Conexion;
import amm.org.reportes.ManejadorReportes;
import java.io.File;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author Jherson Sazo
 */

public class controlador {
    
    private LinkedList<empresa>listado_empresas;
    private String correlativo;
    private String mes_medicion;
    private String anio_medicion;
    private String fecha_inicio;
    private String fecha_fin;
    public LinkedList<correo> lista_correos;
    
    private static final Logger log = Logger.getLogger(controlador.class);
    
    public controlador() {
        this.listado_empresas = new LinkedList<>();
        this.lista_correos = new LinkedList<>();
        log.info("Se inicio el controlador");
    }

    public void iniciar(String correlativo,String mes_med,String anio_med,String inicio, String fin) {
        
        this.correlativo= correlativo;
        this.mes_medicion=mes_med;
        this.anio_medicion= anio_med;
        this.fecha_fin= fin;
        this.fecha_inicio =inicio;
        
        
        log.info("Se inicio la obtencion de empresas");
        llenar_empresas(Conexion.get_reporte_por_fechas(inicio, fin));
        
        
        
        if (listado_empresas.size() > Conexion.no_max_medicion) {
            log.info("El numero de registros es mayor al maximo asignado");
            //se envia un correo a los representantes de AMM ya que hay un error
        } 
    //else {
            
            crear_directorio_cartas(anio_med,mes_med);
            
            llenar_representantes_empresas(listado_empresas);
            llenar_correos_empresas(listado_empresas);
            generar_cartas_empresas(listado_empresas);
            enviar_cartas_empresas(listado_empresas);

      //  }
      
        /*for (empresa a : listado_empresas) {
            System.out.println("------------" + a+ "-----------------");
            for (objeto_reporte reg : a.registros) {
                  System.out.println(reg);
            }
            System.out.println("---------------------------------------------------------------------------");
        }*/
    }
    
    private void llenar_empresas(LinkedList<objeto_reporte> lista){

        String anterior = lista.getFirst().getAgente();
        empresa nueva_emp = new empresa(anterior);
        
        for(objeto_reporte reg : lista){
            if(!anterior.equals(reg.getAgente())){
                listado_empresas.add(nueva_emp);
                anterior = reg.getAgente();
                nueva_emp = new empresa(anterior);
                nueva_emp.registros.add(reg);
            }else{
                nueva_emp.registros.add(reg);
            }
        }
    }

    private void llenar_representantes_empresas(LinkedList<empresa> listado_empresas) {
        log.info("Agrupacion de registros por empresas");
        for(empresa a: listado_empresas){
            Conexion.get_representante_empresa(a.getEmpresa(), a);
        }
    }

    private void llenar_correos_empresas(LinkedList<empresa> listado_empresas) {
        log.info("Llenado de correos por empresas");
        for(empresa a: listado_empresas){
            LinkedList<String> lista_correos = Conexion.get_correos_representantes(a.getCod_rep());
            for(String email : lista_correos)
                llenar_correo(email,a.getEmpresa());
        }
    }

    private void generar_cartas_empresas(LinkedList<empresa> listado_empresas) {
         
        for(empresa a: listado_empresas){
            log.info("Generacion de cartas para: "+a.empresa);
            insertar_registros_temporales(a.getRegistros());
            ManejadorReportes.generar_carta(this.correlativo,"Sin definir",a.empresa,this.mes_medicion,this.anio_medicion,this.fecha_inicio, this.fecha_fin);
            Conexion.limpiar_temp();
            ////generar_carta_
            //eliminar_registros
        }
    }

    private void insertar_registros_temporales(LinkedList<objeto_reporte> registros) {
        for(objeto_reporte ob:registros){
            Conexion.insertar_registro_temporal(ob);
        }
    }
    
    private void enviar_cartas_empresas(LinkedList<empresa> listado_empresas) {
           
            for(correo aux: lista_correos){
                String archivos ="";
                for(String carta:aux.cartas)
                    archivos = archivos.equals("")?carta:archivos+";"+carta;
            
            Conexion.enviar_correos("desarrollo.amm@amm.org.gt","prueba papa", "test.pdf",aux.cartas.toString());
           // Conexion.enviar_correos("Direccion de correo destino","mensaje", archivos,aux.cartas.toString());
           
            }
        /*for(empresa a: listado_empresas){
             String correos ="";
             for(String correo : a.getCorreos()){
                 if(correos.equals(""))
                     correos=correo;
                 else
                    correos+=";"+correo;
             }*/
             //System.out.println(correos);
             //Conexion.enviar_correos("desarrollo.amm@amm.org.gt","prueba papa", "test.pdf",a.empresa);
         //}
    }

    private void crear_directorio_cartas(String anio_med, String mes_med) {
        log.info("Creacion del directorio para cartas: "+Conexion.ruta_cartas+anio_med+"/"+mes_med);
        String ruta_ =Conexion.ruta_cartas+anio_med+"/"+mes_med;
        
        File tmpDir = new File(ruta_);
        //
        if(!tmpDir.exists()){
            try{
                tmpDir.mkdirs();
                //cambiar la ruta por defecto en 
                //oracle para el envio de cartas
            }catch(Exception e){
                System.out.println("Error al crear el directorio");
            }
        }
        
        Conexion.ruta_cartas=ruta_;//+"/";//cambio la ruta para que ahi se guarden todas las cartas
        Conexion.modificar_registro_temporal(Conexion.ruta_cartas);
    }

    private void llenar_correo(String email, String empresa) {
        correo aux = null;
        
        if(email.equals(""))
            return;
        for(correo a: lista_correos){
            if(a.direccion.equalsIgnoreCase(email)){
                aux= a;
                break;
            }
        }
        
        if(aux==null){
            lista_correos.add(new correo(email, empresa+".pdf"));
        }else{
            aux.cartas.add(empresa+".pdf");
        }
    }

}

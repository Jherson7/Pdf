package amm.org.logica;

import amm.org.beans.empresa;
import amm.org.beans.objeto_reporte;
import amm.org.db.Conexion;
import java.util.LinkedList;

/**
 *
 * @author jortiz
 */
public class controlador {
    
    private LinkedList<empresa>listado_empresas;

    public controlador() {
        this.listado_empresas = new LinkedList<>();
    }

    public void iniciar(String inicio, String fin) {
        
        llenar_empresas(Conexion.get_reporte_por_fechas(inicio, fin));

        llenar_representantes_empresas(listado_empresas);
        llenar_correos_empresas(listado_empresas);
        generar_cartas_empresas(listado_empresas);
        //enviar_cartas_empresas(listado_empresas);
        
        for (empresa a : listado_empresas) {
            System.out.println("------------" + a+ "-----------------");
            for (objeto_reporte reg : a.registros) {
                  System.out.println(reg);
            }
            System.out.println("---------------------------------------------------------------------------");
        }
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
        for(empresa a: listado_empresas){
            Conexion.get_representante_empresa(a.getEmpresa(), a);
        }
    }

    private void llenar_correos_empresas(LinkedList<empresa> listado_empresas) {
        for(empresa a: listado_empresas){
            a.setCorreos(Conexion.get_correos_representantes(a.getCod_rep()));
        }
    }

    private void generar_cartas_empresas(LinkedList<empresa> listado_empresas) {
         for(empresa a: listado_empresas){
            insertar_registros_temporales(a.getRegistros());
            Conexion.limpiar_temp();
            System.out.println("-------------------JE-------------------");
            //generar_carta_
            //eliminar_registros
        }
    }

    private void enviar_cartas_empresas(LinkedList<empresa> listado_empresas) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void insertar_registros_temporales(LinkedList<objeto_reporte> registros) {
        for(objeto_reporte ob:registros){
            Conexion.insertar_registro_temporal(ob);
           
        }
    }

    
}

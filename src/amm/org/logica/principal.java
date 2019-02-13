package amm.org.logica;

import amm.org.db.Conexion;
import amm.org.logica.controlador;
import amm.org.reportes.ManejadorReportes;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Jherson Sazo
 */

public class principal {

    private static final Logger log = Logger.getLogger(principal.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        log.info("Se inicio el proceso **************REPORTES DE MEDICION**************");
        Connection con = Conexion.get_con();          //al llamar al metodo get_con, se inicializa la conexion
                                                      //se carga el archivo properties necesario para generar las 
                                                      //cartas.
       
        //Conexion.modificar_registro_temporal("SAazo");
        
                                                     
        controlador c = new controlador();            //instancia de controlador que realiza todo el proceso
        Date fecha_actual = new Date();               //varible para obtener la fecha actual y tomar valores.
        int fecha_medicion =fecha_actual.getMonth()-1;//obtengo el mes actual y le resto 1, con esto 
                                                      //devuelve la fechas del mes anterior, que se usan
                                                      //para generar el reporte de medicion.
        
        
        String anio_medicion = String.valueOf(fecha_actual.getYear()+1900);//anio para generar la carta
        
        String []fechas =get_fechas_reporte(fecha_medicion);//fecha de inicio y fin del reporte, se obtiene del mes anterior
        String correlativo = "ME-0"+fecha_actual.getDate()+"-"+anio_medicion;//correlativo para la carta
       
        System.out.println("Inicio: "+fechas[0]);
        System.out.println("Fin: "+fechas[1]);
        c.iniciar(correlativo,get_month(fecha_medicion),anio_medicion,fechas[0], fechas[1]);
        System.out.println(System.getProperty("os.name"));
        //ManejadorReportes.generar_carta(correlativo,"Jherson","AMM",get_month(fecha_medicion),anio_medicion,fechas[0],fechas[1]);

    }
    
    private static String get_month(int val) {
        switch (val) {
            case 0:
                return "enero";
            case 1:
                return "febrero";
            case 2:
                return "marzo";
            case 3:
                return "abril";
            case 4:
                return "mayo";
            case 5:
                return "junio";
            case 6:
                return "julio";
            case 7:
                return "agosto";
            case 8:
                return "septiembre";
            case 9:
                return "octubre";
            case 10:
                return "noviembre";
            default:
                return "diciembre";
        }
    }
    
    private static String[] get_fechas_reporte(int val){
        
        Date today = new Date();  
        today.setMonth(val);
        today.setDate(1);
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(today);  

        calendar.add(Calendar.MONTH, 1);  
        calendar.set(Calendar.DAY_OF_MONTH, 1);  
        calendar.add(Calendar.DATE, -1);  

        Date lastDayOfMonth = calendar.getTime();  

        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
        System.out.println("Today            : " + sdf.format(today));  
        System.out.println("Last Day of Month: " + sdf.format(lastDayOfMonth)); 
        
        String []fechas = {sdf.format(today),sdf.format(lastDayOfMonth)};
        
        return fechas;
    }
}

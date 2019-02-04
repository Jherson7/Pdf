package amm.org.db;

import amm.org.logica.controlador;
import java.sql.Connection;

/**
 *
 * @author jortiz
 */
public class principal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection con = Conexion.get_con();
        controlador c = new controlador();
        c.iniciar("01/12/2018", "31/12/2018");
        
   
    }
    
}

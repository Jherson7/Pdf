package amm.org.db;

import amm.org.beans.empresa;
import amm.org.beans.objeto_reporte;
import amm.org.logica.controlador;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Jherson Sazo
 */
public class Conexion {
  
    private static Connection conn ;
    private static String driver   ;
    private static String user     ;
    private static String url      ;
    private static String pass     ;
    public static  String ruta_cartas;
    public static  String correo_amm;
    public static  int    no_max_medicion;
    public static String nombre_encargado_amm;
    public static String ocupacion_encargado_amm;
    
    static PreparedStatement stament;//variable para realizar inserciones, modificaciones o eliminacion a DB
    static Statement st; //variable para realizar consultas a DB
    
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Conexion.class);
    
    public static Connection get_con() {
       
        try {
            if (conn == null) {
                iniciar_propiedades();
                DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                System.out.println("Conectando con la base de datos...");
                conn = DriverManager.getConnection(url, user, pass);
               
                /*conn = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:XE", "system", "password");*/
                log.info("Conectado a Base de Datos");
                Class.forName(driver);
            }
            
            return conn;

        } catch (Exception e) {
            System.out.println("The exception raised is:" + e);
            log.info("Error al conectar a la BD> "+e.getMessage());
            return null;
        }
    }

    private static void iniciar_propiedades() {
        try {

            Properties prop = new Properties();
            InputStream input = new FileInputStream("src/db_connection.properties");
            prop.load(input);
            
             driver                 = prop.getProperty("driver");
             user                   = prop.getProperty("user");
             url                    = prop.getProperty("url");
             pass                   = prop.getProperty(("pass"));
             ruta_cartas            = prop.getProperty("ruta_cartas");
             correo_amm             = prop.getProperty("correo_medicion");
             no_max_medicion        = Integer.parseInt(prop.getProperty(("no_max_medidores")));
             nombre_encargado_amm   = prop.getProperty("ingeniero_a_cargo");
             ocupacion_encargado_amm = prop.getProperty("ocupacion_ingeniero");
             
             log.info("Carga de los parametros del archivo properties de la BD");
             
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public static LinkedList<objeto_reporte> get_reporte_por_fechas(String inicio,String fin) {
        try {
            String query = "select * from table (reporte_medicion.consultar_reporte_medicion('"+inicio+"', '"+fin+"'))";
                     
            st = conn.createStatement();
          //  System.out.println(query);
            ResultSet rs = st.executeQuery(query);

            LinkedList<objeto_reporte> result = new LinkedList();

            while (rs.next()) {
                //System.out.println(resultSet.getString(1));
                objeto_reporte es = new objeto_reporte(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getTimestamp(4), rs.getString(5), rs.getString(6), rs.getInt(7));
                result.add(es);
            }
            st.close();

            
            log.info("Obtencion del reporte de medicion");
            log.info(query);
            
            return result;

        } catch (Exception e) {
            System.out.println("Error al obtener la lista de medicion");
            System.out.println(e.getMessage());
            log.info("ERROR al consultar el reporte de la lista de medicion");
            return null;
        }
    }
    
    public static void get_representante_empresa(String codigo,empresa emp) {
        try {
            String query = "select * from table (reporte_medicion.obtener_representante_medicion('"+codigo+"'))";
                     
            st = conn.createStatement();
           // System.out.println(query);
            ResultSet rs = st.executeQuery(query);

            if (rs.next()) {
                //System.out.println(resultSet.getString(1));
                emp.setRepresentante(rs.getString(1));
                emp.setCod_rep(rs.getInt(2));
            }
            
            log.info("Obtencion del representante de "+codigo);
            st.close();
        } catch (Exception e) {
            System.out.println("Error al obtener el representante de  medicion de la empresa: "+codigo);
            log.info("ERROR al obtener del representante de "+codigo);
            System.out.println(e.getMessage());
        }
    }

    public static LinkedList<String> get_correos_representantes(int id_rep) {
        try {
            String query = "select * from table (reporte_medicion.obtener_correos_medicion("+id_rep+"))";
                     
            st = conn.createStatement();
           // System.out.println(query);
            ResultSet rs = st.executeQuery(query);

            LinkedList<String> result = new LinkedList();

            while (rs.next()) {
                result.add(rs.getString(1));
            }
            st.close();

            log.info("Obtencion de los correos de "+id_rep);
            return result;

        } catch (Exception e) {
            System.out.println("Error al obtener la lista de correos de medicion");
            log.info("Error al obtener la lista de correos de medicion de "+id_rep);
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    public static void insertar_registro_temporal(objeto_reporte obj){
        try {
            //stament = conn.prepareStatement("insert into temp_reportes_medicion values(?,?,?,?,?,?,?)");
            stament = conn.prepareStatement("insert into temp_rep_med values(?,?,?,?,?,?,?)");
            //insert into temp_reportes_medicion values('coo',234,'holis','jherson','jejeje','ajdfladj',56);
            stament.setString(1, obj.getAgente());
            stament.setInt(2, obj.getCanal());
            stament.setString(3, obj.getNombre());
            stament.setString(4, obj.getLectura().toString());
            stament.setString(5, obj.getTel());
            stament.setString(6, obj.getId_gu());
            stament.setInt(7, obj.getKe());
                       
            stament.execute();
            //imprimir_temp();
            stament.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al insertar los datos temporales");
            System.out.println(ex.getMessage());
        }
        
    }
    
    public static void enviar_correos(String correos,String mensaje,String archivo,String empresa){
        
        log.info("Envio de correos a: "+empresa+", <"+correos+">");
        
        try {
            //stament = conn.prepareStatement("insert into temp_reportes_medicion values(?,?,?,?,?,?,?)");
            String query = "begin\n" +
                            " amm.p_sendmailattach(\n" +
                            "'"+correos+"',\n" +
                            "NULL,\n" +
                            "'correo de medicion'     ,\n" +//asunto
                            "'"+mensaje+"'              ,\n" +
                            "'"+archivo+"'\n" +
                            ");\n" +
                            "end;";
            stament = conn.prepareStatement(query);
          //  stament.execute();
            stament.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al enviar los correos");
            log.info("ERROR al enviar los correos a: "+empresa+", <"+correos+">");
            System.out.println(ex.getMessage());
        }
        
    }
    
    public static void limpiar_temp() {
         try {
             String query = "truncate table temp_rep_med";

             st = conn.createStatement();
             st.execute(query);

             st.close();
         } catch (Exception e) {
             System.out.println("Error al obtener al truncar la tabla de registros temporales");
             System.out.println(e.getMessage());
         }
     }

    public static void modificar_registro_temporal(String nueva_ruta){
        log.info("Modificacion de la ruta CARTAS_MEDICION --> "+nueva_ruta);
        try {
            stament = conn.prepareStatement("create or replace directory  CARTAS_MEDICION AS '/home/oracle/cartas'");
           
            stament.execute();
            stament.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al modificar la ruta temporal los datos temporales");
            log.info("Error al modificar la ruta temporal los datos temporales --> "+nueva_ruta);
            System.out.println(ex.getMessage());
        }
    }
    
    private static void imprimir_temp() {
        try {
            String query = "select * from temp_reportes_medicion";
                     
            st = conn.createStatement();
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);


            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            st.close();

            
        } catch (Exception e) {
            System.out.println("Error al obtener la lista de correos de medicion");
            System.out.println(e.getMessage());
        }
    }
    

}


            
            


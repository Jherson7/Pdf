package amm.org.db;

import amm.org.beans.empresa;
import amm.org.beans.objeto_reporte;
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
 * @author jortiz
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
    
    static PreparedStatement stament;//variable para realizar inserciones, modificaciones o eliminacion a DB
    static Statement st; //variable para realizar consultas a DB
    
    
    public static Connection get_con() {
        try {
            if (conn == null) {
                iniciar_propiedades();
                DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                System.out.println("Conectando con la base de datos...");
                conn = DriverManager.getConnection(url, user, pass);
                /*conn = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:XE", "system", "jhony0104");*/
                Class.forName(driver);
            }
            
            return conn;

        } catch (Exception e) {
            System.out.println("The exception raised is:" + e);
            return null;
        }
    }

    private static void iniciar_propiedades() {
        try {

            Properties prop = new Properties();
            InputStream input = new FileInputStream("src/db_connection.properties");
            prop.load(input);
            
             driver             = prop.getProperty(("driver"));
             user               = prop.getProperty("user");
             url                = prop.getProperty("url");
             pass               = prop.getProperty(("pass"));
             ruta_cartas        = prop.getProperty(("ruta_cartas"));
             correo_amm         = prop.getProperty(("correo_medicion"));
             no_max_medicion    = Integer.parseInt(prop.getProperty(("no_max_medidores")));
             

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public static LinkedList<objeto_reporte> get_reporte_por_fechas(String inicio,String fin) {
        try {
            String query = "select * from table (reporte_medicion.consultar_reporte_medicion('"+inicio+"', '"+fin+"'))";
                     
            st = conn.createStatement();
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);

            LinkedList<objeto_reporte> result = new LinkedList();

            while (rs.next()) {
                //System.out.println(resultSet.getString(1));
                objeto_reporte es = new objeto_reporte(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getTimestamp(4), rs.getString(5), rs.getString(6), rs.getInt(7));
                result.add(es);
            }
            st.close();

            return result;

        } catch (Exception e) {
            System.out.println("Error al obtener la lista de medicion");
            System.out.println(e.getMessage());
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
            st.close();
        } catch (Exception e) {
            System.out.println("Error al obtener el representante de  medicion de la empresa: "+codigo);
            System.out.println(e.getMessage());
        }
    }
    //

    
    public static LinkedList<String> get_correos_representantes(int id_rep) {
        try {
            String query = "select * from table (reporte_medicion.obtener_correos_medicion("+id_rep+"))";
                     
            st = conn.createStatement();
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);

            LinkedList<String> result = new LinkedList();

            while (rs.next()) {
                result.add(rs.getString(1));
            }
            st.close();

            return result;

        } catch (Exception e) {
            System.out.println("Error al obtener la lista de correos de medicion");
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    
    public static void insertar_registro_temporal(objeto_reporte obj){
        try {
            stament = conn.prepareStatement("insert into temp_reportes_medicion values(?,?,?,?,?,?,?)");
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
    
    //truncate table 
   public static void limpiar_temp() {
        try {
            String query = "truncate table temp_reportes_medicion";
                     
            st = conn.createStatement();
            System.out.println(query);
            st.execute(query);


           /* while (rs.next()) {
                System.out.println(rs.getString(1));
            }*/
            st.close();

            
        } catch (Exception e) {
            System.out.println("Error al obtener al truncar la tabla de registros temporales");
            System.out.println(e.getMessage());
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


            
            


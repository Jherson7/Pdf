package amm.org.reportes;

import amm.org.db.Conexion;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;

public class ManejadorReportes {
    
    static Connection connection;//variable para poder conectarse a la DB 
    
    /*
    private final static String ruta_logo ="/amm/org/reportes/Logo.jpg";
    private final static String firma ="/amm/org/reportes/Firma.jpg";
    */
    private final static String ruta_logo ="src/pictures/Logo.jpg";
    private final static String firma ="src/pictures/Firma.jpg";
    
    private static String contenido=""//contenido de la carta
            + "Conforme el apartado 14.10 de la NCC-14, adjunto a la presente se remite "
            + "el listado de medidores de su representada no interrogados automáticamente "
            + "en <b>month</b> de <b>year</b>, solicitándole <b>que en el plazo <u>de dos días "
            + "calendario</u> se sirva: i) ingresar los registros horarios de medición al sistema "
            + "Direct@MM; y ii) enviar los archivos fuente de los medidores <u>con datos de cada "
            + "15 minutos</u> "
            + "a la dirección <a style = 'color:blue; text-decoration:underlined;' href = 'mailto:medicion@amm.org.gt'>"
            + "medicion@amm.org.gt."
            + "</a></b> Vencido este plazo el AMM estimará la medición conforme la normativa "
            + "vigente: Para el consumo se estimará la medición incrementando 10% a los "
            + "registros de medición del mes anterior y para la generación se descontará "
            + "5% a los datos registrados en el Centro de Despacho de Carga "
            + "para el mes correspondiente. " +
                                     "<br><br>" +
            "Por otra parte, se solicita: <b>i) informar lo antes posible al AMM cualquier "
            + "modificación de emergencia realizada a los equipos de medición</b> "
            + "para mantener actualizada la base de datos SMEC; y ii) tomar las medidas "
            + "necesarias <b>para que siempre estén disponibles los enlaces IP de los medidores</b> "
            + "a efecto de no interrumpir el proceso de interrogación automática de los mismos "
            + "desde el AMM. ";
    
    
    /**
     * @param correlativo para generar la carta
     * @param representante
     * @param empresa para quien labora
     * @param mes_medicion que se realizo la medicion del script
     * @param anio_medicion que se realizo la medicion del script
     * @param fecha_ini
     * @param fecha_fin
     */
    public static void generar_carta(String correlativo,String representante, String empresa,
            String mes_medicion,String anio_medicion,String fecha_ini,String fecha_fin)
    
    {
        contenido =contenido.replaceAll("month", mes_medicion);//se reemplaza por el mes de reporte
        contenido =contenido.replaceAll("year", anio_medicion);//se reemplaza por el año de reporte
        
        try {
            JasperReport  jr= (JasperReport) JRLoader.
                   loadObject(ManejadorReportes.
                           class.getResourceAsStream("/amm/org/reportes/Reporte_medicion.jasper"));
            
            connection = Conexion.get_con();
            
            Map parametros = new HashMap();
            parametros.put("correlativo", correlativo);
            parametros.put("representante", representante);
            parametros.put("empresa", empresa);
            parametros.put("contenido_carta", contenido);
            /*parametros.put("logo", ManejadorReportes.
                           class.getResourceAsStream(ruta_logo));
              parametros.put("firma", ManejadorReportes.
                           class.getResourceAsStream(firma));
            */
             parametros.put("logo", new FileInputStream(ruta_logo));
             parametros.put("firma", new FileInputStream(firma));
             parametros.put("ingeniero_a", Conexion.nombre_encargado_amm);
             parametros.put("ocupacion", Conexion.ocupacion_encargado_amm);
            
            JasperPrint jp;
            jp = JasperFillManager.fillReport(jr, parametros, connection);

            generar_pdf(jp,empresa);
            
            generar_sub_reporte(empresa,fecha_ini,fecha_fin);
            
            unir_pdf(Conexion.ruta_cartas+empresa + ".pdf", Conexion.ruta_cartas+empresa+"_sub" + ".pdf");
            /*JasperViewer visor = new JasperViewer(jp,false);
            visor.setTitle("Carta de medicion");
            visor.setVisible(true);*/
            
        } catch (Exception e) {
            System.out.println("Hubo un error pero podemos continuar :D");
            //System.out.println(e.getLocalizedMessage());
        }
    }
    
    private static void generar_pdf(JasperPrint jp,String nombre_pdf) {
        try {
            JRExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(Conexion.ruta_cartas+nombre_pdf + ".pdf")); // your output goes here
            //exporter. setParameter ( JRExporterParameter.  , fontMap ) ; 
            
            exporter.exportReport();
            
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR al generar el PDF");
            //Logger.getLogger(ManejadorReportes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException ex) {
            System.out.println("ERROR al generar el PDF");
            //Logger.getLogger(ManejadorReportes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void generar_sub_reporte(String empresa,String fecha_ini,String fecha_fin){
        try {
            JasperReport  jr= (JasperReport) JRLoader.
                    loadObject(ManejadorReportes.
                            class.getResourceAsStream("/amm/org/reportes/sub_reporte_medicion.jasper"));
            
            connection = Conexion.get_con();
            
            Map parametros = new HashMap();
            parametros.put("agente", empresa);
            parametros.put("fecha_ini", fecha_ini);
            parametros.put("fecha_fin", fecha_fin);
            parametros.put("img", new FileInputStream("src/pictures/logo.png"));

            JasperPrint jp;
            jp = JasperFillManager.fillReport(jr, parametros, connection);

            generar_pdf(jp,empresa+"_sub");
            
            //eliminamos el sub
            /*File file = new File(Conexion.ruta_cartas+empresa+"_sub" + ".pdf");
            if (file.delete()) {
                System.out.println("Subreporte eliminado");;
            }*/
            
            
        } catch (JRException ex) {
            System.out.println("Hubo un error pero podemos continuar :D");
            //Logger.getLogger(ManejadorReportes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ManejadorReportes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static void unir_pdf(String ruta1,String ruta2){
     
        try {
            PDFMergerUtility ut = new PDFMergerUtility();
            ut.addSource(ruta1);
            ut.addSource(ruta2);
            
            //ut.setDestinationFileName(Conexion.ruta_cartas+"Final_3" + ".pdf"); 
            ut.setDestinationFileName(ruta1); 
            ut.mergeDocuments();
           
        } catch (IOException ex) {
            Logger.getLogger(ManejadorReportes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (COSVisitorException ex) {
            Logger.getLogger(ManejadorReportes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

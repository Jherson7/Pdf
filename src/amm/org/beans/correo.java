/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amm.org.beans;

import java.util.LinkedList;

/**
 *
 * @author jortiz
 */
public class correo {
    public String direccion;
    public LinkedList<String> cartas;

    public correo(String email,String carta_empresa) {
        this.direccion = email;
        this.cartas= new LinkedList<>();
        cartas.add(carta_empresa);
    }
    
    
    
    
}

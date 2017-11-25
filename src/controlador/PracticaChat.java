/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.net.InetAddress;
import vista.VentanaPrincipal;

/**
 *
 * @author 9fdam02
 */
public class PracticaChat {

    public static final String FIN = "FIN";
    public static final String FIN_CLIENTE = "FIN CLIENTE";
    public static final String IP_DIFUSION= "224.0.1.1"; 
    public static final int PUERTO_DIFUSION =50000;
    
    static VentanaPrincipal v;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Preferencia por ipv4 si no da error en macos con ip multicast
        System.setProperty("java.net.preferIPv4Stack", "true");
        if (v == null) {
            v = new VentanaPrincipal();
            v.setVisible(true);
        }
    }

}

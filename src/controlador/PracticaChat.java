/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import vista.VentanaPrincipal;

/**
 *
 * @author 9fdam02
 */
public class PracticaChat {

    public static final String FIN = "FIN";
    public static final String FIN_CLIENTE = "FIN CLIENTE";
    public static final String IP_DIFUSION = "224.0.1.1";
    public static final int PUERTO_DIFUSION = 50000;
    public static final int PUERTO_SERV = 8001;

    static VentanaPrincipal v;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Preferencia por ipv4, si no da error en macos con ip multicast
        System.setProperty("java.net.preferIPv4Stack", "true");
        if (v == null) {
            v = new VentanaPrincipal();
            v.setVisible(true);
        }

        //Cargamos fuente para emojis
        try {
            GraphicsEnvironment ge
                    = GraphicsEnvironment.getLocalGraphicsEnvironment();
            InputStream is = PracticaChat.class.getClassLoader().getResourceAsStream("Symbola.ttf");//lo cargamos como stream ya que va a estar dentro del jar
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, is));
        } catch (IOException | FontFormatException e) {
            System.out.println("Archivo no encontrado");
        }
      
        //Charset del sistema  windows no utf 8 
         System.out.println(Charset.defaultCharset().name());
    }

}

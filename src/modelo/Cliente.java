/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.net.Socket;
import vista.VentanaCliente;

/**
 *
 * @author 9fdam02
 */
public class Cliente extends Thread{
    private Servidor s;
    String nick;
    boolean fin = false;
    VentanaCliente v;
 
    Socket socket;
    
    public Cliente(String nick, VentanaCliente v) {
        this.nick = nick;
        this.start();
    }

    @Override
    public void run() {
        v.escribirTextArea(String.format("Hola %s.", nick));
        //TODO decidir servidor
        v.escribirTextArea("Esperando listado servidores...");
        
        //TODO escuchar mensajes
        while (!fin) {
            
        }
    }
    
    public void mandarMensaje(String msj){
        if (socket!=null) {
            
        }
    }
}

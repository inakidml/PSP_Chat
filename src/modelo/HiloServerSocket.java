/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Servidor;

/**
 *
 * @author 9fdam02
 */
public class HiloServerSocket extends Thread {
    
    private Socket socket;
    private Servidor s;
    private String nick;
    private PrintWriter out;
    private InetAddress ipCliente ;
    
    public HiloServerSocket(Socket socket, Servidor s) {
        this.socket = socket;
        this.s = s;
    }
    
    @Override
    public void run() {
        
        try {
            //primero saludar
            s.getV().escribirTextArea("cliente conectado");
            boolean fin = false;
            enviarMensaje("Hola cliente, ya estas conectado");
            InputStreamReader datosCliente = new InputStreamReader(socket.getInputStream());
            BufferedReader br = new BufferedReader(datosCliente);

            //responde con el nick
            nick = br.readLine();
            s.getV().escribirTextArea("Bienvenido: " + getNick());
            s.addNombreCliente(getNick(), this);
            ipCliente = socket.getInetAddress();
            //empieza el chat
            s.getV().refrescarJTable();
            String texto;
            while (!fin) {
                texto = br.readLine();
                s.getV().escribirTextArea(getNick() + ": " + socket.getInetAddress());
                s.getV().escribirTextArea("mensaje: " + texto);
                distribuirMensaje(getNick() + ": " + texto);
            }
            
            //cerramos todo
            br.close();
            out.close();
            datosCliente.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Fallo al cerrar");
        }
    }
    
    public void enviarMensaje(String msj) {
        if (socket != null) {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(msj);
            } catch (IOException ex) {
                Logger.getLogger(HiloServerSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void distribuirMensaje(String msj) {
        for (HiloServerSocket hilo : s.getHilosRx()) {
            hilo.enviarMensaje(msj);
        }
    }

    /**
     * @return the nick
     */
    public String getNick() {
        return nick;
    }

    /**
     * @return the ipCliente
     */
    public InetAddress getIpCliente() {
        return ipCliente;
    }

    @Override
    public String toString() {
        return "HiloServerSocket{" + "socket=" + socket + ", nick=" + nick + ", ipCliente=" + ipCliente + '}';
    }
    
}

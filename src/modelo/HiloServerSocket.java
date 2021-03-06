/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.PracticaChat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author 9fdam02
 */
public class HiloServerSocket extends Thread {
    
    private Socket socket;
    private Servidor s;
    private String nick;
    private PrintWriter out;
    private InetAddress ipCliente;
    private boolean fin = false;
    
    public HiloServerSocket(Socket socket, Servidor s) {
        this.socket = socket;
        this.s = s;
    }
    
    @Override
    public void run() {
//hilo que se crea en Servidor con cada conexión
        try {
//ya tenemos un socket, creamos streams
            InputStreamReader datosCliente = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            s.getV().escribirTextArea("cliente conectado");
            //saludamos
            enviarMensaje("Servidor: Ya estas conectado.");
            BufferedReader br = new BufferedReader(datosCliente);
            //esperamos respuesta con el nick
            String provNick = br.readLine();
            
            nick = comprobarNick(provNick);
            enviarMensaje("Servidor: Su nick será " + nick);
            s.getV().escribirTextArea(getNick() + " se ha incorporado al chat");
            ipCliente = socket.getInetAddress();//guardamos la ip
            //empieza el chat
            s.getV().refrescarJTable();//actualizamos tabla de la ventana

            //empieza el chat
            String texto;
            while (!fin) {
                texto = br.readLine();
                s.getV().escribirTextArea(getNick() + ": " + socket.getInetAddress());
                s.getV().escribirTextArea("mensaje: " + texto);

                //analizmaos el mensaje
                if (texto.trim().equals(PracticaChat.FIN)) {
                    distribuirMensaje(PracticaChat.FIN);
                    s.removeHilo(this);
                    s.getV().refrescarJTable();
                    fin = true;
                    s.getV().refrescarJTable();
                    s.getV().escribirTextArea(nick + " ha sido desconectado del chat");
                } else if (texto.trim().equals(PracticaChat.FIN_CLIENTE)) {
                    enviarMensaje(PracticaChat.FIN_CLIENTE);
                    s.getV().escribirTextArea(nick + " ha abndonado el chat");
                    distribuirMensaje(nick + " ha abandonado el chat");
                    s.removeHilo(this);
                    s.getV().refrescarJTable();
                    fin = true;
                } else {//si no recibimos fin, propagamos el mensaje
                    distribuirMensaje(getNick() + ": " + texto);
                }
            }//fin while

            //cerramos todo
            br.close();
            out.close();
            datosCliente.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Perdida de conexión");
        }
        System.out.println("HiloServerSocket fin");
    }
    
    public void enviarMensaje(String msj) {
        if (socket != null) {
            out.println(msj);
        }
    }
    
    private void distribuirMensaje(String msj) {
        //enviamos mensaje a todos los clientes del servidor
        for (HiloServerSocket hilo : s.getHilosRx()) {
            hilo.enviarMensaje(msj);
        }
    }
    
    private String comprobarNick(String nick) {
        // recursiva para buscar nicks existentes 
        boolean existe = false;
        for (HiloServerSocket hilo : s.getHilosRx()) {
            if (nick.equals(hilo.getNick())) {
                nick += "_" + nick;
            }
        }
        if (existe) {//si existe lo cambiamos y vlvemos a llamar a la funci'on
            return comprobarNick(nick);
        } else {//si no existe devolvemos el nombre
            return nick;
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
}

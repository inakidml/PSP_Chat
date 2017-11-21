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
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Servidor;

/**
 *
 * @author 9fdam02
 */
class HiloServerSocket extends Thread {

    private Socket socket;
    private Servidor s;
    private String nick;

    public HiloServerSocket(Socket socket, Servidor s) {
        this.socket = socket;
        this.s = s;
    }

    @Override
    public void run() {
        PrintWriter out = null;
        try {
            s.getV().escribirTextArea("cliente conectado");
            boolean fin = false;
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Hola cliente, ya estas conectado");
            InputStreamReader datosCliente = new InputStreamReader(socket.getInputStream());
            BufferedReader br = new BufferedReader(datosCliente);

            nick = br.readLine();
            s.getV().escribirTextArea("Bienvenido: " + nick);
            
            s.addNombreCliente(nick, this);
            String texto;
            while (!fin) {
                texto = br.readLine();
                s.getV().escribirTextArea(nick + ": " + socket.getInetAddress());              
                s.getV().escribirTextArea("mensaje: " + texto);
                //TODO enviar mensajes a todos
//   out.println("un mensaje de vuelta");

            }
            br.close();
            out.close();
            datosCliente.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(HiloServerSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

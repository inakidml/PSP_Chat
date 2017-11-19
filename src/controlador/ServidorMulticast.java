/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Servidor;

/**
 *
 * @author inaki
 */
public class ServidorMulticast extends Thread {

    private static List<String> servidores;
    MulticastSocket ms;
    int puerto;
    InetAddress grupo;
    DatagramPacket dp;
    boolean fin = false;

    public ServidorMulticast() {

        servidores = new ArrayList<>();
        try {
            ms = new MulticastSocket();
        } catch (IOException ex) {
            System.out.println("IOException al instanciar Multicast");;
        }
        puerto = 8000;
        try {
            grupo = InetAddress.getByName("224.0.1.1");
        } catch (UnknownHostException ex) {
            System.out.println("Direccion multicast no válida");;
        }

    }

    @Override
    public void run() {
        difusionServidores();
    }

    private void difusionServidores() {

        while (!fin) {
            String msj = crearMensaje();
            dp = new DatagramPacket(msj.getBytes(), msj.length(), grupo, puerto);
            try {
                ms.send(dp);
            } catch (IOException ex) {
                System.out.println("IOExdception al enviar datgrama en hilo difusión");;
            }
            try {
                sleep(2000);
            } catch (InterruptedException ex) {
                System.out.println("Interrupcion con el sleep del hilo difusión");;
            }
        }

    }

    private String crearMensaje() {
        String msj = "";
        for (String servidor : servidores) {
            if (!msj.equals("")) {
                msj += "," + servidor;
            } else {
                msj = servidor;
            }
        }
        return msj;
    }

    public static void addServidor(Servidor s) {
        String ip = s.getIp().getHostAddress();
        String serv = ip + ":" + s.getPuerto() + ":" + s.getTema();
        servidores.add(serv);
    }

    public void terminarServidor() {
        fin = true;
    }

}

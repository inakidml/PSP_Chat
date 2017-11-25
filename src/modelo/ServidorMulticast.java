/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.PracticaChat;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
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
    InetAddress grupo;
    DatagramPacket dp;
    boolean fin = false;
    private int contador = 0;

    public ServidorMulticast() {

        servidores = new ArrayList<>();
        try {
            ms = new MulticastSocket();
            ms.setTimeToLive(128);

        } catch (IOException ex) {
            System.out.println("IOException al instanciar Multicast");;
        }
        try {
//            try {
//                ms.setNetworkInterface(NetworkInterface.getByInetAddress(
//                        InetAddress.getByName("0.0.0.0")));
//            } catch (SocketException ex) {
//                Logger.getLogger(ServidorMulticast.class.getName()).log(Level.SEVERE, null, ex);
//            }
            grupo = InetAddress.getByName(PracticaChat.IP_DIFUSION);
        } catch (UnknownHostException ex) {
            System.out.println("Direccion multicast no válida");
        }

    }

    @Override
    public void run() {
        contador++;
        this.setName("HiloServDifusion-" + contador);
        difusionServidores();
    }

    private void difusionServidores() {

        while (!fin) {
            String msj = crearMensaje();
            dp = new DatagramPacket(msj.getBytes(), msj.length(), grupo, PracticaChat.PUERTO_DIFUSION);
            try {
                ms.send(dp);
            } catch (IOException ex) {
                System.out.println("IOExdception al enviar datagrama en hilo difusión");;
            }
            try {
                sleep(2000);
            } catch (InterruptedException ex) {
                System.out.println("Interrupcion con el sleep del hilo difusión");;
            }
        }
        System.out.println("fin " + this);
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

    public static void removeServidor(Servidor s) {
        String ip = s.getIp().getHostAddress();
        String serv = ip + ":" + s.getPuerto() + ":" + s.getTema();
        String sBorrar = null;
        for (String servidor : servidores) {
            if (servidor.equals(serv)) {
                sBorrar = servidor;
            }
        }
        if (sBorrar != null) {
            servidores.remove(sBorrar);
        }
    }

    public void terminarServidor() {
        fin = true;
    }

}

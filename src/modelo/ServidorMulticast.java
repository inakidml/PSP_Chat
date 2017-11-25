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
    private MulticastSocket ms;
    private InetAddress grupo;
    private DatagramPacket dp;
    boolean fin = false;
    private int contador = 0;

    public ServidorMulticast() {
//envia a una dirección multicast la lista de todos los servidores que se conecten
        servidores = new ArrayList<>();
        try {
            //creamos multicast socket
            ms = new MulticastSocket();
            //cambio TTL por pruebas en egibide
            ms.setTimeToLive(128);

        } catch (IOException ex) {
            System.out.println("IOException al instanciar Multicast");;
        }
        try {
            grupo = InetAddress.getByName(PracticaChat.IP_DIFUSION);
        } catch (UnknownHostException ex) {
            System.out.println("Direccion multicast no válida");
        }

    }

    @Override
    public void run() {//lo instanciamos y arrancamos desde ventanaprincipal
        contador++;
        this.setName("HiloServDifusion-" + contador);
        difusionServidores();
    }

    private void difusionServidores() {
        while (!fin) {
            String msj = crearMensaje();//creamos lista de sevidores
            dp = new DatagramPacket(msj.getBytes(), msj.length(), grupo, PracticaChat.PUERTO_DIFUSION);
            try {
                ms.send(dp);//enviamos el mensaje
            } catch (IOException ex) {
                System.out.println("IOExdception al enviar datagrama en hilo difusión");;
            }
            try {
                sleep(2000);//esperamos un poco
            } catch (InterruptedException ex) {
                System.out.println("Interrupcion con el sleep del hilo difusión");;
            }
        }
        System.out.println("fin " + this);
    }//fin del while

    private String crearMensaje() {
        //Creamos un string con todos los servidores
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
        //cada servidor se subscribe a la lista con esta función
        String ip = s.getIp().getHostAddress();
        String serv = ip + ":" + s.getPuerto() + ":" + s.getTema();
        servidores.add(serv);
    }

    public static void removeServidor(Servidor s) {
        //al desconectarse los servidores se deben borrar
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

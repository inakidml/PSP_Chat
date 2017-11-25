/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.PracticaChat;
import modelo.Sala;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Servidor;
import vista.VentanaPrincipal;

/**
 *
 * @author inaki
 */
public class HiloRecibirMulticast extends Thread {

    private static boolean ServPresent = false;
    private static MulticastSocket ms = null;
    private static InetAddress grupo = null;
    private static HiloRecibirMulticast h = null;
    private static boolean fin = false;
    private static List<Sala> servidores;
    private static int ultimoServ;
    private static List<HiloRecibirMulticast> hilos;
    private static int contador = 0;
    //hilo que recibe una lista de servidores por multicast

    public HiloRecibirMulticast() {
//nos registramos al grupo de multicast
        //
        hilos = new ArrayList<>();
        try {
            ms = new MulticastSocket(PracticaChat.PUERTO_DIFUSION);
            ms.setSoTimeout(3000);//timeout para poder hacer cierre ordenado
        } catch (IOException ex) {
            System.out.println("IOException en VPrincipal, buscando servidor difusión");;
        }
        try {
            grupo = InetAddress.getByName(PracticaChat.IP_DIFUSION);
        } catch (UnknownHostException ex) {
            Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (ms != null && grupo != null) {
            try {
                ms.joinGroup(grupo);
            } catch (IOException ex) {
                Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void run() {

        String msj;
        while (!fin) {
            byte[] buf = new byte[1024];
            DatagramPacket recibido = new DatagramPacket(buf, buf.length);
            try {
                ms.receive(recibido);
            } catch (SocketTimeoutException e) {
                System.out.println("Se ha excedido el tiempo de espera");
            } catch (IOException ex) {
                System.out.println("Servidor desconectado");
            }
            msj = new String(recibido.getData());
            procesarPaquete(msj);
            ServPresent = true;//cambiamos variable a true para indicar que existe un servidor de difusión
            buf = null;//limpiamos el buffer
        }
        try {
            ms.leaveGroup(grupo);//abanonamos el grupo
        } catch (IOException ex) {
            System.out.println("Servidor desconectado");
        }
        ms.close();//cerramos

    }

    /**
     * @return the ServPresent
     */
    public static boolean isServPresent() {
        //función estatica si no existe el hilo lo arranca y devuelve si hay servidorDifusión
        if (h == null) {
            arrancarHilo();
            try {
                sleep(2100);
            } catch (InterruptedException ex) {
                Logger.getLogger(HiloRecibirMulticast.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ServPresent;
    }

    private static void arrancarHilo() {
        HiloRecibirMulticast h = new HiloRecibirMulticast();
        hilos.add(h);
        contador++;
        h.setName("HiloRxDifusion-" + contador);
        h.start();
    }

    public static void terminarRxDifusion() {
        fin = true;
    }

    private void procesarPaquete(String s) {
        //convertimos el mensaje en servidor puerto y tema
        if (!s.trim().equals("")) {
            servidores = new ArrayList<>();
            String[] ss = s.split(",");//separamos los servidores
            for (String serv : ss) {
                String[] datos = serv.split(":"); //separamos ip:puerto:tema
                try {
                    ultimoServ++;
                    Sala sala = new Sala(ultimoServ, InetAddress.getByName(datos[0]), Integer.parseInt(datos[1]), datos[2]);
                    servidores.add(sala);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(HiloRecibirMulticast.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            servidores = null;
        }
    }

    /**
     * @return the servidores
     */
    public static List<Sala> getServidores() {
        //si desde un cliente quieren los servidores y no esta arrancado este hilo lo arrancamos y devuelve serv
        if (h == null) {
            arrancarHilo();
        }
        return servidores;
    }
}

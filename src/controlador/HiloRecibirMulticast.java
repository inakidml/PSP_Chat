/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

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

    public HiloRecibirMulticast() {
        hilos = new ArrayList<>();
        try {
            ms = new MulticastSocket(PracticaChat.PUERTO_DIFUSION);
            ms.setSoTimeout(3000);
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
            ServPresent = true;
            buf = null;

        }
        try {
            ms.leaveGroup(grupo);
        } catch (IOException ex) {
            System.out.println("Servidor desconectado");
        }
        ms.close();

    }

    /**
     * @return the ServPresent
     */
    public static boolean isServPresent() {
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
//no lo uso, al final timeout de multicastsocket -> ms.setSoTimeout(3000);
//        HiloMataHilos h = new HiloMataHilos(hilos);
//        h.start();
    }

    private void procesarPaquete(String s) {
        if (!s.trim().equals("")) {
            servidores = new ArrayList<>();
            String servidor;
            String puerto;
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
        if (h == null) {
            arrancarHilo();
        }
        return servidores;
    }
}

//No usado
class HiloMataHilos extends Thread {

    private static List<HiloRecibirMulticast> hilos;

    public HiloMataHilos(List<HiloRecibirMulticast> hilos) {
        this.hilos = hilos;
        hilos = new ArrayList<>();
    }

    @Override
    public void run() {
        this.setName("HiloMataHilos");
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        System.out.println(threadArray.length);
        for (Thread thread : threadArray) {
            System.out.println(thread);
        }

        for (HiloRecibirMulticast hilo : hilos) {
            System.out.println("Hilo a matar");
            System.out.println(hilo);
            hilo.interrupt();
        }
        System.out.println("fin " + this);
    }

}

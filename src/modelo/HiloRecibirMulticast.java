/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public HiloRecibirMulticast() {

        try {
            ms = new MulticastSocket(8000);
        } catch (IOException ex) {
            System.out.println("IOException en VPrincipal, buscando servidor difusión");;
        }

        try {
            grupo = InetAddress.getByName("224.0.1.1");
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
        byte[] buf = new byte[1024];
        String msj;
        while (!fin) {

            DatagramPacket recibido = new DatagramPacket(buf, buf.length);

            try {
                ms.receive(recibido);
                msj = new String(recibido.getData());
                procesarPaquete(msj);
                ServPresent = true;
                System.out.println(ServPresent);
            } catch (IOException ex) {
                Logger.getLogger(HiloRecibirMulticast.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        try {
            ms.leaveGroup(grupo);
        } catch (IOException ex) {
            Logger.getLogger(HiloRecibirMulticast.class.getName()).log(Level.SEVERE, null, ex);
        }
        ms.close();

    }

    /**
     * @return the ServPresent
     */
    public static boolean isServPresent() {
        if (h == null) {
            arrancarHilo();
        }
        try {
            sleep(2100);
        } catch (InterruptedException ex) {
            Logger.getLogger(HiloRecibirMulticast.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ServPresent;

    }

    private static void arrancarHilo() {

        HiloRecibirMulticast h = new HiloRecibirMulticast();
        h.start();

    }

    public static void terminarServ() {
        fin = true;
    }

    private void procesarPaquete(String s) {
        if (!s.trim().equals("")) {
            String servidor;
            String puerto;
            String[] ss = s.split(",");//separamos los servidores
            for (String serv : ss) {
                String[] datos = serv.split(":"); //separamos ip:puerto:tema
                try {
                    Sala sala = new Sala(InetAddress.getByName(datos[0]), Integer.parseInt(datos[1]), datos[2]);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(HiloRecibirMulticast.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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

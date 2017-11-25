/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.PracticaChat;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vista.VentanaCliente;
import vista.VentanaServidor;

/**
 *
 * @author 9fdam02
 */
public class Servidor extends Thread {

    /**
     * @return the v
     */
    public VentanaServidor getV() {
        return v;
    }

    private InetAddress ip;
    private int puerto = -1;
    private String tema;
    boolean fin = false;

    private VentanaServidor v;
    private List<HiloServerSocket> hilosRx;
    private Map<String, HiloServerSocket> mapHilos; // conexiones de clientes ordenadas por nick

    public Servidor(String tema, VentanaServidor v) {

        hilosRx = new ArrayList<>();
        mapHilos = new HashMap();
        try {
            this.ip = InetAddress.getLocalHost();

        } catch (UnknownHostException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.puerto = getPuerto();
        this.tema = tema;
        this.v = v;
        this.start();
    }

    @Override
    public void run() {
        getV().escribirTextArea("Recuperando ip servidor: " + ip + ":" + puerto);

        //TODO conseguir ip real y seleccionar tarjeta
//        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
//        for (; n.hasMoreElements();) {
//            NetworkInterface e = n.nextElement();
//            v.escribirTextArea("Interface: " + e.getName());
//            Enumeration<InetAddress> a = e.getInetAddresses();
//            for (; a.hasMoreElements();) {
//                InetAddress addr = a.nextElement();
//                v.escribirTextArea("  " + addr.getHostAddress());
//            }
//        }
        getV().escribirTextArea("Servidor creado.");
        ServidorMulticast.addServidor(this);
        getV().escribirTextArea("Servidor añadido a lista de difusión.");
        getV().escribirTextArea("Creando ServerSocket");
        ServerSocket sc = null;
        try {
            sc = new ServerSocket(puerto);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        getV().escribirTextArea("Esperando conexión");
        try {
            sc.setSoTimeout(2000);
        } catch (SocketException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (!fin) {
            Socket socket = null;
            try {
                socket = sc.accept();
            } catch (IOException ex) {
                //System.out.println("esperando recibir socket");
            }
            if (socket != null) {
                HiloServerSocket h = new HiloServerSocket(socket, this);
                getHilosRx().add(h);
                h.start();
            }

        }
        v.escribirTextArea("Servidor terminado");

    }

    public void addNombreCliente(String s, HiloServerSocket h) {
        getMapHilos().put(s, h);
    }

    public void removeHilo(HiloServerSocket h) {
        getHilosRx().remove(h);
    }

    public int getPuerto() {
        if (puerto != -1) {
            return puerto;
        } else {
            puerto = -1;
            List<Sala> servidores = HiloRecibirMulticast.getServidores();
            if (servidores != null) {
                for (Sala servidor : servidores) {
                    if (servidor.getPuerto() > puerto) {
                        puerto = servidor.getPuerto();
                    }
                }
                return puerto + 1;
            } else {
                return 8001;
            }
        }
    }

    /**
     * @return the ip
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     * @return the tema
     */
    public String getTema() {
        return tema;
    }

    /**
     * @return the hilosRx
     */
    public List<HiloServerSocket> getHilosRx() {
        return hilosRx;
    }

    /**
     * @return the mapHilos
     */
    public Map<String, HiloServerSocket> getMapHilos() {
        return mapHilos;
    }

    public void terminarServidor() {
        fin = true;
        ServidorMulticast.removeServidor(this);
        desconectarClientes();

    }

    public void desconectarClientes() {
        if (hilosRx.size() > 0) {
            hilosRx.get(0).enviarMensaje(PracticaChat.FIN);
        }
    }
}

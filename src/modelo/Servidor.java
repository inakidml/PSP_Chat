/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.PracticaChat;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public Servidor(String tema, VentanaServidor v) {

        hilosRx = new ArrayList<>();

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

        getV().escribirTextArea("Recuperando datos del servidor: " + ip.getHostAddress() + ":" + puerto);

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
        //añadimos servidor a la lista de difusión
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
            sc.setSoTimeout(2000);//timeout para poder acabar bien
        } catch (SocketException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (!fin) {
            Socket socket = null;
            try {
                socket = sc.accept();
            } catch (IOException ex) {
                //cada vez que pasa timeout salta exceptiom
                //System.out.println("esperando recibir socket");
            }
            //hemos recivido una conexión y creamos un hilo para el cliente
            if (socket != null) {
                HiloServerSocket h = new HiloServerSocket(socket, this);
                getHilosRx().add(h);
                h.start();
            }

        }
        v.escribirTextArea("Servidor terminado");

    }

    public void removeHilo(HiloServerSocket h) {
        //el hilo de un cliente ha termiando y lo borramos de la lista
        getHilosRx().remove(h);
    }

    public int getPuerto() {
        //sebusca un puerto nuevo
        if (puerto != -1) {
            return puerto;
        } else {
            puerto = -1;
            List<Sala> servidores = HiloRecibirMulticast.getServidores();
            //busca el puerto mayor
            if (servidores != null) {
                for (Sala servidor : servidores) {
                    if (servidor.getPuerto() > puerto) {
                        puerto = servidor.getPuerto();
                    }
                }
                return puerto + 1;
            } else {
                return PracticaChat.PUERTO_SERV;//empezamos a contar desde la constante
            }
        }
    }

    public void terminarServidor() {
        //fin, borrar servidor de lista y desconectar clientes
        fin = true;
        ServidorMulticast.removeServidor(this);
        desconectarClientes();

    }

    public void desconectarClientes() {
        //desconecta todos los hilos
        if (hilosRx.size() > 0) {
            hilosRx.get(0).enviarMensaje(PracticaChat.FIN);
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
}

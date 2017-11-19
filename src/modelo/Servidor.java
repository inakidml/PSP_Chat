/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.HiloEscuchaServ;
import controlador.HiloRecibirMulticast;
import controlador.ServidorMulticast;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vista.VentanaCliente;
import vista.VentanaServidor;

/**
 *
 * @author 9fdam02
 */
public class Servidor {

    private List<Cliente> clientes;
    private InetAddress ip;
    private int puerto = -1;
    private String tema;
    boolean fin = false;
    HiloEscuchaServ h;
    VentanaServidor v;

    public Servidor(String tema, VentanaServidor v) {
        clientes = new ArrayList<>();
        try {
            this.ip = InetAddress.getLocalHost();

        } catch (UnknownHostException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.puerto = getPuerto();
        this.tema = tema;
        this.v = v;
        v.escribirTextArea("Recuperando ip servidor: " + ip);

        //TODO conseguir ip real 
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

        v.escribirTextArea("Servidor creado.");
        ServidorMulticast.addServidor(this);
        v.escribirTextArea("Servidor añadido a lista de difusión.");
    }

    public void addCliente(Cliente c) {
        getClientes().add(c);
    }

    /**
     * @return the clientes
     */
    public List<Cliente> getClientes() {
        return clientes;
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
     * @param clientes the clientes to set
     */
    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
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

}

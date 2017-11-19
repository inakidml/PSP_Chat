/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.HiloEscuchaServ;
import controlador.HiloRecibirMulticast;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
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

    public Servidor(InetAddress ip, String tema, VentanaServidor v) {
        clientes = new ArrayList<>();
        this.ip = ip;
        this.puerto = getPuerto();
        this.tema = tema;
        h = new HiloEscuchaServ(this);
        this.v = v;
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

}

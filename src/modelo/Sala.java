/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.net.InetAddress;

/**
 *
 * @author inaki
 */
public class Sala {
    private InetAddress ip;
    private int puerto;
    private String tema;

    public Sala(InetAddress ip, int puerto, String tema) {
        this.ip = ip;
        this.puerto = puerto;
        this.tema = tema;
    }

    /**
     * @return the ip
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     * @return the puerto
     */
    public int getPuerto() {
        return puerto;
    }

    /**
     * @return the tema
     */
    public String getTema() {
        return tema;
    }
    
}

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

    public Sala(InetAddress ip, int pueto, String tea) {
        this.ip = ip;
        this.puerto = pueto;
        this.tema = tea;
    }

    /**
     * @return the ip
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     * @return the pueto
     */
    public int getPuerto() {
        return puerto;
    }

    /**
     * @return the tea
     */
    public String getTema() {
        return tema;
    }
    
}

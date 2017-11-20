/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.HiloRecibirMulticast;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vista.VentanaCliente;

/**
 *
 * @author 9fdam02
 */
public class Cliente extends Thread {

    private Servidor s;
    String nick;
    boolean fin = false;
    VentanaCliente v;
    int servSeleccionado = -1;

    Socket socket;

    public Cliente(String nick, VentanaCliente v) {
        this.nick = nick;
        this.v = v;
        this.start();
    }

    @Override
    public void run() {
        v.escribirTextArea(String.format("Hola %s.", nick));
        //TODO decidir servidor
        v.escribirTextArea("Esperando listado servidores...");
        List<Sala> salas = null;

        do {
            salas = HiloRecibirMulticast.getServidores();
            System.out.println("salas: " + salas);
            try {
                sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (salas == null);
        int cont = 1;
        for (Sala sala : salas) {
            v.escribirTextArea(cont + ": " + sala.getTema());
            cont++;
        }
        v.escribirTextArea("Seleccione la sala: ");
        while (servSeleccionado <= 0 || servSeleccionado > salas.size()) {
            try {
                sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //TODO coenctar y escuchar mensajes, cambiar variable conectado de vcliente
        while (!fin) {
            v.escribirTextArea("bucle");
            v.escribirTextArea("" + servSeleccionado);
            Sala salaSelec = salas.get(servSeleccionado-1);
            v.escribirTextArea(salaSelec.getIp() + ": " + salaSelec.getPuerto() + salaSelec.getTema());
            try {
                sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void mandarMensaje(String msj) {
        if (socket != null) {

        }
    }

    public void seleccionarSala(String s) {
        int num = Integer.parseInt(s);
        servSeleccionado = num;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.HiloRecibirMulticast;
import controlador.PracticaChat;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
    InputStreamReader datosCliente = null;
    Socket socket;
    PrintWriter out = null;

    public Cliente(String nick, VentanaCliente v) {
        this.nick = nick;
        this.v = v;
        this.start();

    }

    @Override
    public void run() {
        try {
            this.setName("HiloCLiente");
            v.escribirTextArea(String.format("Hola %s.", nick));
            //TODO decidir servidor
            v.escribirTextArea("Esperando listado servidores...");
            List<Sala> salas = null;
            int contador = 0;
            do {
                salas = HiloRecibirMulticast.getServidores();
                try {
                    sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
                contador++;

            } while (salas == null && contador < 5);

            if (salas == null) {//no hay servidores cerca
                v.escribirTextArea("No se ha encontrado lista de servidores, pasamos a modo manual.");
                conectarManual();
            } else {
                int cont = 1;
                v.escribirTextArea("0: Conexión manual.");
                for (Sala sala : salas) {
                    v.escribirTextArea(cont + ": " + sala.getTema());
                    cont++;
                }
                v.escribirTextArea("Seleccione la sala: ");
                while (servSeleccionado < 0 || servSeleccionado > salas.size()) {

                    try {
                        sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                if (servSeleccionado != 0) {
                    v.escribirTextArea("" + servSeleccionado);
                    Sala salaSelec = salas.get(servSeleccionado - 1);
                    v.escribirTextArea(salaSelec.getIp() + ": " + salaSelec.getPuerto() + ":" + salaSelec.getTema());

                    socket = new Socket(salaSelec.getIp(), salaSelec.getPuerto());
                    datosCliente = new InputStreamReader(socket.getInputStream());
                } else {
                    conectarManual();
                }
            }

            BufferedReader br = new BufferedReader(datosCliente);
            String texto = null;
            texto = br.readLine();
            v.escribirTextArea(texto);//Hola cliente 
            v.setConectado(true);
            mandarMensaje(nick);

            while (!fin) {
                //recibiendo mensajes
                texto = br.readLine();

                if (texto.trim().equals(PracticaChat.FIN_CLIENTE)) {
                    fin = true;
                } else if (texto.trim().equals(PracticaChat.FIN)) {
                    mandarMensaje(PracticaChat.FIN);
                    v.escribirTextArea("El servidor envio desconectar");
                    fin = true;
                } else {
                    v.escribirTextArea(texto);
                }
            }

            br.close();
            out.close();
            socket.close();

        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(
                "Cliente fin");
    }

    private void conectarManual() throws IOException {
        boolean ok = false;
        do {
            try {
                String ip = JOptionPane.showInputDialog("Introduzca la dirección ip: ");
                int puerto = Integer.parseInt(JOptionPane.showInputDialog("Introduzca el puerto: "));
                socket = new Socket(ip, puerto);
                ok = true;
            } catch (HeadlessException headlessException) {
            } catch (NumberFormatException numberFormatException) {
                v.escribirTextArea("Formato incorrecto");
            } catch (IOException iOException) {
                v.escribirTextArea("Host no es valido");
            }
        } while (!ok);
        datosCliente = new InputStreamReader(socket.getInputStream());
    }

    public void mandarMensaje(String msj) {
        if (socket != null && !socket.isClosed()) {

            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(msj);

            } catch (IOException ex) {
                Logger.getLogger(Cliente.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    public void seleccionarSala(String s) {//se llama desde la ventana, para seleccionar la sala
        try {
            int num = Integer.parseInt(s);
            servSeleccionado = num;
        } catch (NumberFormatException ex) {
            System.out.println("no es un número");
        }
    }

    public void desconectarCLiente() {
        mandarMensaje(PracticaChat.FIN_CLIENTE);
    }

}

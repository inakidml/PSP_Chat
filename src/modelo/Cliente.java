/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.PracticaChat;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
    private String nick;
    private boolean fin = false;
    private VentanaCliente v;
    private int servSeleccionado = -1;
    private InputStreamReader datosCliente = null;
    private Socket socket;
    private PrintWriter out = null;

    public Cliente(String nick, VentanaCliente v) {
        //Constructor y arranca hilo
        this.nick = nick;
        this.v = v;
        this.start();

    }

    @Override
    public void run() {
        try {
            //renombramos hilo para debug
            this.setName("HiloCLiente");
            v.escribirTextArea(String.format("Hola %s.", nick));
            v.escribirTextArea("Esperando listado servidores...");
            List<Sala> salas = null;
            //esperamos la lista de servidores, o 5 vueltas
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

            if (salas == null) {//no lista de servidores
                v.escribirTextArea("No se ha encontrado lista de servidores, pasamos a modo manual.");
                conectarManual();
            } else {//si hay lista
                int cont = 1;
                v.escribirTextArea("0: Conexión manual.");
                for (Sala sala : salas) {
                    v.escribirTextArea(cont + ": " + sala.getTema());
                    cont++;
                }
                v.escribirTextArea("Seleccione la sala: ");
                while (servSeleccionado < 0 || servSeleccionado > salas.size()) {//validamos dato, servSeleccionado se cambia con seleccionarSala() desde ventana
                    try {
                        sleep(500);//Esperamos para que el usuario elija y no de vueltas como loco.
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //servidor o conexión manual
                if (servSeleccionado != 0) {
                    v.escribirTextArea("" + servSeleccionado);
                    Sala salaSelec = salas.get(servSeleccionado - 1);
                    v.escribirTextArea(salaSelec.getIp() + ": " + salaSelec.getPuerto() + ":" + salaSelec.getTema());
                    //creamos conexión
                    socket = new Socket(salaSelec.getIp(), salaSelec.getPuerto());
                    datosCliente = new InputStreamReader(socket.getInputStream());
                } else {
                    conectarManual();
                }
            }
            //Primero recibimos un saludo
            BufferedReader br = new BufferedReader(datosCliente);
            String texto = null;
            //esperamos a recibir saludo
            texto = br.readLine();
            v.escribirTextArea(texto);//Hola cliente 
            v.setConectado(true);//cambiamos flag en la ventana
            //servidor espera nuestro nick
            mandarMensaje(nick);
            //empieza el chat
            while (!fin) {
                //recibiendo mensajes
                texto = br.readLine();

                if (texto.trim().equals(PracticaChat.FIN_CLIENTE)) {//si desconectamos el cliente
                    fin = true;
                } else if (texto.trim().equals(PracticaChat.FIN)) {//se desconectan todos por el servidor
                    mandarMensaje(PracticaChat.FIN);
                    v.escribirTextArea("El servidor envio desconectar");
                    v.escribirTextArea("Cliente sin conexión, pulse salir");
                    fin = true;
                } else {//si no es ninguno de los finales mostramos el mensaje
                    v.escribirTextArea(texto);
                }

            }//fin del while

//fin cerramos todo
            br.close();
            out.close();
            socket.close();

        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Cliente fin");
    }

    private void conectarManual() throws IOException {
        //creamos conexión con datos del cliente
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
        //función para mandar mensajes, la llamamos desde la ventana
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
        // al salir desconectamos el cliente
        mandarMensaje(PracticaChat.FIN_CLIENTE);
    }

}

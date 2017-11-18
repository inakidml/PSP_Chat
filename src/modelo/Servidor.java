/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.ArrayList;
import java.util.List;
import vista.VentanaCliente;

/**
 *
 * @author 9fdam02
 */
public class Servidor {
    private List<VentanaCliente> vClientes;

    /**
     * @return the vClientes
     */
    public List<VentanaCliente> getvClientes() {
        return vClientes;
    }

    /**
     * @param vClientes the vClientes to set
     */
    public void setvClientes(List<VentanaCliente> vClientes) {
        this.vClientes = vClientes;
    }

    public Servidor() {    
        vClientes = new ArrayList<>();
    }
    
}

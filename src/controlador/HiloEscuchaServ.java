/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import modelo.Servidor;

/**
 *
 * @author txiki
 */
public class HiloEscuchaServ extends Thread{
private Servidor s;
    public HiloEscuchaServ(Servidor s) {
        this.s = s;
    }

    @Override
    public void run() {
       
    }
    
}

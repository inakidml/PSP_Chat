/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import vista.VentanaPrincipal;

/**
 *
 * @author 9fdam02
 */
public class PracticaChat {

    static VentanaPrincipal v;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (v == null) {
            v = new VentanaPrincipal();
            v.setVisible(true);
        }
    }

}
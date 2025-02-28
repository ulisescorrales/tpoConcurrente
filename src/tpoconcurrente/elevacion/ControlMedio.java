/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpoconcurrente.elevacion;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ulisescorrales
 */
public class ControlMedio extends Thread {

    private final MedioElevacion medio;

    public ControlMedio(MedioElevacion medio) {
        this.medio = medio;
    }

    public void run() {
        while (true) {
            try {
                medio.permitirUsarMolinetes();
                medio.ascender();
                Thread.sleep(3000);
                medio.llegarArriba();
                medio.descender();
                Thread.sleep(1000);
                medio.llegarAbajo();

            } catch (InterruptedException ex) {
                Logger.getLogger(ControlMedio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

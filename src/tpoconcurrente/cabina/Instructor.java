/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpoconcurrente.cabina;

import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author ulisescorrales
 */
public class Instructor extends Thread{
    private final CabinaInstructor cabina;

    public Instructor(CabinaInstructor cabina,int numInstructor) {
		super("Instructor Nº "+numInstructor);
        this.cabina = cabina;
    }
    
    public void run(){
		Clase clase;
        while(true){
            try {
                clase=cabina.comenzarEnseniar();
                Thread.sleep(5000);
                cabina.terminarEnseniar(clase);
            } catch (InterruptedException ex) {
                Logger.getLogger(Instructor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

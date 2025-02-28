/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpoconcurrente.confiteria;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ulisescorrales
 */
public class Cocinero extends Thread{
    private final Confiteria confiteria;
   
	public Cocinero(Confiteria confiteria,int numCocinero){
		super("Cocinero Nº "+numCocinero);
		this.confiteria=confiteria;
	}
    public void run(){
        Orden orden;
        Comida comida;
        while(true){
            try {
                //Toma órdenes y genera una instancia Comida para devolver
                orden=confiteria.tomarOrden();
                //Cocinar
                Thread.sleep(5000);
                comida=new Comida(orden.esPostre());
                confiteria.dejarEnMostrador(comida);
            } catch (InterruptedException ex) {
                Logger.getLogger(Cocinero.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

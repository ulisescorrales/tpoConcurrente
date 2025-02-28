/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tpoconcurrente.complejo;

/**
 *
 * @author ulisescorrales
 */
public class Reloj implements Runnable{
    Complejo complejo;

    public Reloj(Complejo complejo){
        this.complejo=complejo;
    }
    @Override
    public void run() {
        complejo.addHora();
    }
            
    
}

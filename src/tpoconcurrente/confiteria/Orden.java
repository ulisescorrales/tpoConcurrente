/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpoconcurrente.confiteria;

/**
 *
 * @author ulisescorrales
 */
public class Orden {
    private final boolean esPostre;

    public Orden(boolean esPostre) {
        this.esPostre = esPostre;
    }

    public boolean esPostre() {
        return esPostre;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpoconcurrente.elevacion;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author ulisescorrales
 */
public class Molinete{

    private final AtomicInteger contador = new AtomicInteger(0);
    private CyclicBarrier barrera;

    public Molinete(CyclicBarrier barrera) {
        this.barrera = barrera;
    }

    public void usarPase() throws BrokenBarrierException, InterruptedException {
        this.barrera.await();
		System.out.println(Thread.currentThread().getName()+" PASÓ LA BARRERA");
        this.contador.addAndGet(1);
    }

    public int getContador() {
        return contador.get();
    }

    public void resetContador() {
        this.contador.set(0);
    }

    public CyclicBarrier getCyclycBarrier() {
        return this.barrera;
    }

    public void setBarrera(CyclicBarrier barrera) {
        this.barrera = barrera;
    }
}

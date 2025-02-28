/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpoconcurrente.confiteria;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 *
 * @author ulises.corrales
 */
public class Confiteria {
    //Implementación con Semáforos

    private final int capacidad = 100;
    private int personasAdentro = 0;

    int capacidadMostradorComida = 40;
    private final ArrayBlockingQueue<Orden> ordenes = new ArrayBlockingQueue<Orden>(capacidad, true);
    private final ArrayBlockingQueue<Comida> comidasListas = new ArrayBlockingQueue<Comida>(20, true);
    private final ArrayBlockingQueue<Comida> comidasListas2 = new ArrayBlockingQueue<Comida>(capacidadMostradorComida / 2, true);
    private final ArrayBlockingQueue<Comida> postresListos = new ArrayBlockingQueue<Comida>(capacidadMostradorComida / 2, true);

    private final Semaphore puedePonerComida = new Semaphore(capacidadMostradorComida, true);
    private final Semaphore puedeSacarComida = new Semaphore(0, true);
    //Lock para la entrada y salida de personas
    private final Semaphore entrar = new Semaphore(100, true);
    private final Semaphore mutex = new Semaphore(1, true);

    public void entrar() {
        //Método para esquiador
        try {
            entrar.acquire();
            mutex.acquire();
            this.personasAdentro++;
            mutex.release();
            System.out.println(Thread.currentThread().getName() + " entró a la confitería");
        } catch (InterruptedException e) {
        }
        //Si la capacidad está llena, esperar
    }

    public Orden generarOrden(boolean esPostre) {
        //Método para esquiador, coloca una orden que toma el cocinero
        Orden nuevaOrden = new Orden(esPostre);
        System.out.println(Thread.currentThread().getName() + " genera orden");
        try {
            this.ordenes.put(nuevaOrden);
        } catch (InterruptedException e) {
        }
        return nuevaOrden;
    }

    public Orden tomarOrden() {
        //Método para cocinero, se queda esperando si la cola está vacía
        Orden orden = null;
        try {
            orden = this.ordenes.take(); //El cocinero se queda esperando hasta que exista una orden
        } catch (InterruptedException e) {
        }
        System.out.println(Thread.currentThread().getName() + " tomó una orden");
        return orden;
    }

    public void dejarEnMostrador(Comida comida) {
        //Método para concinero
        if (comida.esPostre()) {
            try {
                postresListos.put(comida);
            } catch (InterruptedException ex) {
                Logger.getLogger(Confiteria.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //Intentar colocar en un mostrador y si está lleno, usa el otro
            try {
                this.puedePonerComida.acquire();
                //Por lo menos hay un espacio en algún mostrador
                boolean exito = comidasListas.offer(comida);
                if (!exito) {
                    try {
                        comidasListas2.put(comida);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Confiteria.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println(Thread.currentThread().getName() + " dejó  la comida en mostrador");
                this.puedeSacarComida.release();
            } catch (InterruptedException ex) {
                Logger.getLogger(Confiteria.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Comida recogerComida(Orden orden) {
        //Método para el esquiador
        Comida retirar = null;
        if (orden.esPostre()) {
            retirar = retirarPostre(orden);
            System.out.println(Thread.currentThread().getName() + " recoge su postre");
        } else {
            retirar = retirarComida(orden);
            System.out.println(Thread.currentThread().getName() + " recoge su comida");
        }
        return retirar;
    }

    public Comida retirarPostre(Orden comida) {
        //Método para esquiador
        Comida retirar = null;
        try {
            retirar = postresListos.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(Confiteria.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retirar;
    }

    public Comida retirarComida(Orden comida) {
        //Método para esquiador
        Comida retirar = null;
        try {
            //Alguno de los dos mostradores tendrá platos para retirar
            this.puedeSacarComida.acquire();
            retirar = comidasListas.poll();
            if (retirar == null) {
                try {
                    retirar = comidasListas2.take();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Confiteria.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Confiteria.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.puedePonerComida.release();

        return retirar;
    }

    public void salir() {
        //Método para esquiador
        entrar.release();
        try {
            mutex.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Confiteria.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.personasAdentro--;
        System.out.println(Thread.currentThread().getName() + " sale de la confitería, personasAdentro: " + this.personasAdentro);
        mutex.release();
    }
}

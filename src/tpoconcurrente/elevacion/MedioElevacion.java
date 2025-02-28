/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpoconcurrente.elevacion;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

/**
 *
 * @author ulisescorrales
 */
public class MedioElevacion {
    //Objeto compartido entre el control del medio de elevación y el esquiador
    //Implentación con monitores

    private final int id;
    private final int capacidad;
    private int cantArriba;
    private boolean estaAbajo = true;
    private boolean estaArriba = false;

    private final Molinete[] molinetes;
    private int molineteAUsar = -1;

    private final Object entrada = new Object();//Objeto sincronizado para modificar this.molineteAUsar y reemplazar la barrera rota
    //private AtomicBoolean abierto=new AtomicBoolean(true);
    private boolean abierto = true;

    private long turnoActual = 0;
    private long ultimoTurno = 0;
    private boolean puedeUsarMolinete = true;
    private int cantUsandoMolinete = 0;

    public MedioElevacion(int capacidad, int id) {
        this.id = id;
        this.capacidad = capacidad;
        molinetes = new Molinete[capacidad];
        int i;
        CyclicBarrier barrera = new CyclicBarrier(this.capacidad);
        for (i = 0; i < capacidad; i++) {
            //Los molinetes de un mismo MedioElevacion comparten el CyclicBarrier
            molinetes[i] = new Molinete(barrera);
        }
    }

    public int verificarHabilitado() {
        //Método para esquiador que verifica si los molinetes están habilitados
        //Si está cerrado retorna -1, sino el número de  molinete a usar
        int retornar = -1;
        synchronized (entrada) {
            long tempTurno = this.ultimoTurno;
            this.ultimoTurno++;
            while ((this.abierto && !this.puedeUsarMolinete) || (this.abierto && tempTurno != this.turnoActual && cantUsandoMolinete >= this.capacidad)) {
                try {
                    entrada.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MedioElevacion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (this.abierto) {
                this.molineteAUsar = (this.molineteAUsar + 1) % this.capacidad;//Los que entran ocupan distintos molinetes
                this.turnoActual++;
                this.entrada.notify();
                this.cantUsandoMolinete++;
                retornar = this.molineteAUsar;
            }
        }
        return retornar;
    }

    public void entrar(int numMolinete, Thread hilo) throws BrokenBarrierException, InterruptedException {
        //Método para el esquiador, usar el pase para el molinete
        this.molinetes[numMolinete].usarPase();
        synchronized (entrada) {
            this.puedeUsarMolinete = false;
            this.cantUsandoMolinete = 0;//reiniciar la variable
        }
    }

    public synchronized void subir() {
        //Método para el esquiador
        while (cantArriba == capacidad || !estaAbajo) {
            //Si está lleno o está arriba
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
        cantArriba++;
        System.out.println(Thread.currentThread().getName() + " sube al medio de elevación Nº" + this.id);
        if (cantArriba == capacidad) {
            //le debe llegar una notificación al control del medio
            notifyAll();
        }
    }

    public synchronized void ascender() {
        //Método para control del medio
        while (cantArriba < capacidad) {
            //Mientras la silla no esté llena
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(MedioElevacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Sube el medio de elevación Nº" + this.id);
        estaAbajo = false;
    }

    public synchronized void llegarArriba() {
        //Método para el medio de elevación
        estaArriba = true;
        System.out.println("Llega arriba el medio de elevación Nº" + this.id);
        notifyAll();
    }

    public synchronized void bajar() {
        //Método para el esquiador
        while (!estaArriba) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
        cantArriba--;
        if (cantArriba == 0) {
            notifyAll();
        }
        System.out.println(Thread.currentThread().getName() + " se baja del medio de elevación Nº" + this.id);
    }

    public synchronized void descender() {
        //Método para el medio de elevación (desciende sin pasajeros)
        while (cantArriba > 0 || !estaArriba) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(MedioElevacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Empieza a descender el medio de elevación Nº" + this.id);
        estaArriba = false;
    }

    public synchronized void llegarAbajo() {
        //Método para el medio de elevación (puede recoger pasajeros)
        estaAbajo = true;
        System.out.println("Llego abajo el medio de elevación Nº" + this.id);
        notifyAll();
    }

    public void permitirUsarMolinetes() {
        //Método para medio de elevación
        synchronized (entrada) {
            this.puedeUsarMolinete = true;
            this.entrada.notifyAll();

        }
    }

    public void deshabilitar() {
        synchronized (entrada) {
            this.abierto = false;
            CyclicBarrier barrera = this.molinetes[0].getCyclycBarrier();//Todos los molinetes de un mismo medio tienen la misma referencia a un CyclicBarrier
            barrera.reset();//Si había gente esperando en los molinetes, se retiran con un BrokenBarrierException
            this.entrada.notifyAll();// Los que esperaban antes de entrar a los molinetes se retiran
        }
    }

    public void habilitar() {
        //Si la barrera quedó rota por el uso del reset(), reemplazar por una nueva
        synchronized (this.entrada) {
            this.abierto = true;
            this.molineteAUsar = -1;
            this.abierto = true;
            CyclicBarrier barrera = this.molinetes[0].getCyclycBarrier();//Tomar el CyclicBarrier y ver si está en estado broken después de hacer reset())
            if (barrera.isBroken()) {
                int i;
                CyclicBarrier barrera2 = new CyclicBarrier(this.capacidad);
                for (i = 0; i < this.capacidad; i++) {
                    this.molinetes[i].setBarrera(barrera2);
                }
            }
        }
    }

    public int getSumatoriaContador() {
        int resultado = 0;
        synchronized (entrada) {
            //Método para obtener la suma de todos los contadores de los molinetes asociados al medio de elevación
            for (int i = 0; i < molinetes.length; i++) {
                resultado += molinetes[i].getContador();
            }
        }
        return resultado;
    }

    public int getId() {
        return this.id;
    }
}

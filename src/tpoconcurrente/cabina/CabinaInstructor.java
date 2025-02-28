/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpoconcurrente.cabina;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author ulises.corrales
 */
public class CabinaInstructor {

    private int segundosMax = 5;
    private final int cantAlumnosMax = 4;

    private final Lock entrada = new ReentrantLock(true);

    private final Condition puedeEntrar = entrada.newCondition();
    private final Condition puedeEnseniar = entrada.newCondition();
    private final Condition puedeEmpezarClase = entrada.newCondition();
    private int esperando = 0;
    private int cantEntrar = 0;
    private boolean crearClase = true;
    private Clase claseActual;
    private boolean puedeIniciarClase = false;

    private final Lock salida = new ReentrantLock(true);
    private final Condition puedeComenzarOtraClase = salida.newCondition();
    private final Condition puedeSalir = salida.newCondition();

    private int contadorClases = 0;

    public Clase entrarClase() {
        this.entrada.lock();
        Clase claseATomar;
        try {
            //Método de esquiador
            boolean exito = true;
            this.esperando++;
            this.puedeEnseniar.signal();
            //Si pueden entrar esquiadores o existe un timeout
            while (this.cantEntrar == 0 && exito) {
                try {
                    exito = this.puedeEntrar.await(segundosMax, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                }
            }
            this.esperando--;
            if (exito) {
                //sin timeout
                this.cantEntrar--;
                claseATomar = this.claseActual;
                claseATomar.addAlumno();
                System.out.println(Thread.currentThread().getName() + " entra a la clase Nº" + claseATomar.getId());
                if (cantEntrar == 0) {
                    this.puedeIniciarClase = true;
                    this.puedeEmpezarClase.signal();
                }
            } else {
                //timeout cumplido
                claseATomar = null;
                System.out.println(Thread.currentThread().getName() + " esperó la clase y se fue (timeout)");
            }
        } finally {
            this.entrada.unlock();
        }
        return claseATomar;
    }

    public Clase comenzarEnseniar() {
        //Método para instructor 
        Clase claseEmpezada = null;
        this.entrada.lock();
        try {
            //Avisar a los alumnos que pueden entrar
            while (!this.crearClase || this.esperando < this.cantAlumnosMax) {
                try {
                    this.puedeEnseniar.await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CabinaInstructor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.crearClase = false;//Bloquear que otro profesor pueda crear una clase hasta que el actual termine de hacerlo
            this.claseActual = new Clase(this.contadorClases);
            this.contadorClases++;
            claseEmpezada = this.claseActual;
            this.cantEntrar = this.cantAlumnosMax;
            this.puedeEntrar.signalAll();
            //Empezar la clase
            while (!puedeIniciarClase) {
                try {
                    this.puedeEmpezarClase.await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CabinaInstructor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //Reiniciar las variables para otro profesor
            this.puedeIniciarClase = false;
            this.crearClase = true;//Otro profesor puede crear otra clase
            System.out.println(Thread.currentThread().getName() + " comienza la clase Nº" + claseEmpezada.getId());
            puedeEnseniar.signal();//Avisarle al siguiente profesor que puede recoger alumnos
        } finally {
            this.entrada.unlock();
        }
        return claseEmpezada;
    }

    public void terminarEnseniar(Clase clase) {
        this.salida.lock();
        try {
            //Método para instructor
            clase.terminar();
            System.out.println(Thread.currentThread().getName() + " termina la clase Nº" + clase.getId());
            puedeSalir.signalAll();
            //Esperar que se vacíe la clase para empezar otra
            while (!clase.estaVacia()) {
                try {
                    this.puedeComenzarOtraClase.await();
                } catch (InterruptedException e) {
                }
            }
        } finally {
            this.salida.unlock();
        }

    }

    public void dejarClase(Clase clase) {
       
            this.salida.lock();
        try {
            //Método para esquiador
            while (!clase.finalizado()) {
                try {
                    puedeSalir.await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CabinaInstructor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            clase.restarAlumno();
            if (clase.estaVacia()) {
                //Avisar al esquiador que puede comenzar otra clase
                this.puedeComenzarOtraClase.signal();
            }
            System.out.println(Thread.currentThread().getName() + " se fue de la clase Nº" + clase.getId());
        } finally {
            this.salida.unlock();
        }

      
    }

}

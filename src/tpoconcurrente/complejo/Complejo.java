/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpoconcurrente.complejo;

import tpoconcurrente.elevacion.MedioElevacion;
import tpoconcurrente.cabina.CabinaInstructor;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import tpoconcurrente.confiteria.Confiteria;

/**
 *
 * @author ulisescorrales
 */
public class Complejo {
    //Clase que contiene todos los elementos del complejo y que puede usar un esquiador

    private final MedioElevacion[] medios;
    private final AtomicInteger horaActual= new AtomicInteger(15);
    private int horaApertura = 10;
    private int horaCierre = 17;
    private final CabinaInstructor cabina;
    private final Confiteria confiteria;

    public Complejo(MedioElevacion[] medios, CabinaInstructor cabina, Confiteria confiteria) {
        //El complejo posee todos los objetos compartidos
        this.medios = medios;
        this.cabina = cabina;
        this.confiteria = confiteria;
    }

    public MedioElevacion getRandomMedioElevacion() {
        //Método que retorna un medio de elevación aleatorio para que un esquiador por el que un esquiador pueda subir
        //Si está cerrado, retorna null
        MedioElevacion medio;
        Random ran = new Random();
        int num = ran.nextInt(medios.length);
        medio = this.medios[num];
        return medio;
    }

    private void cerrarMedios() {
        //Invoca reset() a los CylicBarrier de todos los medios
        int cantMedios = this.medios.length;
        for (int i = 0; i < cantMedios; i++) {
            medios[i].deshabilitar();
            System.out.println("Cerrado medio Nº" + i + " con " + medios[i].getSumatoriaContador() + " asistencias");
        }
    }

    private void abrirMedios() {
        //Permitir la entrada de esquiadores a los medios
        int cantMedios = this.medios.length;
        for (int i = 0; i < cantMedios; i++) {
            medios[i].habilitar();
            System.out.println("Abierto medio Nº" + i);
        }
    }

    public void addHora() {
        //Método para reloj
        horaActual.set((horaActual.get()+1)%24);
        int hora=horaActual.get();
        if (hora == horaCierre) {
            //Cerrar
            cerrarMedios();
        } else if (hora == horaApertura) {
            //Abrir
            abrirMedios();
        }
        System.out.println("HORA: " + hora);
    }

    public CabinaInstructor getCabinaInstructor() {
        return this.cabina;
    }

    public Confiteria getConfiteria() {
        return this.confiteria;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpoconcurrente;

import tpoconcurrente.complejo.Complejo;
import tpoconcurrente.complejo.Esquiador;
import tpoconcurrente.elevacion.MedioElevacion;
import tpoconcurrente.elevacion.ControlMedio;
import tpoconcurrente.cabina.CabinaInstructor;
import tpoconcurrente.cabina.Instructor;
import tpoconcurrente.confiteria.Confiteria;
import tpoconcurrente.confiteria.Cocinero;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import tpoconcurrente.complejo.Reloj;

/**
 *
 * @author ulisescorrales
 */
public class TpoConcurrente {

    /**
     * @param args the command line arguments
     * /home/ulisescorrales/NetBeansProjects/TPOConcurrente
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        //Complejo
        CabinaInstructor cabina = new CabinaInstructor();
        int cantMedios = 4;
        MedioElevacion[] medios = new MedioElevacion[cantMedios];
        ControlMedio[] controles = new ControlMedio[cantMedios];
        int i = 0;
        Random random = new Random();
        int cantMolinetes;
        for (i = 0; i < cantMedios; i++) {
            cantMolinetes = random.nextInt(4) + 1;
            medios[i] = new MedioElevacion(cantMolinetes, i);
            System.out.println("Creando medio de elevación Nº" + i + " con capacidad " + cantMolinetes);
            controles[i] = new ControlMedio(medios[i]);
            controles[i].start();
        }

        Confiteria confiteria = new Confiteria();
        Complejo complejo = new Complejo(medios, cabina, confiteria);

        //Crear el reloj
        Reloj reloj = new Reloj(complejo);
        ScheduledExecutorService relojExecutor = Executors.newSingleThreadScheduledExecutor();
        //Para tomar clases de esquí
        byte cantInstructores = 5;
        Instructor[] instructores = new Instructor[cantInstructores];
        int k;
        for (k = 0; k < cantInstructores; k++) {
            instructores[k] = new Instructor(cabina, k);
            instructores[k].start();
        }

        //Confitería
        int cantCocineros = 4;
        Cocinero[] cocineros = new Cocinero[cantCocineros];
        int m;
        for (m = 0; m < cantCocineros; m++) {
            cocineros[m] = new Cocinero(confiteria, m);
            cocineros[m].start();
        }

        System.out.println("Eliga que opción testear");
        System.out.println("1-Usar medios de elevación");
        System.out.println("2-Tomar clases de esquí");
        System.out.println("3-Ir a la confitería");
        switch (scan.nextInt()) {
            case 1:
                crearEsquiadores(complejo, 1);
                relojExecutor.scheduleAtFixedRate(reloj, 0, 2, TimeUnit.SECONDS);
                break;
            case 2:
                crearEsquiadores(complejo, 2);
                break;
            case 3:
                crearEsquiadores(complejo, 3);
                break;
        }
    }

    public static void crearEsquiadores(Complejo complejo, int opcion) {
        //Crear esquiadores        
        int cantEsquiadores=0;
        switch (opcion) {
            case 1:
                cantEsquiadores = 20;
                break;
            case 2:
                cantEsquiadores = 21;//Un esquiador espera y se va
                break;
            case 3:
                cantEsquiadores = 101;
                break;
        }
        Esquiador[] esquiadores = new Esquiador[cantEsquiadores];
        int j;
        int cant = esquiadores.length;
        for (j = 0; j < cant; j++) {
            esquiadores[j] = new Esquiador(complejo, j, opcion);
            esquiadores[j].start();
        }
    }

}

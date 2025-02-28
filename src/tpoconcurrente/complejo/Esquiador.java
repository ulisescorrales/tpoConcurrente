/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpoconcurrente.complejo;

import java.util.Random;
import tpoconcurrente.elevacion.MedioElevacion;
import tpoconcurrente.cabina.CabinaInstructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import tpoconcurrente.confiteria.Confiteria;
import tpoconcurrente.confiteria.Orden;
import java.util.concurrent.BrokenBarrierException;
import tpoconcurrente.cabina.Clase;
/**
 *
 * @author ulisescorrales
 */
public class Esquiador extends Thread{
	private final Complejo complejo;
	private final int opcionTest;

	public Esquiador(Complejo complejo,int numEsquiador,int opcion) {
		super("Esquiador Nº"+ numEsquiador);
		this.complejo = complejo;
		this.opcionTest=opcion;
	}
        @Override
	public void run(){
		switch(this.opcionTest){
			case 1:
			while(true){
				esquiar();
			}
			//break;
			case 2:
			tomarClases();
			break;
			case 3:
			irALaConfiteria();
			break;
		}
	}

	private void esquiar(){
		//El esquiador escoge un medio al azar
		MedioElevacion medio=complejo.getRandomMedioElevacion();
		try{
			int molineteAUsar=medio.verificarHabilitado();
                        //System.out.println("Habilitado: "+puedePasarMolinete);
			//System.out.println(Thread.currentThread().getName()+" quiere usar el medio de elevación Nº"+medio.getId());
			if(molineteAUsar>-1){
				medio.entrar(molineteAUsar,this);
				//System.out.println("Esquiador Nº "+idEsquiador +" quiere subir");
				medio.subir();
				//System.out.println("Esquiador Nº "+idEsquiador +"  sube");
				medio.bajar();//Bajar de la silla
				//System.out.println("Esquiador Nº"+idEsquiador+" está esquiando");
				sleep(5000);//Esquiar y descansar
			}
		}catch(InterruptedException|BrokenBarrierException e){
		}
	}
	private void tomarClases(){
		CabinaInstructor cabina=this.complejo.getCabinaInstructor();
		Clase clase=cabina.entrarClase();
		if(clase!=null){
			cabina.dejarClase(clase);
		}
	}

	private void irALaConfiteria(){
		Confiteria confiteria=complejo.getConfiteria();
		confiteria.entrar();
		//Aleatoriamente el esquiador elige con postre o no. Si es con postre, generará dos ordenes en la confitería
		Random ran=new Random();
		Orden ordenComida =confiteria.generarOrden(false);
		Orden ordenPostre=null;
		if(ran.nextInt(2)==1){
			ordenPostre=confiteria.generarOrden(true);
		}
		//Una vez generadas las órdenes, recoge la comida
		confiteria.recogerComida(ordenComida);
		if(ordenPostre!=null){
			confiteria.recogerComida(ordenPostre);
		}
		try {
			//Comer
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
			Logger.getLogger(Esquiador.class.getName()).log(Level.SEVERE, null, ex);
		}

		confiteria.salir();
	}
}

package tpoconcurrente.cabina;

public class Clase{
	//Clase que vincula al esquiador y al instructor
	private boolean terminado=false;
	private int cantAdentro=0;
	private final int id;

	public Clase(int id){
		this.id=id;
	}
	public int getId(){
		return this.id;
	}
	public void terminar(){
		this.terminado=true;
	}
	public boolean finalizado(){
		return this.terminado;
	}
	public void addAlumno(){
		this.cantAdentro++;
	}
	public void restarAlumno(){
		this.cantAdentro--;
	}
	public boolean estaVacia(){
		return this.cantAdentro==0;
	}
	public int getCantAdentro(){
		return this.cantAdentro;
	}
}

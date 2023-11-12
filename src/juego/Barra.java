package juego;
/////////////////////////////////////////
/////////////////////////////////////////

public class Barra{
	//Variables de instancia
	private double x;
	private double y;
	private double ancho;
	private double alto;
	
	//Constructor
	public Barra(double x, double y, double ancho, double alto) {
		this.x=x;
		this.y=y;
		this.ancho=ancho;
		this.alto=alto;
	}
	
	//getterY
	public double getY() {
		return this.y;
	}
	//getterX
	public double getX() {
		return this.x;
	}
	//getter Alto
	public double getAlto() {
		return this.alto;
	}
	//getter Ancho
	public double getAncho() {
		return this.ancho;
	}
	
	//getter piso donde se apoya el agente, barriles, Donky, etc.
	public double getPiso() {
		return this.y-this.alto/2;
	}
	
	//getter piso donde se apoya el agente, barriles, Donky, etc.
	public double getBase() {
		return this.y-this.ancho/2;
	}
	
	
}

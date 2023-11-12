package juego;

/////////////////////////////////
/////////////////////////////////

public class Barril{
	//Variables de instancia
	private double x;
	private double y;
	private double diametro;
	
	//Constructor
	public Barril(double x, double y, double diametro){
		this.x=x;
		this.y=y;
		this.diametro=diametro;
	}
	
	/////////////movimiento////////////////
	///////////////////////////////////////
	//Derecha
	public void moverseDerecha(double velocidad){
		this.x+=velocidad;
	}
	
	//Izquierda
	public void moverseIzquierda(double velocidad){
		this.x-=velocidad;
	}
	
	//caerse hacia la derecha
	public void caerseDerecha(double distanciaCaida, double anguloCaida){
		this.y+=distanciaCaida;
		this.x+=anguloCaida;
	}
	
	//caerse
	public void caerseIzquierda(double distanciaCaida, double anguloCaida){
		this.y+=distanciaCaida;
		this.x-=anguloCaida;
	}
	
	
	/////////////getters/////////////////
	/////////////////////////////////////
	//getter base del barril y
	public double getBase() {
		return this.y+this.diametro/2; //da el perfilderecho del barril
	}
	
	//getter altura del barril y
	public double getAltura() {
		return this.y-diametro/2; //da el perfil iszquierdo del barril
	}
	
	//getter inicio del barril x
	public double getInicio() {
		return this.x-this.diametro/2;
	}
	
	//getter fin del barril x
	public double getFin() {
		return this.x+this.diametro/2;
	}
		
	//getters
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
}

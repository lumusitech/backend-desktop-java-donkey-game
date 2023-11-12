package juego;

public class Donkey {
	private int x;
	private int y;
	private int ancho;
	private int alto;
	
	//Constructor
	public Donkey(int x,int y, int ancho, int alto) {
		this.x=x;
		this.y=y;
		this.ancho=ancho;
		this.alto=alto;
	}	
	
	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	// getters para el valor donde termina a la derecha
	public int getFin() {
		return this.x+this.ancho/2;
	}
	// getters para el valor donde empieza a la izquierda
	public int getInicio() {
		return this.x-this.ancho/2;
	}
	
	// getters para el valor del piso 
	public int getPiso() {
		return this.y+this.alto;
	}
	// getters para el valor del techo
	public int getTecho() {
		return this.y-this.alto;
	}
	
}

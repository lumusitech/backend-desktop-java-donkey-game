package juego;

/////////////////////////////////////////
/////////////////////////////////////////

public class Escalera{
	
	//Variables de instancia
	private int x;
	private int y;
	private int ancho;
	private int alto;
	
	//Constructor
	public Escalera(int x,int y, int ancho, int alto) {
		this.x=x;
		this.y=y;
		this.ancho=ancho;
		this.alto=alto;
	}
	
	//posicion
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	//perfiles
	public int getPerfilDer() {
		return this.x+ancho/2;
	}
	
	public int getPerfilIzq() {
		return this.x-ancho/2;
	}
	
	public int getFin() {
		return this.y-alto/2;
	}
	
	public int getInicio() {
		return this.y+alto/2;
	}
	
	public int getAlto() {
		return this.alto;
	}

	
}

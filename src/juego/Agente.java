package juego;
/////////////////////////////////////////
/////////////////////////////////////////

public class Agente{
	//Variables de instancia
	private int x;
	private int y;
	private int ancho;
	private int alto;
	private int velocidad;
	private int tiempoSalto;
	private int ajusteImpactoX;
	private int ajusteImpactoY;
	
	//Constructor
	public Agente(int x,int y, int ancho, int alto, int velocidad, int tiempoSalto, int ajusteImpactoX, int ajusteImpactoY) {
		this.x=x;
		this.y=y;
		this.ancho=ancho;
		this.alto=alto;
		this.velocidad=velocidad;
		this.tiempoSalto=tiempoSalto;
		this.ajusteImpactoX=ajusteImpactoX;
		this.ajusteImpactoY=ajusteImpactoY;
	}
	
	/////////////////MOVIMIENTO//////////////////
	/////////////////////////////////////////////
	//limites de la barra base 
	//mientras sea V el agente podra seguir moviendose hacia la derecha
	public boolean sobreLineaDeBarra(Barra b) {
		//Devuelve V si la base Y del agente esta sobre la barra	
		return this.y+this.alto/2 == b.getPiso(); 
		//Y si el X del agente no llego al fin de la barra
	}
	
	
	//enEscaleraArriba
	public boolean enEscaleraArriba(Escalera e) {
		//Devuelve V si esta en el rango X de una escalera
		return this.x >= e.getPerfilIzq() && this.x <= e.getPerfilDer()
		//y si la base del agente aun no llego al final Y de la escalera
		&& this.y+this.alto/2 <= e.getY()+e.getAlto()/2 && this.y+this.alto/2 >= e.getY()-(e.getAlto()/2)+1;
	}
	//enEscaleraAbajo
	public boolean enEscaleraAbajo(Escalera e) {
		//Devuelve V si esta en el rango X de una escalera
		return this.x >= e.getPerfilIzq() && this.x <= e.getPerfilDer()
		//y si la base del agente esta en la cima o aun no llego a la base Y de la escalera
		&& this.y+this.alto/2>= e.getY()-e.getAlto()/2 && this.y+this.alto/2 <= e.getY()+(e.getAlto()/2)-1;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//Moverse a la derecha
	public void moverseDerecha() {
		this.x+=this.velocidad;
	}
	
	//Moverse a la izquierda
	public void moverseIzquierda() {
		this.x-=this.velocidad;
	}
	
	//Moverse arriba
	public void moverseArriba() {
		this.y-=this.velocidad-1;
	}
	
	//Moverse abajo
	public void moverseAbajo() {
		this.y+=this.velocidad-1;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	
	//getter X
	public int getX() {
		return this.x;
	}
	
	//getter Y
	public int getY() {
		return this.y;
	}
	
	//getter inicio X agente
	public int getInicio() {
		return this.x-this.ancho/2;
	}
	
	//getter ancho agente
	public int getAncho() {
		return this.ancho;
	}
	
	//getter X fin
	public int getFin() {
		return this.x+this.ancho/2;
	}
	
	//getter techo Y agente
	public int getTecho() {
		return this.y-this.alto/2;
	}
	
	//getter piso Y agente
	public int getPiso() {
		return this.y+this.alto/2;
	}
	
	//////////////////////////////SALTO///////////////////////////////////////////////////

	
	public int getVelSalto() {
		return this.tiempoSalto;
	}
		
	//setter "y" para el inicio del salto, subida, si se cambia sube mas alto o menos alto
	public void iniciaSalto(double altura) {
		this.y-=altura;		
	}
	
	//setter "y" para el fin del salto, caida, su valor tiene que se igual al inicial salto
	//para que retorne a la superficie de donde salio
	public void terminaSalto(double altura) {
		this.y+=altura;
	}	
	
	//Moverse a la derecha en el salto
	public void moverseDerechaSalto(int VelocidadMovDer) {
		this.x+=VelocidadMovDer;		
	}			
		
	//Moverse a la izquierda en el salto
	public void moverseIzquierdaSalto(int VelocidadMovIzq) {
		this.x-=VelocidadMovIzq;
	}	
	
	/////////////////////////////////
	
	public void cae(int velocidadDeCaida) {
		this.y+=velocidadDeCaida;
	}
	
	/////////////////////////////////
	
	//Colisionar con Barril 
	public boolean colision(Barril b) {
		
		//Se observan Ajustes hechos a los barriles para que no sea tan dificil sortearlos
		//impacto a agente por su derecha
		return ( this.x+this.ancho/2 >= b.getInicio()+this.ajusteImpactoX && this.y+this.alto/2==b.getBase()
		&& this.x-this.ancho/2 < b.getFin()-this.ajusteImpactoX
		//impacto a agente por su izquierda
		|| this.x-this.ancho/2 <= b.getFin()-this.ajusteImpactoX && this.y+this.alto/2==b.getBase() 
		&& this.x+this.ancho/2 > b.getInicio()+this.ajusteImpactoX
		//impacto a agente desde arriba
		|| this.y-this.alto <= b.getBase()-this.ajusteImpactoY && this.y+this.alto/4 > b.getAltura()+this.ajusteImpactoY 
		&& this.x+this.ancho/2 >= b.getInicio()+this.ajusteImpactoX && this.x-this.ancho/2 <= b.getFin()-this.ajusteImpactoX
		// impacto a agente desde abajo ( osea el agente salta sobre algun barril..
		|| this.y+this.alto/2 >= b.getAltura()+this.ajusteImpactoY && this.y-this.alto/2 < b.getBase()-this.ajusteImpactoY
		&& this.x+this.ancho/2 >= b.getInicio()+this.ajusteImpactoX && this.x-this.ancho/2 <= b.getFin()-this.ajusteImpactoX);
	}  
	
	//Colision con Donkey 
	public boolean colisionD(Donkey d) {
		//impacto a agente derecha
		return ( this.x-this.ancho/2 <= d.getFin() &&   this.x+this.ancho/2 > d.getInicio() 
		&& this.y+this.alto/2<=d.getPiso() && this.y-this.alto/2 >= d.getTecho() );         
	} 
		
}


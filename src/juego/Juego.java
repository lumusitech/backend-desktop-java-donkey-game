package juego;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.sound.sampled.Clip;
import java.util.Random;
import javax.swing.*;
import entorno.Entorno;
import entorno.InterfaceJuego;
import entorno.Herramientas;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Juego extends InterfaceJuego {
	
	/////////////////////////////////////////VARIABLES DE INSTANCIA////////////////////////////////////////////////////

	//ENTORNO DEL JUEGO Y HERRAMIENTAS
	private Entorno entorno;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private String jugador;
	private int dificultad;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private int puntajeActual;
	private int puntajeMaximo;
	//Se puede poner la cantidad de puntajes que se quiera
	//y los mostrará al ganar, perder o pausar el juego
	private int [] puntajes = new int[8]; 
		
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Clip sonidoInicio;
	private Clip sonidoFondoJuego;
	private Clip sonidoPerdio;
	private Clip sonidoGano;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//CAPTURA DEL TICK Y OTROS TEMPORIZADORES
	private int tick;					//(se inicializa en 0; y dentro del tick() se aumenta como contador)
	private int segundos;				//usado para calcular segundos a partir del tick
	private int cronometroSalto;		//usado controlar el salto del agente (subida y bajada)
	//TEMPORIZADORES PARA ANIMACION
	private int cronometroAgenteDer;	//controla la carga de imagen al caminar a la derecha
	private int cronometroAgenteIzq;	//controla la carga de imagen al caminar a la izquierda
	private int cronometroAgenteEsc;	//controla la carga de imagen al subir o bajar por la escalera
	//usado para animar con imagenes a cada barril
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//CONTROL DEL JUEGO - COMENZAR Y PAUSAR JUEGO
	private boolean comenzar;		//se usa para comenzar el juego si se presiona la tecla enter en el inicio
	private int pausa;				//se usa para pausar el juego su se presiona la tecla ctrl durante el juego
	private int contadorDeReinicios;
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	//BARRAS
	private Barra[] barras = new Barra[6];
	private int distanciaBarras;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//ESCALERAS
	private Escalera[] escaleras = new Escalera[5];
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//BARRILES
	
	//usadas para hacer aparecer un barril con salidas y velocidades aleatorias
	
	private int cantidadDeBarriles;
	
	private int [] cronometroBarril;
	
	private Barril [] barriles;
	
	private Random [] randomSalidaDeBarril;
	
	private int [] salidaDeBarril;
	
	private Random [] randomVelocidad; 

	private int [] velocidadAleatoria;
	
	private int controlMovBarril;		//usado para animar con imagenes a cada barril
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//AGENTE
	private Agente agente;	
	
	int velocidadDelAgente;
	
	private int ajusteImpactoX;
	
	private int ajusteImpactoY;
	
	private int controlMov;
	
	private int [] enElAire = new int[5];	//usado para limitar el salto durante la caida de las barras
	
	private boolean salto;					//usada para controlar la carga de imagenes durante el salto
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//DONKEY
	private Donkey donkey;
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	
	///////////////////////////////////////////CONSTRUCTOR/////////////////////////////////////////////////////////////
	
	Juego() {
		//ENTORNO DEL JUEGO
		
		//Pregunta el nombre del jugador
		this.jugador=JOptionPane.showInputDialog("Ingrese su nombre de agente");		
		
		//Pregunta el nivel de dificultad
		this.dificultad=JOptionPane.showConfirmDialog(this.entorno, "¿Deseas jugar en modo fácil?");
		if(this.dificultad==0) {
			this.cantidadDeBarriles=2;
			this.velocidadDelAgente=3;
			this.ajusteImpactoX=10;
			this.ajusteImpactoY=15;
		}
		else if(this.dificultad==1){
			this.cantidadDeBarriles=4;
			this.velocidadDelAgente=2;
			this.ajusteImpactoX=5;
			this.ajusteImpactoY=7;
		}
		else if(this.dificultad==2){
			this.cantidadDeBarriles=7;
			this.velocidadDelAgente=2;
			this.ajusteImpactoX=0;
			this.ajusteImpactoY=0;
		}
		
		this.cronometroBarril = new int [this.cantidadDeBarriles];
		
		this.barriles = new Barril[this.cantidadDeBarriles];
		
		this.randomSalidaDeBarril = new Random [this.cantidadDeBarriles];
		
		this.salidaDeBarril = new int [this.cantidadDeBarriles];
		
		this.randomVelocidad = new Random [this.cantidadDeBarriles]; 

		this.velocidadAleatoria = new int [this.cantidadDeBarriles];
		
		this.entorno = new Entorno(this, "Donkey - Grupo: Fernández - Hullman - Figueroa - V0.01", 800, 600);
		
		//Se controla la cantidad de reinicios para diferentes fines
		this.contadorDeReinicios = 0;
		//guarda el puntaje actual
		this.puntajeActual = 0;
		//guarda el puntaje maximo
		this.puntajeMaximo = 0;
		//se resetean los puntajes anteriores
		//this.resetearPuntajes();
		
		//INICIALIZA LAS VARIABLES DE INSTANCIA
		this.inicializar();
		
		//INICIA EL JUEGO!
		this.entorno.iniciar();
		
	}//FIN DEL CONSTRUCTOR
	
	/*****************************************************************************************************************/
	/*****************************************************************************************************************/
	
	///////////////////////////////////////////////METODO TICK/////////////////////////////////////////////////////////
	
	public void tick() {
		
		//////////////////////////////SE EJECUTA EN CADA INSTANTE - SIMULA PASO DEL TIEMPO/////////////////////////////
		
		//imprime la pantalla inicial del juego y recibe la orden de inicio de juego del usuario
		if (this.comenzar == false) {
			//solo carga la imagen de inicio solo la primera vez
			if(this.contadorDeReinicios==1) {
				this.fondoInicio();		//funcion que carga imagen de inicio
				this.sonidoInicio.start();
			}
			this.comenzar = this.entorno.estaPresionada(this.entorno.TECLA_ENTER);		//comenzar recibe orden
		}
		else if (this.comenzar) {		//si comenzar es true
			this.sonidoInicio.stop();
			//El metodo tick se va ejecutar si el agente no choca contra ningun barril
			if (!this.colisionAgenteYBarriles(this.cantidadDeBarriles)
			//y NO toque (vacune) a Donkey
			&& (!this.agente.colisionD(donkey))) {
				
			///////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				//PAUSA
				if (this.entorno.sePresiono(this.entorno.TECLA_CTRL) && this.pausa == 0) {
					this.pausa++;		//pone pausa en 1 si se presiono ctrl
				}
				
				else if (this.pausa == 1) {					
					dibujarPausa();		//metodo que dibuja la pausa cuando pausa es 1
					//si se vuelve a presionar ctrl pausa pasa a ser 0
					if (this.entorno.sePresiono(this.entorno.TECLA_CTRL) && this.pausa == 1) {
						this.pausa = 0;
					}
				}

				//si no esta en pausa, pausa=0, el juego se activa
				else if (this.pausa == 0) {
					
					///////////////////////////////////////////////////////////////////////////////////////////////////
					
					//se dibuja el fondo del juego
					this.fondoJuego();		
					
					///////////////////////////////////////////////////////////////////////////////////////////////////
					
					this.tick++;//el temporizador tick captura cada ciclo del metodo tick
					
					///////////////////////////////////////////////////////////////////////////////////////////////////
					
					this.calcularSegundos();
					
					///////////////////////////////////////////////////////////////////////////////////////////////////
					
					//BARRILES											 
					
					//carga la imagen predeterminada de Donkey
					this.Donkey();
					//Se inicia el cronometro que se usa en el método animacionBarriles
					this.controlMovBarril++;
					//Método que crea la cantidad de barriles que se le pida
					this.fabricaDeBarriles(this.cantidadDeBarriles);	
					
					///////////////////////////////////////////////////////////////////////////////////////////////////
					
					//MOVIMIENTOS DEL AGENTE
					
					//movimientos permitidos derecha e izquierda sobre barras
					this.movSobreBarras(this.barras);			
					
					//movimientos permitidos sobre escaleras
					this.movSobreEscaleras();					
					
					//movimiento de salto
					this.saltoDeAgente();
					
					//movimiento de caída
					this.caidaDeAgente();
						
					///////////////////////////////////////////////////////////////////////////////////////////////////
	
					//CARGA DE IMÁGENES	DEL AGENTE  						   			 	

					//PARADO
					if(this.estaParado()) {
						//se carga la imagen del agente parado
						this.agenteParado(this.entorno);
					}
					
					//PARADO EN ESCALERA
					else if(this.estaParadoEnEscalera()){
						//se carga la imagen del agente parado en la escalera
						this.agenteEscParado(this.entorno);
					}
								
					//DERECHA SOBRE BARRAS
					else if(this.puedeCaminaALaDerecha()) {
						//se cargan las imágenes del agente caminando a la derecha
						this.caminaALaDerecha();
					}	
					
					//IZQUIERDA SOBRE BARRAS
					else if(this.puedeCaminarALaIzquierda()) {
						//se cargan las imágenes del agente caminando a la izquierda
						this.caminaALaIzquierda();
					}
							
					//ARRIBA Y ABAJO EN ESCALERAS
					if( this.puedeSubirBajarEscalera() ) {
						//se cargan las imágenes del agente subiendo-bajando escalera
						this.subirBajarEscaleras();
					}
	
					//IMAGENES DEL SALTO
					if(puedeSaltar()) {
						//se cargan las imágenes del agente saltando
						this.salta();
					} 
								
					//IMAGENES DE CAIDA
					//si esta cayendo carga estas imagenes
					if(this.enElAire()){
						//se cargan las imágenes del agente cayendo
						this.cae();
					}	
					
					///////////////////////////////////////////////////////////////////////////////////////////////////

				
					

					//////////////////////////////////////////////////////////////////////////////////////////////////////			
					//IMPRESIONES EN PANTALLA										
					
					//De acuerdo a la situación, este método escribe texto en pantalla
					this.imprimirTextosEnPantalla();
					
					//////////////////////////////////////////////////////////////////////////////////////////////////////
					
				}// fin de la pausa
	
			}// fin del control: colision del agente con barriles
				
			// Si choca con cualquiera de los barriles Pierde el juego
			else if (this.perdio()) {
				//se reproduce el sonido de choque
				this.sonidoPerdio.start();
				//Si pierde llama a este metodo para que dibuje la ultima posicion de las cosas cuando pierde
				dibujarPerdio();
				//si se presiona enter se reinicia el juego
				if(this.entorno.sePresiono(this.entorno.TECLA_ENTER)) {
					//se reinician todas las variables para iniciar un nuevo juego
					this.inicializar();
				}
				else if(this.entorno.sePresiono(this.entorno.TECLA_FIN)) {
					//si presiona la tecla fin se destruye el objeto entorno, se cierra la ventana
					this.entorno.dispose();
					this.sonidoFondoJuego.stop();
				}
				else if(this.entorno.sePresiono(this.entorno.TECLA_DELETE)) {
					//si presiona suprimir se resetean los puntajes
					this.resetearPuntajes();
				}
			}
			
			
			// O si logra tocar a Donkey gana el juego
			else if (this.gano()) {
				//se reproduce el sonido de choque
				this.sonidoGano.start();
				//Si gana llama a este metodo para que dibuje la ultima posicion de las cosas cuando gana
				this.dibujarGano();
				//si se presiona enter se reinicia el juego
				if(this.entorno.sePresiono(this.entorno.TECLA_ENTER)) {
					//se reinician todas las variables para iniciar un nuevo juego
					this.inicializar();
				}
				else if(this.entorno.sePresiono(this.entorno.TECLA_FIN)) {
					//si presiona la tecla fin se destruye el objeto entorno, se cierra la ventana
					this.entorno.dispose();
					this.sonidoFondoJuego.stop();
				}
				else if(this.entorno.sePresiono(this.entorno.TECLA_DELETE)) {
					//si presiona suprimir se resetean los puntajes
					this.resetearPuntajes();
				}
			}
			
			
			//si pausa el juego
			if (this.pausa==1) {
				//si esta en pausa llama a este metodo para que dibuje la ultima posicion de las cosas
				//si se presiona enter se reinicia el juego
				if(this.entorno.sePresiono(this.entorno.TECLA_ENTER)) {
					//se reinician todas las variables para iniciar un nuevo juego
					this.inicializar();
				}
				else if(this.entorno.sePresiono(this.entorno.TECLA_FIN)) {
					//si presiona la tecla fin se destruye el objeto entorno, se cierra la ventana
					this.entorno.dispose();
					this.sonidoFondoJuego.stop();
				}
				else if(this.entorno.sePresiono(this.entorno.TECLA_DELETE)) {
					//si presiona suprimir se resetean los puntajes
					this.resetearPuntajes();
				}
				
			}
			
			//si no está en pausa y no ganó o perdió la música de fondo suena
			if(this.pausa==0 && !this.gano() && !this.perdio()) {
				this.sonidoFondoJuego.start();
			}
			else {
				this.sonidoFondoJuego.stop();
			}
			
			
		} 	// fin del else if respuesta del jugador == true para empezar el juego en la
			// pantalla principal del juego.
		
	}// fin de tick
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	

	/***************************************************************************************************************/
	/***************************************************************************************************************/
	
	
	
	//////////////////////////////////////// METODOS AUXILIARES//////////////////////////////////////////////////////

	//MÉTODO PARA CALCULAR SEGUNDOS APROXIMADOS
	public void calcularSegundos(){
		if (this.tick % 100==0) {
			this.segundos = this.tick / 100;	
		}
	}
	
	
	//PAUSA
	//Se dibuja la ultima posicion antes de la pausa con un color mas oscuro
	public void dibujarPausa() {
		
		this.fondoJuego();
		
		//donkey durante la pausa
		this.Donkey();
		
		//agente durante la pausa
		this.agenteParado(this.entorno);
		
		//barriles durante la pausa
		
		//solo si ya comenzo a rodar el barril se va a ver en la pausa, si todavia
		//sigue en su posicion de inicio no se dibuja
		for(int i=0; i<this.barriles.length; i++) {
			if(colisionAgenteYBarriles(this.cantidadDeBarriles)) {
				this.barrilDetenido();			
			}
		}
		
		imprimirTextosEnPantalla();
		
		//rectangulo que hace mas oscura la pantalla pausada
		this.entorno.dibujarRectangulo(this.entorno.ancho()/2,
		this.entorno.alto()/2, this.entorno.ancho(), this.entorno.alto(),
		0, new Color(0,0,0,150));
		
	}//fin del metodo dibujarPausa

	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//PERDIO
	public boolean perdio() {
		
		return this.colisionAgenteYBarriles(this.cantidadDeBarriles);
	}
	
	//Se dibuja la ultima posicion cuando perdio con un color mas oscuro
	public void dibujarPerdio() {
		
		this.fondoJuego();
		
		//donkey
		this.Donkey();
		
		//agente
		this.agenteParado(this.entorno);

		//barriles
		
		//solo si ya comenzo a rodar el barril se va a ver en la pausa, si todavia
		//sigue en su posicion de inicio no se dibuja
		for(int i=0; i<this.barriles.length; i++) {
			if(colisionAgenteYBarriles(this.cantidadDeBarriles)) {
				this.barrilDetenido();			
			}
		}
		
		//imprime los textos correspondientes a cada situación
		this.imprimirTextosEnPantalla();
				
		//rectangulo que hace mas oscura la pantalla pausada
		this.entorno.dibujarRectangulo(this.entorno.ancho()/2,
		this.entorno.alto()/2, this.entorno.ancho(), this.entorno.alto(),
		0, new Color(0,0,0,150));
		
	}//fin del metodo dibujarPerdio
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//GANO
	public boolean gano() {
		return this.agente.colisionD(donkey);
	}
	
	public void dibujarGano(){
		
		this.fondoJuego();
		
		//carga a donkey
		this.Donkey();
		
		//carga la imagen del agente vacunandolo
		this.agenteVacuna(this.entorno);
		
		//solo si ya comenzo a rodar el barril se va a ver en la pausa, si todavia
		//sigue en su posicion de inicio no se dibuja
		for(int i=0; i<this.barriles.length; i++) {
			if(colisionAgenteYBarriles(this.cantidadDeBarriles)) {
				this.barrilDetenido();			
			}
		}
		
		//imprime los textos correspondientes a cada situación
		this.imprimirTextosEnPantalla();
		
		//rectangulo que hace mas oscura la pantalla pausada
		this.entorno.dibujarRectangulo(this.entorno.ancho()/2,
		this.entorno.alto()/2, this.entorno.ancho(), this.entorno.alto(),
		0, new Color(0,0,0,150));
	}//fin de dibujarGano
		
	////////////////////////////////////////////////////////////////////////////////////////////////////////////7
	
	//FONDO PANTALLA DE INICIO
	public void fondoInicio(){
		Image imagen = Herramientas.cargarImagen("fondo.png");
		this.entorno.dibujarImagen(imagen, 405, 305, 0);
		this.entorno.cambiarFont(Font.SANS_SERIF, 18, Color.WHITE);
		this.entorno.escribirTexto("agente "+this.jugador+" Jumpman", 530, 545);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	//FONDO DEL JUEGO
	public void fondoJuego(){
		Image imagenJuego = Herramientas.cargarImagen("fondoJuego.png");
		this.entorno.dibujarImagen(imagenJuego, 400, 300, 0);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//Textos en pantalla
	public void imprimirTextosEnPantalla() {
		
		this.calcularPuntaje();
		
		//Posiciones del texto
		
		int saltoDeLinea=35;
		
		int posicionRelojX=300;
		int posicionRelojY=25;
		
		int posicionNombreX=600;
		int posicionNombreY=25;
		
		int posicionX = 270;
		int posicionY = 275;
		
		int posicionReiniciarSalirX=150;
		int posicionReiniciarSalirY=520;
		
		//tamaño de fuentes
		
		int tamanioRelojYNombre = 18;
		int tamanioGanoPerdio = 50;
		int tamanioTextoGeneral = 25;
		int tamanioReiniciarSalir = 20;
		
		//color de Fuentes
		
		Color colorRelojYNombre = Color.WHITE;
		Color colorGeneral = Color.WHITE;
		Color colorGano = Color.GREEN;
		Color colorPerdio = Color.RED;
		Color colorReiniciarSalir = Color.WHITE;
		
		// formato del texto en pantalla
		this.entorno.cambiarFont(Font.SANS_SERIF, tamanioRelojYNombre, colorRelojYNombre);
		//impresión de textos
		this.entorno.escribirTexto("Tiempo : " + this.segundos + " segundos", posicionRelojX, posicionRelojY);
		this.entorno.escribirTexto("Agente "+this.jugador+" Jumpman", posicionNombreX, posicionNombreY);
		this.entorno.escribirTexto("Puntaje: "+this.puntajeActual, posicionNombreX, posicionNombreY+saltoDeLinea*1);
		
		if(this.perdio()) {
			
			this.entorno.cambiarFont(Font.SANS_SERIF, tamanioGanoPerdio, colorPerdio);
			this.entorno.escribirTexto("PERDISTE :-(", posicionX, posicionY);
			
			this.entorno.cambiarFont(Font.SANS_SERIF, tamanioTextoGeneral, colorGeneral);
			this.entorno.escribirTexto("Chocaste con un Barril!", posicionX, posicionY+saltoDeLinea*1);
			this.entorno.escribirTexto("Puntaje obtenido: " + this.puntajeActual + " Puntos", posicionX, posicionY+saltoDeLinea*2);
			
			
			//imprime los últimos 5 puntajes
			this.imprimirPuntajesEnPantalla();
			
			this.entorno.cambiarFont(Font.SANS_SERIF, tamanioReiniciarSalir, colorReiniciarSalir);
			this.entorno.escribirTexto("PRESIONA <SUPRIMIR> PARA REINICIAR LOS PUNTAJES!!!",
			posicionReiniciarSalirX, posicionReiniciarSalirY);
			this.entorno.escribirTexto("PRESIONA <ENTER> PARA INTENTARLO DE NUEVO!!!", 
			posicionReiniciarSalirX, posicionReiniciarSalirY+saltoDeLinea*1);
			this.entorno.escribirTexto("PRESIONA <FIN> PARA SALIR SI NO TIENES EL VALOR!!!",
			posicionReiniciarSalirX, posicionReiniciarSalirY+saltoDeLinea*2);
		}
		else if(this.gano()) {
			
			this.entorno.cambiarFont(Font.SANS_SERIF, tamanioRelojYNombre, colorRelojYNombre);
			this.entorno.escribirTexto("Tiempo : " + this.segundos + " segundos",
			posicionRelojX, posicionRelojY);
			this.entorno.escribirTexto("Agente "+this.jugador+" Jumpman", 
			posicionNombreX, posicionNombreY);
			
			this.entorno.cambiarFont(Font.SANS_SERIF, tamanioTextoGeneral, colorGano);
			this.entorno.escribirTexto("Vacunaste a Donkey!!!", posicionX, posicionY);
			this.entorno.escribirTexto("Has superado el simulador!!!",
			posicionX, posicionY+saltoDeLinea*1);
			this.entorno.cambiarFont(Font.SANS_SERIF, tamanioTextoGeneral, colorGeneral);
			this.entorno.escribirTexto("Tardaste: " + this.segundos + " segundos",
			posicionX, posicionY+saltoDeLinea*2);
			this.entorno.escribirTexto("Puntaje obtenido: " + this.puntajeActual + " Puntos",
			posicionX, posicionY+saltoDeLinea*3);
			this.entorno.escribirTexto("Intenta hacerlo en menos tiempo :)",
			posicionX, posicionY+saltoDeLinea*4);
			
			//imprime los últimos 5 puntajes
			this.imprimirPuntajesEnPantalla();
			
			this.entorno.cambiarFont(Font.SANS_SERIF, tamanioReiniciarSalir, colorReiniciarSalir);
			this.entorno.escribirTexto("PRESIONA <SUPRIMIR> PARA REINICIAR LOS PUNTAJES!!!",
			posicionReiniciarSalirX, posicionReiniciarSalirY);
			this.entorno.escribirTexto("PRESIONA <ENTER> PARA INTENTARLO DE NUEVO!!!",
			posicionReiniciarSalirX, posicionReiniciarSalirY+saltoDeLinea*1);
			this.entorno.escribirTexto("PRESIONA <FIN> PARA SALIR SI NO TIENES EL VALOR!!!",
			posicionReiniciarSalirX, posicionReiniciarSalirY+saltoDeLinea*2);
		}
		else if(this.pausa==1) {
			
			//texto de la pausa
			this.entorno.cambiarFont(Font.SANS_SERIF, tamanioGanoPerdio, colorGeneral);
			this.entorno.escribirTexto("Juego pausado", posicionX, posicionY);
					
			//imprime los últimos 5 puntajes
			this.imprimirPuntajesEnPantalla();
			this.entorno.cambiarFont(Font.SANS_SERIF, tamanioReiniciarSalir, colorReiniciarSalir);
			this.entorno.escribirTexto("PRESIONA <SUPRIMIR> PARA REINICIAR LOS PUNTAJES!!!",
			posicionReiniciarSalirX, posicionReiniciarSalirY);
			this.entorno.escribirTexto("PRESIONA <ENTER> PARA REINICIAR EL JUEGO!!!", 
			posicionReiniciarSalirX, posicionReiniciarSalirY+saltoDeLinea*1);
			this.entorno.escribirTexto("PRESIONA <FIN> PARA SALIR SI NO TIENES EL VALOR!!!", 
			posicionReiniciarSalirX, posicionReiniciarSalirY+saltoDeLinea*2);
		}
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//IMAGEN DONKEY
	public void Donkey(){
		Image imagenJuego = Herramientas.cargarImagen("donkey.png");
		this.entorno.dibujarImagen(imagenJuego, 50, 57, 0);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
	//IMAGEN DONKEY TIRANDO BARRIL
	public void DonkeyBarril(){
		Image imagenJuego = Herramientas.cargarImagen("donkeytirabarriles.png");
		this.entorno.dibujarImagen(imagenJuego, 50, 57, 0);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	//MOVIMIENTO DE BARRILES
	public void movimientoBarriles(int numBarril, double velocidadAleatoria) {
		//indica donde inician las barras 2 y 4
		int inicioDeBarrasPares=100;
		
		//indica el punto cuando desaparece un barril de pantalla
		int desaparicionDeBarril=-20;
		
		Barril barril = this.barriles[numBarril];
		
		if (barril.getBase() == this.barras[5].getPiso()		//si esta en la barra 6 se mueve a
		&& barril.getInicio() < this.barras[5].getAncho()) {	//la derecha con una velocidad
			barril.moverseDerecha(velocidadAleatoria);			//aleatoria
		}

		else if (barril.getBase() == this.barras[4].getPiso()	//si esta en la barra 5 se mueve a
		&& barril.getFin() > inicioDeBarrasPares) {								//la izquierda
			barril.moverseIzquierda(velocidadAleatoria);
		}

		else if (barril.getBase() == this.barras[3].getPiso()	//si esta en la barra 4 se mueve a
		&& barril.getInicio() < this.barras[4].getAncho()) {	//la derecha
			barril.moverseDerecha(velocidadAleatoria);
		}

		else if (barril.getBase() == this.barras[2].getPiso()	//si esta en la barra 3 se mueve a
		&& barril.getFin() > inicioDeBarrasPares) {								//la izquierda
			barril.moverseIzquierda(velocidadAleatoria);
		}

		else if (barril.getBase() == this.barras[1].getPiso()	//si esta en la barra 2 se mueve a
		&& barril.getInicio() < this.barras[1].getAncho()) {	//la derecha
			barril.moverseDerecha(velocidadAleatoria);
		}

		else if (barril.getBase() == this.barras[0].getPiso()	//si esta en la barra 2 se mueve a
		&& barril.getFin() > desaparicionDeBarril) {								//la izquierda
			barril.moverseIzquierda(velocidadAleatoria);
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////////////
					
		else if (!(this.barriles[0].getBase() == this.barras[5].getPiso())	//si tiene la caida de la barra
		&& !(barril.getInicio() < this.barras[5].getAncho())	//en el lado derecho el barril
		|| !(barril.getBase() == this.barras[3].getPiso())		//se cae de ese lado
		&& !(barril.getInicio() < this.barras[3].getAncho())
		|| !(barril.getBase() == this.barras[1].getPiso())
		&& !(barril.getInicio() < this.barras[1].getAncho())) {
			barril.caerseDerecha(2, 1.2);
		}
		
		else {															//si tiene la caida de la barra
			barril.caerseIzquierda(2, 1.2);						//en el lado izquierdo el barril
		}																//se cae de ese lado

	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	public void animacionBarriles(Barril b) {
				
		if(controlMovBarril<10) {
			//de acuerdo a la barra donde se encuentre se simula el movimiento
			if(b.getBase()==this.barras[5].getPiso()
			|| b.getBase()==this.barras[3].getPiso()
			|| b.getBase()==this.barras[1].getPiso()) {
				Image imagen = Herramientas.cargarImagen("barril1.png");
				this.entorno.dibujarImagen(imagen, b.getX(), b.getY()-5, 0);
			}
			else {
				Image imagen = Herramientas.cargarImagen("barril4.png");
				this.entorno.dibujarImagen(imagen, b.getX(), b.getY()-5, 0);
			}
			
		}
		else if(controlMovBarril>=10 
		&& controlMovBarril<=20) {
			if(b.getBase()==this.barras[5].getPiso()
			|| b.getBase()==this.barras[3].getPiso()
			|| b.getBase()==this.barras[1].getPiso()) {
				Image imagen = Herramientas.cargarImagen("barril2.png");
				this.entorno.dibujarImagen(imagen, b.getX(), b.getY()-5, 0);
			}
			else {
				Image imagen = Herramientas.cargarImagen("barril3.png");
				this.entorno.dibujarImagen(imagen, b.getX(), b.getY()-5, 0);
			}
		}
		else if(controlMovBarril>=20 
		&& controlMovBarril<=30) {
			if(b.getBase()==this.barras[5].getPiso()
			|| b.getBase()==this.barras[3].getPiso()
			|| b.getBase()==this.barras[1].getPiso()) {
				Image imagen = Herramientas.cargarImagen("barril3.png");
				this.entorno.dibujarImagen(imagen, b.getX(), b.getY()-5, 0);
			}
			else {
				Image imagen = Herramientas.cargarImagen("barril2.png");
				this.entorno.dibujarImagen(imagen, b.getX(), b.getY()-5, 0);
			}
		}
		else if(controlMovBarril>30 
		&& controlMovBarril<=40){
			if(b.getBase()==this.barras[5].getPiso()
			|| b.getBase()==this.barras[3].getPiso()
			|| b.getBase()==this.barras[1].getPiso()) {
				Image imagen = Herramientas.cargarImagen("barril4.png");
				this.entorno.dibujarImagen(imagen, b.getX(), b.getY()-5, 0);
			}
			else {
				Image imagen = Herramientas.cargarImagen("barril1.png");
				this.entorno.dibujarImagen(imagen, b.getX(), b.getY()-5, 0);
			}
				
		}
		
		//se inicializa
		if(controlMovBarril>=40) {
			controlMovBarril=0;
		}
	}
	
	/////////////////////////////////////
	
	public void generadorDeBarril(int numBarril) {
		//BARRIL
			
		this.cronometroBarril[numBarril]++;
		
		this.animacionBarriles(this.barriles[numBarril]);
		
		if(this.cronometroBarril[numBarril]<25){														
			this.DonkeyBarril();						
		}
	
		//MOVIMIENTO Y VELOCIDAD ALEATORIA DEL BARRIL
		this.movimientoBarriles(numBarril, velocidadAleatoria[numBarril]);
		
		
		if (this.barriles[numBarril].getFin() < -20) {				//si llego al final de su recorrido que
															//aparezca de nuevo arriba
			
			this.velocidadAleatoria[numBarril] = randomVelocidad[numBarril].nextInt(5);	//se genera velocidad aleatoria
			
			while (this.velocidadAleatoria[numBarril] == 0) {			//evitando que la velocidad sea 0 ya que
															//se quedaria detenido el barril
				
				this.velocidadAleatoria[numBarril] = randomVelocidad[numBarril].nextInt(5);	//mientras sea cero que siga
																		//genereando randoms
			}
			
			this.salidaDeBarril[numBarril] += this.randomSalidaDeBarril[numBarril].nextInt(100);
			
			//cuando se obtiene una velocidad aleatoria diferente de cero se construye un nuevo barril 1
			this.barriles[numBarril] = new Barril(100, this.barras[5].getPiso() - 20, 40);
			this.cronometroBarril[numBarril]=0;	//el temporizador de control se inicializa
			
		}
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void fabricaDeBarriles(int cantidad) {
		
		for(int i=0; i<cantidad ;i++) {
			if(this.tick>this.salidaDeBarril[i]) {
				this.generadorDeBarril(i);
			}
		}
	}
		
	//Carga y dibuja la imagen del barril detenido durante la pausa, gano y perdió
	public void barrilDetenido() {
		Image imagen = Herramientas.cargarImagen("barril1.png");
		for(int i=0; i<this.barriles.length;i++) {
			this.entorno.dibujarImagen(imagen, this.barriles[i].getX(), this.barriles[i].getY(),0);
		}
		
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//MOVIMIENTO DEL AGENTE
	
	//TECLAS PRESIONADAS
	public boolean teclasPresionadas() {
		return this.entorno.estaPresionada(this.entorno.TECLA_ABAJO)
		||	this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA)
		||	this.entorno.estaPresionada(this.entorno.TECLA_DERECHA)
		||	this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA)
		|| 	this.entorno.estaPresionada(this.entorno.TECLA_ESPACIO);
	}
	
	//EN EL PISO
	public boolean enPiso(Barra [] b) {
		for (int i = 0; i < b.length; i++) {
			if(this.agente.sobreLineaDeBarra(this.barras[i])) {
				return true;
			}
		}
		return false;
	}
		
	//MOVIMIENTO DE IZQUIERDA O DERECHA SOBRE BARRAS EN LOS LIMITES DEL ENTORNO
	public void movEnElEntornoBarras(Entorno e) {
		if ( this.limiteEntornoDer() && entorno.estaPresionada(entorno.TECLA_DERECHA)
		&& (!entorno.estaPresionada(entorno.TECLA_ABAJO) && !entorno.estaPresionada(entorno.TECLA_ARRIBA)) ){			
			this.controlMov=1;								//si no sobrepasa el limite
			this.agente.moverseDerecha();				//derecho del entorno,se mueve
		}												
		

		if (this.limiteEntornoIzq() && entorno.estaPresionada(entorno.TECLA_IZQUIERDA)
		&& !entorno.estaPresionada(entorno.TECLA_ABAJO) && !entorno.estaPresionada(entorno.TECLA_ARRIBA)) {
			this.controlMov=2;							//si no sobrepasa el limite
			this.agente.moverseIzquierda();			//izquierdo del entorno,se mueve
		}												//hacia la izquierda
	}
	
	//MOVIMIENTO DE IZQUIERDA O DERECHA DURANTE EL SALTO EN LOS LIMITES DEL ENTORNO
	public void movEnElEntornoSalto(Entorno e) {
		if (this.limiteEntornoDer() && entorno.estaPresionada(entorno.TECLA_DERECHA)) {			
														//si no sobrepasa el limite
			this.agente.moverseDerechaSalto(1);			//derecho del entorno,se mueve
		}												//hacia la derecha durante el salto

		if (this.limiteEntornoIzq() && entorno.estaPresionada(entorno.TECLA_IZQUIERDA)) {			
														//si no sobrepasa el limite
			this.agente.moverseIzquierdaSalto(1);		//izquierdo del entorno,se mueve
		}												//hacia la izquierda durante el salto
	}
	
	//MOVIMIENTO SOBRE BARRAS
	public void movSobreBarras(Barra [] b) {
		
		if (this.enPiso(this.barras)){					//si esta a nivel de alguna barra
			this.movEnElEntornoBarras(this.entorno); 	//se puede mover en los limites del entorno
		}
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//EN ESCALERAS ARRIBA
	public boolean enEscalerasArriba(Escalera [] e) {
		for (int i = 0; i < e.length; i++) {
			if(this.agente.enEscaleraArriba(this.escaleras[i])) {
				return true;
			}
		}
		return false;
	}
	
	//EN ESCALERAS ABAJO
	public boolean enEscalerasAbajo(Escalera [] e) {
		for (int i = 0; i < e.length; i++) {
			if(this.agente.enEscaleraAbajo(this.escaleras[i])) {
				return true;
			}
		}
		return false;
	}
	
	//MOVIMIENTO SOBRE ESCALERAS
	public void movSobreEscaleras() {
		if(enEscalerasArriba(this.escaleras) && entorno.estaPresionada(entorno.TECLA_ARRIBA)) {
			this.controlMov=3;
			this.agente.moverseArriba();
			this.cronometroSalto=this.agente.getVelSalto()+1;
		}
		if(enEscalerasAbajo(this.escaleras) && entorno.estaPresionada(entorno.TECLA_ABAJO)) {
			this.controlMov=3;
			this.agente.moverseAbajo();
			this.cronometroSalto=this.agente.getVelSalto()+1;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//SALTO
	public void saltoDeAgente() {
		//para que no pueda saltar cuando se encuentra sobre una escalera
		if( !(this.enEscalerasArriba(this.escaleras)) && !(this.enEscalerasAbajo(this.escaleras)) ){
			
			//si presiona espacio para saltar
			if (this.entorno.sePresiono(this.entorno.TECLA_ESPACIO)
			//si no esta cayendo (para que no pueda saltar durante la caida)
			&& !this.enElAire()) {
				//y si termino el salto anterior que dura 68 tick
				if (this.cronometroSalto > this.agente.getVelSalto()) {
					// pone el cronometro en 0 para dar lugar al salto
					this.cronometroSalto = 0;	
				}
			}

			// el cronometro antes puesto en 0 comienza a correr
			this.cronometroSalto++;

			//primer parte del salto, elevacion del agente
			if (this.cronometroSalto < (this.agente.getVelSalto()/2)+1) {
				this.agente.iniciaSalto(2);// metodo que lo mueve hacia arriba
				this.salto=true;
				if(this.cronometroSalto == 1) {
					Herramientas.cargarSonido("sonidoSalto.wav").start();
				}
				
				
				this.movEnElEntornoSalto(this.entorno);
			}
			// segunda parte del salto, caida del agente
			else if (this.cronometroSalto >= (this.agente.getVelSalto()/2)-1 
			&& this.cronometroSalto <= this.agente.getVelSalto()) {
				this.agente.terminaSalto(2);// metodo que lo lleva nuevamente para abajo
				this.salto=true;
				
				this.movEnElEntornoSalto(this.entorno);
			}
			else {
				this.salto=false;//controla la carga de imagenes durante el salto
			}
			
		}//fin del if para que no salte en una escalera
	}
	
	//Limite de salto en base
	public boolean limiteEntornoDer() {
		return this.agente.getX()+this.agente.getAncho()/2<this.entorno.ancho();
	}
	
	//Limite de salto en base
	public boolean limiteEntornoIzq() {
		return this.agente.getX()-this.agente.getAncho()/2>0;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//CAIDA
	public void caidaDeAgente() {
		
		if(!this.salto) {
			//desde la barra mas alta hasta la barra con indice 1
			for (int i = 5; i > 0; i--) { 	
				
				// caida en barra impar (1, 3 y 5)
				if(i%2!=0) {
					if (this.agente.sobreLineaDeBarra(this.barras[i])			//si esta en una barra
					&& this.agente.getX() - 12 > this.barras[i].getAncho()) {	//si supera el ancho de la barra
						enElAire[i-1]=1;										//se pone en 1 el control de esa barra
					}
					if (enElAire[i-1] == 1 										//si el control esta en 1
					&& !this.agente.sobreLineaDeBarra(this.barras[i-1])) {		//y si todavia no llega al piso anterior						
						this.agente.cae(2);										//el agente cae
					} else {
						enElAire[i-1] = 0;										//si no sucede lo anterior el control
					}															//se mantiene en 0
				}
				
				// caida en barra par (2 y 4)
				else {
					if (this.agente.sobreLineaDeBarra(this.barras[i]) 			//si esta en una barra
					&& this.agente.getX() + 12 < 100) {							//si supera el ancho de la barra
						enElAire[i-1]=1;										//se pone en 1 el control de esa barra
					}
					if (enElAire[i-1] == 1 										//si el control esta en 1
					&& !this.agente.sobreLineaDeBarra(this.barras[i-1])) {		//y si todavia no llega al piso anterior
						this.agente.cae(2);										//el agente cae
					} else {
						enElAire[i-1] = 0;										//si no sucede lo anterior el control
					}															//se mantiene en 0
				}
			}
		}
	}
	
	//verifica si hay colision con los barriles
	public boolean colisionAgenteYBarriles(int cantidad) {
		for(int i=0; i<cantidad; i++) {
			if(this.agente.colision(barriles[i])) {
				return true;
			}
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//////Métodos que cargan las imagenes del agente de acuerdo a su estado/////////
	
	//Metodo que detecta si esta parado (usado para la carga correcta de imagenes)
	public boolean estaParado() {
		
		//si no esta presionada ninguna tecla
		return ( !this.teclasPresionadas()
				
		//y no esta en escaleras	
	    && (!this.enEscalerasArriba(this.escaleras) && !this.enEscalerasAbajo(this.escaleras))
		
		//si no esta cayendo
		&& !this.enElAire() 
		
		//si esta presionando abajo o arriba y no esta en las escaleras
		|| ( ( (this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA)
		|| this.entorno.estaPresionada(this.entorno.TECLA_ABAJO))
		&& (!this.enEscalerasArriba(this.escaleras) || !this.enEscalerasAbajo(this.escaleras)))
		
		//si esta presionando derecha e izquierda al mismo tiempo y no esta en las escaleras
		|| ( (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA)
		&& this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA))
		&& (!this.enEscalerasArriba(this.escaleras) || !this.enEscalerasAbajo(this.escaleras))  ) )   )
		 
		//si todo lo anterior no se cumple y no esta saltando
		&& this.cronometroSalto>this.agente.getVelSalto();		
	}
	
	//Metodo que detecta si esta parado en una escalera (usado para la carga correcta de imagenes)
	public boolean estaParadoEnEscalera() {
		
		//si no esta presionada ninguna tecla o se presiona izquierda o derecha
		return  ((!this.teclasPresionadas() 
		|| this.entorno.estaPresionada(this.entorno.TECLA_DERECHA)
		|| this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA))
		//y esta en escaleras
		&& (this.enEscalerasArriba(this.escaleras) || this.enEscalerasAbajo(this.escaleras))  
		
		//o si esta presionando derecha, pero no junto a arriba o abajo y esta en las escaleras
		|| ( ((this.entorno.estaPresionada(this.entorno.TECLA_DERECHA)
		&& (!this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA)
		|| !this.entorno.estaPresionada(this.entorno.TECLA_ABAJO))))		
		&& (this.enEscalerasArriba(this.escaleras) || this.enEscalerasAbajo(this.escaleras)))
		
		//o si esta presionando izquierda, pero no junto a arriba o abajo y esta en las escaleras
		&& ( ((this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA)
		&& (!this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA)
		|| !this.entorno.estaPresionada(this.entorno.TECLA_ABAJO))))		
		&& (this.enEscalerasArriba(this.escaleras) || this.enEscalerasAbajo(this.escaleras)))
		
		|| (this.entorno.estaPresionada(this.entorno.TECLA_ESPACIO) 
		&& (this.enEscalerasArriba(this.escaleras) || this.enEscalerasAbajo(this.escaleras))) )
		
		//si se cumple todo lo anterior y ademas no presiona arriba o abajo en las escaleras
		&& (!this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA)
		&& !this.entorno.estaPresionada(this.entorno.TECLA_ABAJO) 
		&& (this.enEscalerasArriba(this.escaleras) || this.enEscalerasAbajo(this.escaleras))); 
	}
	
	//Metodo que verifica si el agente puede caminar
	public boolean puedeCaminaALaDerecha() {
		//si presiona la DERECHA
		if(this.entorno.estaPresionada(this.entorno.TECLA_DERECHA)) {
			//para que no "mueva" los pies cuando salte, solo lo hace sobre las barras
			if(this.enPiso(this.barras)) {
				//controlMov()==1 se usa para cargar la imagenes que miran hacia la derecha
				if(this.controlMov==1 
				//que no las cargue estas imagenes cuando se presionan otras teclas
				&& (!this.entorno.estaPresionada(this.entorno.TECLA_ABAJO) 
				&& !this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA))) {
					return true;				
				}
			}
		}
		return false;
	}
	
	//Método que alterna dos imagenes cuando el agente camina a la derecha
	public void caminaALaDerecha() {
		//inicia el cronometro de control
		this.cronometroAgenteDer++;
		if(this.cronometroAgenteDer<10) {	
			this.agenteDer1(this.entorno);	//carga la 1er imagen
		}
		else if(this.cronometroAgenteDer>=10 && this.cronometroAgenteDer<=19){
			this.agenteDer2(this.entorno);	//carga la 2da imagen
			if(this.cronometroAgenteDer>=19) {
				this.cronometroAgenteDer=0; //se resetea el cronometro para que repita todo
			}
		}
	}
	
	
	public boolean puedeCaminarALaIzquierda() {
		//si presiona la IZQUIERDA
		if(this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA)){
			//para que no "mueva" los pies cuando salte, solo lo hace sobre las barras
				if(this.enPiso(this.barras)){
					//controlMov()==2 se usa para cargar la imagenes que miran hacia la izquierda
					if(this.controlMov==2 
					//que no las cargue estas imagenes cuando se presionan otras teclas
					&& (!this.entorno.estaPresionada(this.entorno.TECLA_ABAJO) 
					&& !this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA))) {
						return true;
					}
				}
			}
		return false;
	}
	
	//Método que alterna dos imagenes cuando el agente camina a la izquierda
	public void caminaALaIzquierda() {
		this.cronometroAgenteIzq++;			//inicia el cronometro de control	
		if(this.cronometroAgenteIzq<10) {
			this.agenteIzq1(this.entorno);	//carga la 1er imagen
		}
		else if(this.cronometroAgenteIzq>=10 && this.cronometroAgenteIzq<=19){
			this.agenteIzq2(this.entorno);	//carga la 2da imagen
			if(this.cronometroAgenteIzq>=19) {
				this.cronometroAgenteIzq=0;	//se resetea el cronometro para que repita todo
			}
		}
	}
	
	
	//Método que verifica si está en escaleras
	public boolean puedeSubirBajarEscalera() {
		return (this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA)
		|| (this.entorno.estaPresionada(this.entorno.TECLA_ABAJO))
		
		|| (this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA)
		&& (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA)))
		
		|| (this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA)
		&& (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA)))
		
		|| (this.entorno.estaPresionada(this.entorno.TECLA_ABAJO)
		&& (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA)))
		
		|| (this.entorno.estaPresionada(this.entorno.TECLA_ABAJO)
		&& (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA))) )
		
		//si se cumple lo anterior y ademas no esta sobre piso
		&& !this.enPiso(this.barras);
	}
	
	//Método que alterna dos imagenes cuando el agente sube-baja escaleras
	public void subirBajarEscaleras() {
		//controlMov()==3 se usa para cargar la imagenes subiendo o bajando
		if(this.controlMov==3) {
			this.cronometroAgenteEsc++;			//inicia el cronometro de control
			if(this.cronometroAgenteEsc<10) {
				this.agenteEsc1(this.entorno);	//carga la 1er imagen
			}
			else if(this.cronometroAgenteEsc>=10 && this.cronometroAgenteEsc<=19){
				this.agenteEsc2(this.entorno);	//carga la 2da imagen
				if(this.cronometroAgenteEsc>=19) {
					this.cronometroAgenteEsc=0;	//se resetea el cronometro para que repita todo
				}
			}
			
		}
	}
	
	//Método que detecta si el agente está saltando
	public boolean puedeSaltar() {
		//Si esta saltando carga estas imagenes
		return this.cronometroSalto<this.agente.getVelSalto()+1;
	}
	
	//Método que carga correctamente las imagenes del agente cuando salta
	public void salta() {
		//si presiona derecha durante el salto carga la imagen hacia la derecha
		if(this.entorno.estaPresionada(this.entorno.TECLA_DERECHA) 
		&& (!this.enEscalerasArriba(this.escaleras) && !this.enEscalerasAbajo(this.escaleras))) {
			this.agenteDer1(this.entorno);
		}
		//si presiona izquierda durante el salto carga la imagen hacia la izquierda
		else if(this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA)
		&& (!this.enEscalerasArriba(this.escaleras) && !this.enEscalerasAbajo(this.escaleras))) {
			this.agenteIzq1(this.entorno);
		}
		//si presiona cualquier otra tecla de control durante el salto carga la imagen hacia la derecha
		else if(this.entorno.estaPresionada(this.entorno.TECLA_ESPACIO)
		&& (!this.enEscalerasArriba(this.escaleras) && !this.enEscalerasAbajo(this.escaleras))
		|| this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA)
		|| this.entorno.estaPresionada(this.entorno.TECLA_ABAJO)) {
			this.agenteDer1(this.entorno);
		}
		//si no presiona ninguna tecla durante el salto carga la imagen hacia la derecha
		else if(!this.teclasPresionadas() && (!this.enEscalerasArriba(this.escaleras) 
		&& !this.enEscalerasAbajo(this.escaleras))){
			
			this.agenteDer1(this.entorno);
		}
	}
	
	
	//Método que verifica si el agente esta en el aire cayendo
	public boolean enElAire() {
		for (int i = 0; i < this.enElAire.length; i++) {
			if(enElAire[i]==1) {
				return true;
			}
		}
		return false;
	}
	
	//Método que carga las imágenes del agente cayendo
	public void cae() {
		//si presiona derecha mientras cae se carga la imagen hacia la derecha
		if(this.entorno.estaPresionada(this.entorno.TECLA_DERECHA)) {
			this.agenteDer1(this.entorno);
		}
		//si presiona izquierda mientras cae se carga la imagen hacia la izquierda
		else if(this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA)) {
			this.agenteIzq1(this.entorno);
		}
		//si no presiona teclas durante la caida carga al agente parado
		else if(!this.teclasPresionadas() || this.entorno.estaPresionada(this.entorno.TECLA_ABAJO)
		|| this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA)
		
		|| (this.entorno.estaPresionada(this.entorno.TECLA_ABAJO)
		&& this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA)) 
		
		|| this.entorno.estaPresionada(this.entorno.TECLA_ESPACIO)) {
			this.agenteParado(this.entorno);
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	
	//Los métodos que siguen Cargan/Dibujan las imagenes del agente
	
	public void agenteParado(Entorno e){
		Image imagenJuego = Herramientas.cargarImagen("agenteParado.png");
		e.dibujarImagen(imagenJuego, this.agente.getX(), this.agente.getY()-2, 0);
	}
	
	public void agenteDer1(Entorno e){
		Image imagenJuego = Herramientas.cargarImagen("agenteDer1.png");
		e.dibujarImagen(imagenJuego, this.agente.getX()-5, this.agente.getY(), 0);
	}
	
	public void agenteDer2(Entorno e){
		Image imagenJuego = Herramientas.cargarImagen("agenteDer2.png");
		e.dibujarImagen(imagenJuego, this.agente.getX()-5, this.agente.getY(), 0);
	}
	
	public void agenteIzq1(Entorno e){
		Image imagenJuego = Herramientas.cargarImagen("agenteIzq1.png");
		e.dibujarImagen(imagenJuego, this.agente.getX(), this.agente.getY(), 0);
	}
	
	public void agenteIzq2(Entorno e){
		Image imagenJuego = Herramientas.cargarImagen("agenteIzq2.png");
		e.dibujarImagen(imagenJuego, this.agente.getX(), this.agente.getY(), 0);
	}
	
	public void agenteEscParado(Entorno e){
		Image imagenJuego = Herramientas.cargarImagen("agenteEscParado.png");
		e.dibujarImagen(imagenJuego, this.agente.getX(), this.agente.getY()-3, 0);
	}
	
	public void agenteEsc1(Entorno e){
		Image imagenJuego = Herramientas.cargarImagen("agenteEsc1.png");
		e.dibujarImagen(imagenJuego, this.agente.getX(), this.agente.getY(), 0);
	}
	
	public void agenteEsc2(Entorno e){
		Image imagenJuego = Herramientas.cargarImagen("agenteEsc2.png");
		e.dibujarImagen(imagenJuego, this.agente.getX(), this.agente.getY(), 0);
	}
	
	public void agenteVacuna(Entorno e){
		Image imagenJuego = Herramientas.cargarImagen("agenteVacuna.png");
		e.dibujarImagen(imagenJuego, this.agente.getX()+10, this.agente.getY()-5, 0);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	//Método encargado de inicializar las variables de instancia en inicio o reinicio
	public void inicializar() {
		
		//cuenta las veces que se reinicia el juego
		this.contadorDeReinicios++;
		
		//verifica el puntaje. Si es mayor, lo pone como puntaje máximo
		this.puntajeMaximo=this.maximoPuntaje();
		
		//agrega el puntajeActual al arreglo de puntajes
		//se le pasa por parámetro otro método que devuelve el arreglo nuevo
		//con el puntajeActual incorporado y después copia sus valores a this.puntajes
		this.copiarPuntajes(this.agregarPuntaje());
			
			
		/////////////////////////////////////INICIALIZAN LAS VARIABLES DE INSTANCIA////////////////////////////////////
					

		//inicializan los Sonidos.
		this.sonidoInicio = Herramientas.cargarSonido("sonidoInicio.wav");
		this.sonidoFondoJuego = Herramientas.cargarSonido("sonidoFondo.wav");		
	    this.sonidoGano = Herramientas.cargarSonido("sonidoGano.wav");
	    this.sonidoPerdio = Herramientas.cargarSonido("sonidoPerdio.wav");
		
		
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//CAPTURA DEL TICK Y OTROS TEMPORIZADORES
		this.tick = 0;
		this.segundos = 0;						//simula el paso de segundos
		this.cronometroSalto = 100000;			//inciado en numero alto para que no inicie saltando solo
		for(int i=0; i<this.cronometroBarril.length; i++) {
			this.cronometroBarril[i] = 0;		//usados para hacer aparecer un barril con velocidad aleatoria
		}
		this.cronometroAgenteDer=0;				//controla la carga de imagen al caminar a la derecha
		this.cronometroAgenteIzq=0;				//controla la carga de imagen al caminar a la izquierda
		this.cronometroAgenteEsc=0;				//controla la carga de imagen al subir o bajar por la escalera
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//CONTROL DEL JUEGO - COMENZAR Y PAUSAR JUEGO
		this.comenzar = false;
		this.pausa = 0;
		
		//AGENTE
		this.agente = new Agente(30, 570, 25, 40,this.velocidadDelAgente,68,this.ajusteImpactoX,this.ajusteImpactoY);
		
		for(int i=0; i<this.enElAire.length;i++) {	//inicializa en cero a cada posición de este arreglo que 
			this.enElAire[i]=0;						//se usa para controlar desde que barra cae el agente
		}
		
		this.salto=false;
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//BARRAS
		this.distanciaBarras = 100;
		this.barras[0] = new Barra(405, 600, 810, 20);	//barra base
		for (int i = 1; i < barras.length; i++) {	//barras restantes
			if (i % 2 == 0) {	//construye cada barra de acuerdo a si es par o no
				this.barras[i] = new Barra(405 + 50, 600 - (this.distanciaBarras * i), 710, 20);
			} else {
				this.barras[i] = new Barra(405 - 50, 600 - (this.distanciaBarras * i), 710, 20);
			}
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//ESCALERAS
		for (int i = 0; i < escaleras.length; i++) {
			int distanciaEscaleras = 100;
			if (i % 2 == 0) { //construye cada escalera de acuerdo a si es par o no
				this.escaleras[i] = new Escalera(810 - 200, 610 - 70 - (distanciaEscaleras * i), 25, 100);
			} else {
				this.escaleras[i] = new Escalera(200, 610 - 70 - (distanciaEscaleras * i), 25, 100);
			}
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//DONKEY
		this.donkey = new Donkey(30, 40, 60, 80);
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//BARRILES
		for(int i=0; i<this.barriles.length;i++) {
			this.barriles[i] = new Barril(100, this.barras[5].getPiso() - 20, 40);
		}
		
		//SALIDAS RANDOM DE LOS BARRILES
		for(int i=0; i<this.salidaDeBarril.length; i++) {
			this.randomSalidaDeBarril[i] = new Random();
			this.salidaDeBarril[i] = randomSalidaDeBarril[i].nextInt(1024);
		}
		
		//VELOCIDADES RANDOM INICIALES DE LOS BARRILES
		for(int i=0; i<this.velocidadAleatoria.length; i++) {
			this.randomVelocidad[i] = new Random();
		
			this.velocidadAleatoria[i] = randomVelocidad[i].nextInt(4);
			while (this.velocidadAleatoria[i] == 0) {
				this.velocidadAleatoria[i] = randomVelocidad[i].nextInt(4);
			}
		}
		
		//usado para animar con imagenes a cada barril
		this.controlMovBarril=0;	
	}
	
	//Verifica si el puntaje actual superó al resto de los puntajes
	public int maximoPuntaje() {
		int max=0;
		for(int i=0; i<this.puntajes.length; i++) {
			if(this.puntajes[i]>max) {
				max=this.puntajes[i];
			}	
		}
		if(this.puntajeActual>max) {
			return this.puntajeActual;
		}
		return max;
	}
	
	//usado para resetear los puntos si el usuario lo desea
	public void resetearPuntajes() {
		//guarda los últimos n puntajes
		for(int i=0; i<this.puntajes.length; i++) {
			this.puntajes[i] = 0;
		}
	}
	
	public int [] agregarPuntaje() {
		
		int [] nuevo = new int [this.puntajes.length];
		
		//pone el ultimo intento en la posición cero de nuevo
		nuevo[0]=this.puntajeActual;
		
		for(int i=1; i<nuevo.length; i++) {
			//copia los puntajes de this.puntajes menos su último elemento
			//ya que en la posición cero se ingresa el puntaje actual por lo que se van corriendo
			nuevo[i]=this.puntajes[i-1];
		}
		
		return nuevo;
	}
	
	//se encarga de copiar los puntajes de nuevo en this.puntajes[i]
	public void copiarPuntajes(int [] nuevo) {
		for(int i=0; i<this.puntajes.length; i++) {
			this.puntajes[i]=nuevo[i];
		}
	}
	
	//imprime la lista de puntajes
	public void imprimirPuntajesEnPantalla() {
		int posicionX=20;
		int posicionY=145;
		int saltoDeLinea=posicionY+30;
		
		//rectangulo que hace mas oscura la pantalla pausada
		this.entorno.dibujarRectangulo(90,270, 250, 300,
		0, new Color(0,0,0,200));
		this.entorno.cambiarFont(Font.SANS_SERIF, posicionX, new Color(255,255,102));
		
		this.entorno.escribirTexto("Puntaje máximo: "+this.puntajeMaximo, posicionX, posicionY);
		this.entorno.cambiarFont(Font.SANS_SERIF, posicionX, new Color(255,128,0));
		this.entorno.escribirTexto("Puntajes Anteriores: ", posicionX, saltoDeLinea);
		
		for(int i=0; i<this.puntajes.length; i++) {	
				saltoDeLinea+=30;
				this.entorno.escribirTexto("==>"+this.puntajes[i], posicionX, saltoDeLinea);	
		}	
	}
	
	public int calcularPuntaje() {
		
		if(this.gano()) {
			this.puntajeActual=500-this.segundos*2;
			return this.puntajeActual;
		}
		else if(this.perdio()){
			this.puntajeActual=this.segundos*2+5;
			return this.puntajeActual;
		}
		else if(this.pausa==0){
			this.puntajeActual=this.segundos*2+5;
		}
		
		return this.puntajeActual;
	}
	
	
	/********************************************************************************************************/
	/********************************************************************************************************/

	////////////////////////////////////////METODO MAIN////////////////////////////////////////////////
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		Juego juego = new Juego();
	}

}//FIN DE LA CLASE JUEGO

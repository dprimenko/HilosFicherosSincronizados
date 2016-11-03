import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class ControladorDeFicheros {
	private PrintWriter fichero;
	
	
	public ControladorDeFicheros(String nomFichero) {
		try {
			fichero = new PrintWriter(new FileWriter(nomFichero));
			
		}
		catch(IOException e) {
			System.err.println("Error al crear el fichero: " + e.getMessage());
		}
	}
	
	public void println(String cadena) {
		fichero.println(cadena);
	}
	
	public void print(String cadena) throws InterruptedException {
		fichero.print(cadena);
	}
	
	public void close() {
		fichero.close();		
	}
}

class Escritor extends Thread {
	
	private ControladorDeFicheros destino;	
	private String contenido = "";
	private static Object mutex = new Object();
	
	public Escritor(ControladorDeFicheros fichero) {
		this.destino = fichero;
	}
	
	public void fraseAdd(String cadena) {
		this.contenido += cadena;
	}
	
	@Override
	public void run() {
		//destino.println(contenido);
		synchronized(mutex) {
			for (int i = 0; i < contenido.length(); i++) {
				try {
					destino.print(contenido.substring(i, i+1));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			destino.println("");
		}
	}
}


public class PruebaFicherosSincronizado {
	public static void main(String[] args) throws InterruptedException {
		ControladorDeFicheros cf;
		String ruta = "/home/usuario/Documentos/poema.txt";
		
		cf = new ControladorDeFicheros(ruta);
		
		String parrafo1 = "Hola caracola, que te cuentas? No me encuentro muy bien caracol";
		String parrafo2 = "En un lugar de la Mancha... y que te cuentas tu ilipoia";	
		Escritor cervantes = new Escritor(cf);
		Escritor shakespeare = new Escritor(cf);
		
		shakespeare.fraseAdd(parrafo1);
		cervantes.fraseAdd(parrafo2);
		
		shakespeare.start();
		cervantes.start();
		
		shakespeare.join();
		cervantes.join();
		
		cf.close();
		
		System.out.println("Los datos se han escrito en el fichero");
	}
}

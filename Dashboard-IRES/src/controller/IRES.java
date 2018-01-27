package controller;


public class IRES {

	public static void main(String[] args) {
		//Stabiliamo la connessione con il DB tramite la user e la password
		//inserite in fase di esecuzione del sistema
		Storage.inizializza(args);
		
		//Inizializziamo il componente GestoreSegnali e lo facciamo partire
		GestoreSegnali gs = new GestoreSegnali(args);
		Thread gsThread = new Thread(gs);
		gsThread.start();
		
		
		
	}

}

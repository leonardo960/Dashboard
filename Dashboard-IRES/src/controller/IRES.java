package controller;


public class IRES {

	public static void main(String[] args) {
		GestoreSegnali gs = new GestoreSegnali();
		Storage.inizializza(args);
		
		long begin = System.currentTimeMillis();
		
		Thread gsThread = new Thread(gs);
		gsThread.start();
		try {
			gsThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		
		long timeOfExecution = (end - begin) / 1000L;
		
		System.out.println("Tempo di esecuzione: " + (timeOfExecution));
	}

}

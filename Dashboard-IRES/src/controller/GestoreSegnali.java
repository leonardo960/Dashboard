package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.ServerSocket;
//import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONException;
import org.json.JSONObject;

import model.ClientWrapper;
import model.Cluster;
import model.FinestraTemporale;
import model.Robot;
import model.Segnale;

public class GestoreSegnali implements Runnable {
	private ConcurrentHashMap<String, Cluster> clusters;
	private ConcurrentHashMap<String, Robot> robots;
	private ConcurrentLinkedQueue<Segnale> segnali;
	private ServerSocket serverForDashboards;
	private LinkedList<ClientWrapper> clients;
	private Thread IRHandler;
	//private long timer;
	public GestoreSegnali(String[] args){
		clusters = new ConcurrentHashMap<String, Cluster>();
		robots = new ConcurrentHashMap<String, Robot>();
		segnali = new ConcurrentLinkedQueue<Segnale>();
		clients = new LinkedList<ClientWrapper>();
		IRHandler = new Thread(){
			private Connection con;
			public void run(){
				try {
					Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				try {
					con = DriverManager.getConnection("jdbc:mysql://localhost/?useSSL=true", args[0], args[1]);
					con.createStatement().execute("use dashboard");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(true){
					System.out.println("Inizio calcolo e invio IR");
					//System.out.println("Segnali rimasti pre-calcolo e invio: " + segnali.size());
					//Finiamo di effettuare le ultime query se ne rimangono
					Storage.commitChanges();
					
					//Calcolo il dato e lo invio
					calcolaIR(con);
					
					//Resettiamo il cooldown
					//timer = System.currentTimeMillis();
					
					//System.out.println("Segnali rimasti post-calcolo e invio:" + segnali.size());
				}
			}
		};
		IRHandler.start();
		try {
			serverForDashboards = new ServerSocket(60012);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void analizzaSegnale(){
		if(segnali.isEmpty()){
			return;
		}else{
			//Segnale segnale = segnali.removeFirst();
			Segnale segnale = segnali.remove();
			Robot robot = null;
			Cluster cluster = null;
			//Riconosciamo il Robot e il Cluster
			if(!robots.containsKey(segnale.getRobotID())){
				//Prima creiamo il cluster se non c'è
				if(!clusters.containsKey(segnale.getClusterID())){
					cluster = new Cluster(segnale.getClusterID());
					clusters.put(cluster.getID(), cluster);
				}else{
					cluster = clusters.get(segnale.getClusterID());
				}
				robot = new Robot(segnale.getRobotID(), segnale.getClusterID());
				robots.put(robot.getID(), robot);
				cluster.getRobots().add(robot);
			}else{
				robot = robots.get(segnale.getRobotID());
				cluster = clusters.get(segnale.getClusterID());
			}
			//Analizziamo il segnale
			if(segnale.getValue()){
				robot.decrementDownSensors();
				robot.setSensor(segnale.getSensorNumber(), true);
				if(robot.getDownSensors() == 0){
					Storage.chiudiFinestraTemporaleRobot(segnale);
					cluster.decrementRobotDown();
					if(cluster.getRobotDown() == 0){
						Storage.chiudiFinestraTemporaleCluster(segnale);
					}
				}
			}else{
				//Parte Robot
				if(robot.getDownSensors() == 0){
					robot.incrementDownSensors();
					Storage.apriFinestraTemporaleRobot(segnale);
					robot.setSensor(segnale.getSensorNumber(), false);
					if(cluster.getRobotDown() == 0){
						Storage.apriFinestraTemporaleCluster(segnale);
					}
					cluster.incrementRobotDown();
				}else{
					robot.incrementDownSensors();
					robot.setSensor(segnale.getSensorNumber(), false);
				}				
			}
		}
	}

	private void calcolaIR(Connection con){
		//System.out.println("Inizio calcolo IR");
		//long begin = System.currentTimeMillis();
		
		Timestamp oneHourAgo = new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000));
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		//System.out.println("One hour ago: " + oneHourAgo);
		//System.out.println("Now: " + now);
		System.out.println("Inizio recupero finestre dal db");
		HashMap<String, LinkedList<FinestraTemporale>> finestreTemporali = Storage.prelevaFinestreTemporali(con);
		System.out.println("Fine recupero finestre dal db");
		long downTime = 0;
		for(String id : finestreTemporali.keySet()){
			downTime = 0;
			for(FinestraTemporale ft : finestreTemporali.get(id)){
				if(ft.getSogliaDestra() != null){
					if(ft.getSogliaSinistra().before(oneHourAgo)){
						downTime += ft.getSogliaDestra().getTime()/1000L - oneHourAgo.getTime()/1000L;
					}else{
						downTime += ft.getSogliaDestra().getTime()/1000L - ft.getSogliaSinistra().getTime()/1000L;
					}	
				}else{
					downTime += now.getTime()/1000L - ft.getSogliaSinistra().getTime()/1000L;
				}
			}
			double preciseDownTime = downTime / 36.0;
			
			//Tronco fino alla prima cifra decimale
			BigDecimal bd = new BigDecimal(String.valueOf(preciseDownTime)).setScale(1, BigDecimal.ROUND_FLOOR);
			preciseDownTime = bd.doubleValue();
			
			//Arrotondiamo per eccesso, come da specifica
			preciseDownTime = Math.ceil(preciseDownTime);
			
			byte IR = (byte) preciseDownTime;
			
			if(id.charAt(0) == 'C'){
				clusters.get(id).setIR(IR);
			}else{
				robots.get(id).setIR(IR);
			}
		}
		System.out.println("Calcolo IR completato. Invio del dato alle Dashboard");
		//Invio il dato calcolato
		for(ClientWrapper client : clients){
			try {
				HashMap<String, Robot> r_clone = new HashMap<String, Robot>(robots);
				HashMap<String, Cluster> c_clone = new HashMap<String, Cluster>(clusters);
				client.getClientOut().writeObject(c_clone);
				client.getClientOut().writeObject(r_clone);
				client.getClientOut().flush();
				client.getClientOut().reset();
				System.out.println("Dato inviato.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Storage.rimuoviFinestreInattive(oneHourAgo);
		
		//long end = System.currentTimeMillis();
		
		//long timeElapsed = end - begin;
		
		//System.out.println("Per calcolare l'IR dei Robot e dei Cluster ci sono voluti " + timeElapsed / 1000.0 + " secondi");
		
		//Stampo l'IR dei primi 500 Robot e dei primi 50 Cluster per controllare un po'
		//int counter = 500;
		//for(String c : robots.keySet()){
		//	System.out.println("L\'IR del robot " + c + " è " + robots.get(c).getIR() + "%");
		//	if(--counter < 0) break;
		//}
		//counter = 100;
		//for(String c : clusters.keySet()){
		//	System.out.println("Dati Cluster " + c +" :\n"
		//			+ "IR: " + clusters.get(c).getIR() + "%\n"
		//			+ "Robot attualmente down: " + clusters.get(c).getRobotDown());
			//if(--counter < 0) break;
	}
	
	@Override
	public void run() {
		/*long begin = System.currentTimeMillis();
		
		long end = begin + 30000;
		
		while(begin < end){*/
		
		//Spawniamo il thread che si occupa di ricevere i segnali
		Thread ricettore = new Thread(new Ricettore());
		ricettore.start();
		
		//Spawniamo il thread che si occupa di accettare la connessione
		//delle varie Dashboard
		new Thread(){
			public void run(){
				try {
					while(true){
					Socket newClient = serverForDashboards.accept();
					clients.add(new ClientWrapper(newClient));
					System.out.println("Connected to dashboard " + newClient.getLocalAddress());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		
		//Con i due componenti helper istanzianti possiamo occuparci di 
		//analizzare i segnali
		//System.out.println("Inizio l'analisi dei segnali");
		
		//timer = System.currentTimeMillis();
		
		synchronized(this){
			try {
			Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		while(true){
			analizzaSegnale();
			/*if(System.currentTimeMillis() - timer >= 10000){
				if(IRHandler.getState() == Thread.State.WAITING){
					synchronized(IRHandler){
						IRHandler.notify();
					}
				}
			}*/
		}
		
		
		
	
		//long end = System.currentTimeMillis();
		
		//long timeElapsed = (end - begin);
		
		//System.out.println("Per analizzare " + numeroSegnali + " segnali ci sono voluti " + timeElapsed / 1000.0 + " secondi");
		//System.out.println("Il numero di Robot registrati è: " + robots.size());
		
	}
	
	class Ricettore implements Runnable{
		@Override
		public void run() {
			Socket socket;
			try {
				socket = new Socket("localhost", 60011);
				
				OutputStreamWriter outputStream = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

				
				
				JSONObject segnaleJSON = null;
				while(true)
					try{
						String jsonText = br.readLine();
						if(jsonText.equals("B")) break;
						segnaleJSON = new JSONObject(jsonText);
						Segnale toAdd = new Segnale(segnaleJSON.getString("robotid"), segnaleJSON.getString("clusterid"), (byte)segnaleJSON.getInt("sensornumber"), segnaleJSON.getBoolean("sensorvalue"), segnaleJSON.getLong("timestamp"));
						segnali.add(toAdd);
						//System.out.println("Read signal " + "#" + counter++ + " from client " + socket.getLocalAddress());
					}catch(IOException | JSONException e){
						e.printStackTrace();
					}
				//Fine ricezione segnali; avviso il tester
				outputStream.write("B\n");
				outputStream.flush();
				
				System.out.println("Stop ricezione segnali");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

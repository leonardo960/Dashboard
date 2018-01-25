package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.ServerSocket;
//import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import model.Cluster;
import model.Dato;
import model.FinestraTemporale;
import model.Robot;
import model.Segnale;

public class GestoreSegnali implements Runnable {
	private HashMap<String, Cluster> clusters;
	private HashMap<String, Robot> robots;
	private LinkedList<Segnale> segnali;
	private ServerSocket serverForDashboards;
	private LinkedList<Socket> clients;
	public GestoreSegnali(){
		this.clusters = new HashMap<String, Cluster>();
		this.robots = new HashMap<String, Robot>();
		robots = new HashMap<String, Robot>();
		segnali = new LinkedList<Segnale>();
		clients = new LinkedList<Socket>();
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
			Segnale segnale = segnali.removeFirst();
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

	private void calcolaIR(){
		//System.out.println("Inizio calcolo IR");
		//long begin = System.currentTimeMillis();
		
		Timestamp oneHourAgo = new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000));
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		//System.out.println("One hour ago: " + oneHourAgo);
		//System.out.println("Now: " + now);
		
		HashMap<String, LinkedList<FinestraTemporale>> finestreTemporali = Storage.prelevaFinestreTemporali();
		
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
			//if(clusters.containsKey(id)) System.out.println(downTime);
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
		
		//Invio il dato calcolato
		for(Socket client : clients){
			try {
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				out.writeObject(new Dato(clusters, robots));
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Storage.rimuoviFinestreInattive(oneHourAgo);
		
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
					clients.add(newClient);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		//Con i due componenti helper istanzianti possiamo occuparci di 
		//analizzare i segnali
		System.out.println("Inizio l'analisi dei segnali");
		
		long timer = System.currentTimeMillis();
	
	
		while(true){
			analizzaSegnale();
			if(System.currentTimeMillis() - timer >= 5000){
				//Finiamo di effettuare le ultime query se ne rimangono
				Storage.commitChanges();
				
				//Calcolo il dato e lo invio
				calcolaIR();
				
				//Resettiamo il cooldown
				timer = System.currentTimeMillis();
			}
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
				//Sarebbe per sempre in teoria
				int counter = 0;
				
				JSONObject segnaleJSON = null;
				while(true)
					try{
						String jsonText = br.readLine();
						if(jsonText.equals("B")) break;
						segnaleJSON = new JSONObject(jsonText);
						Segnale toAdd = new Segnale(segnaleJSON.getString("robotid"), segnaleJSON.getString("clusterid"), (byte)segnaleJSON.getInt("sensornumber"), segnaleJSON.getBoolean("sensorvalue"), segnaleJSON.getLong("timestamp"));
						segnali.add(toAdd);
						System.out.println("Read signal " + "#" + counter++ + " from client " + socket.getLocalAddress());
					}catch(IOException | JSONException e){
						e.printStackTrace();
					}
				//Fine ricezione segnali; avviso il tester
				outputStream.write("B\n");
				outputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

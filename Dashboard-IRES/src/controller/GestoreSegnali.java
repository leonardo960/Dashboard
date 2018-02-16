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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.*;

import model.ClientWrapper;
import model.Cluster;
import model.Data;
import model.FinestraTemporale;
import model.Robot;
import model.Segnale;

public class GestoreSegnali implements Runnable {
	private ConcurrentHashMap<String, Cluster> clusters;
	private ConcurrentHashMap<String, Robot> robots;
	private ConcurrentLinkedQueue<Segnale> segnali;
	private ServerSocket serverForDashboards;
	private ConcurrentLinkedQueue<ClientWrapper> clients;
	private Thread IRHandler;
	//private long timer;
	public GestoreSegnali(String[] args){
		clusters = new ConcurrentHashMap<String, Cluster>();
		robots = new ConcurrentHashMap<String, Robot>();
		segnali = new ConcurrentLinkedQueue<Segnale>();
		clients = new ConcurrentLinkedQueue<ClientWrapper>();
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
					if(!Storage.isDatabaseDown.get()){
					System.out.println("Inizio calcolo e invio IR");

					//Finiamo di effettuare le ultime query se ne rimangono
					Storage.commitChanges();
					
					//Calcolo il dato e lo invio
					calcolaIR(con);
					}else{
						System.out.println("Database down; non posso calcolare l'IR");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
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
			synchronized(clusters){
				synchronized(robots){
					
			
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
			
				}
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
		Timestamp oneHourAgo = new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000));
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
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
		Iterator<ClientWrapper> itr = clients.iterator();
		while(itr.hasNext()){
			ClientWrapper client = itr.next();
			try {
				synchronized(clusters){
					synchronized(robots){
						client.getClientOut().writeObject(new Gson().toJson(new Data(clusters, robots)));
					}
				}
				client.getClientOut().flush();
				client.getClientOut().reset();
			} catch (IOException e) {
				//Dashboard disconnected
				System.out.println("Dashboard disconnected: " + client.getClientSocket().getInetAddress());
				clients.remove(client);
			}
		}
		System.out.println("Dato inviato.");
	}
	
	@Override
	public void run() {
		
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
		}
		
		
		
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

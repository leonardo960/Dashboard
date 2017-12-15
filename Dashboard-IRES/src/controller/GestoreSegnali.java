package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;

import model.Cluster;
import model.FinestraTemporale;
import model.Robot;
import model.Segnale;

public class GestoreSegnali implements Runnable {
	private HashMap<String, Cluster> clusters = new HashMap<String, Cluster>();
	private HashMap<String, Robot> robots = new HashMap<String, Robot>();
	private LinkedList<Segnale> segnali = new LinkedList<Segnale>();
	
	private void analizzaSegnale(){
		if(segnali.isEmpty()){
			return;
		}else{
			Segnale segnale = segnali.removeLast();
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
		System.out.println("Inizio calcolo IR");
		long begin = System.currentTimeMillis();
		
		Timestamp oneHourAgo = new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000));
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
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
			if(clusters.containsKey(id)) System.out.println(downTime);
			double preciseDownTime = downTime / 36.0;
			preciseDownTime = Math.ceil(preciseDownTime);
			downTime = (long) preciseDownTime;
			byte IR = 0;
			if(downTime >= 100L){
				IR = 100;
			}else{
				IR = (byte) downTime;
			}
			if(id.charAt(0) == 'C'){
				clusters.get(id).setIR(IR);
			}else{
				robots.get(id).setIR(IR);
			}
		}
		
		/*
		 * 
		 * TODO: invio dati
		 * 
		 */
		
		Storage.rimuoviFinestreInattive(oneHourAgo);
		
		long end = System.currentTimeMillis();
		
		long timeElapsed = end - begin;
		
		System.out.println("Per calcolare l'IR dei Robot e dei Cluster ci sono voluti " + timeElapsed / 1000.0 + " secondi");
		
		//Stampo l'IR dei primi 500 Robot e dei primi 50 Cluster per controllare un po'
		int counter = 500;
		for(String c : robots.keySet()){
			System.out.println("L\'IR del robot " + c + " è " + robots.get(c).getIR() + "%");
			if(--counter < 0) break;
		}
		counter = 100;
		for(String c : clusters.keySet()){
			System.out.println("Dati Cluster " + c +" :\n"
					+ "IR: " + clusters.get(c).getIR() + "\n"
					+ "Robot attualmente down: " + clusters.get(c).getRobotDown());
			if(--counter < 0) break;
		}
	}
	
	@Override
	public void run() {
		/*long begin = System.currentTimeMillis();
		
		long end = begin + 30000;
		
		while(begin < end){*/
		
		
		Thread ricettore = new Thread(new Ricettore());
		ricettore.start();
		try {
			ricettore.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Ora inizia l'analisi dei segnali");
		
		long begin = System.currentTimeMillis();
		
		while(segnali.size() > 0){
			analizzaSegnale();
		}
		
		long end = System.currentTimeMillis();
		
		long timeElapsed = (end - begin);
		
		System.out.println("Per analizzare 90.000 segnali ci sono voluti " + timeElapsed / 1000.0 + " secondi");
		/*try {
		System.out.println("Dormo per 10 secondi");
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		calcolaIR();
	}
	
	class Ricettore implements Runnable{
		@Override
		public void run() {
			Socket socket;
			try {
				socket = new Socket("localhost", 60011);
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				//Sarebbe per sempre in teoria
				for(int i = 0; i < 180000; i++){
					try{
						Segnale segnale = (Segnale) inputStream.readObject();
						System.out.println("Read signal " + "#" + i + "from client " + socket.getLocalAddress());
						segnali.add(segnale);
					}catch(IOException | ClassNotFoundException e){
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import model.Cluster;
import model.FinestraTemporale;
import model.Robot;
import model.Segnale;

public class GestoreSegnali implements Runnable {
	private HashMap<char[], Cluster> clusters = new HashMap<char[], Cluster>();
	private HashMap<char[], Robot> robots = new HashMap<char[], Robot>();
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
				//Prima creaiamo il cluster se non c'è
				if(!clusters.containsKey(segnale.getClusterID())){
					cluster = new Cluster(segnale.getClusterID());
					clusters.put(segnale.getClusterID(), cluster);
				}
				robot = new Robot(segnale.getSensorNumber(), segnale.getRobotID(), segnale.getClusterID());
				robots.put(segnale.getRobotID(), robot);
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
		long begin = System.currentTimeMillis();
		
		
		
		
		Timestamp oneHourAgo = new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000));
		
		TreeMap<char[], LinkedList<FinestraTemporale>> finestreTemporali = Storage.prelevaFinestreTemporali();
		
		
		
		long end = System.currentTimeMillis();
		
		long timeElapsed = end - begin;
		
		System.out.println("Per calcolare l'IR dei Robot e dei Cluster ci sono voluti " + timeElapsed / 1000.0 + " secondi");
		
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
		
		
		
		//calcolaIR();
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
				for(int i = 0; i < 90000; i++){
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

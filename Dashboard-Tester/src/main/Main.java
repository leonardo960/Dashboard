package main;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;

import model.Segnale;


public class Main {
	private static LinkedList<Robot> robots;
	//Devo controllare se il Cluster non esiste o me ne vengono un bordello
	private static Random rand;
	public static void main(String[] args){
		robots = new LinkedList<Robot>();
		rand = new Random();
		for(int i = 0; i < 90000; i++){
			String robotID;
			if(i < 10) robotID = "R0000";
			else if(i >= 10 && i < 100) robotID = "R000";
			else if(i >= 100 && i < 1000) robotID = "R00";
			else if (i >= 1000 && i < 10000) robotID = "R0";
			else robotID = "R";
			char[] clusterid;
			String clusteridString = "C" + rand.nextInt(10) + rand.nextInt(10);
			clusterid = clusteridString.toCharArray();
 			robots.add(new Robot((robotID + i).toCharArray(), clusterid));
		}
		
		try {
			ServerSocket socket = new ServerSocket(60011);
			Socket client = socket.accept();
			System.out.println("Connected to client: " + client.getLocalAddress());
			
			long sendBegin = System.currentTimeMillis();
			
			ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
			outputStream.flush();
			for(int j = 0; j < 2; j++){
				long begin = System.currentTimeMillis();
					for(int i = 0; i < 90000; i++){
						outputStream.writeObject(generaSegnaleRandom());
						System.out.println("Sent signal " + "#" + i + " to client: " + client.getLocalAddress());
					}
				long end = System.currentTimeMillis();
				
				if((end - begin) < 60000L)
					try {
						Thread.sleep(60000L - (end - begin));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
			long sendEnd = System.currentTimeMillis();
			System.out.println("Per generare e inviare 180000 segnali ci sono voluti " + (sendEnd - sendBegin) + "secondi");
			System.out.println("Done sending signals. Accepting other clients as a means to wait");
			
			socket.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static Segnale generaSegnaleRandom(){
		Robot robot = robots.get(rand.nextInt(robots.size()));
		int choice = rand.nextInt(7);
		byte sensorNumber = 0;
		switch(choice){
		case 0: sensorNumber = 0;
				break;
		case 1: sensorNumber = 1;
				break;
		case 2: sensorNumber = 2;
				break;
		case 3: sensorNumber = 3;
				break;
		case 4: sensorNumber = 4;
				break;
		case 5: sensorNumber = 5;
				break;
		case 6: sensorNumber = 6;
				break;
		}
		return new Segnale(String.valueOf(robot.getID()), String.valueOf(robot.getCluster()), sensorNumber, !robot.getSensorValue(sensorNumber), System.currentTimeMillis());
	}
}

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
	
	public static void main(String[] args){
		robots = new LinkedList<Robot>();
		
		for(int i = 0; i < 90000; i++){
			String robotID;
			if(i < 10) robotID = "R0000";
			else if(i > 10 && i < 100) robotID = "R000";
			else if(i > 100 && i < 1000) robotID = "R00";
			else if (i > 1000 && i < 10000) robotID = "R0";
			else robotID = "R";
 			robots.add(new Robot((robotID + i).toCharArray()));
		}
		
		try {
			ServerSocket socket = new ServerSocket(60011);
			Socket client = socket.accept();
			System.out.println("Connected to client: " + client.getLocalAddress());
			
			
			ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
			outputStream.flush();
			for(int i = 0; i < 90000; i++){
				outputStream.writeObject(generaSegnaleRandom());
				System.out.println("Sent signal " + "#" + i + " to client: " + client.getLocalAddress());
			}
			
			System.out.println("Done sending signals. Accepting other clients as a means to wait");
			
			socket.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static Segnale generaSegnaleRandom(){
		Random rand = new Random();
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
		char[] clusterid;
		String clusteridString = "C" + rand.nextInt(10) + rand.nextInt(10);
		clusterid = clusteridString.toCharArray();
		
		return new Segnale(robot.getID(), clusterid, sensorNumber, !robot.getSensorValue(sensorNumber), System.currentTimeMillis());
	}
}

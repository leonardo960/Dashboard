package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import model.Segnale;


public class Main {
	private static LinkedList<Robot> robots;
	private static Random rand;
	private static int iterazioni;
	private static int segnaliPerIterazione;
	
	public static void main(String[] args){
		iterazioni = 1;
		segnaliPerIterazione = 90000;
		robots = new LinkedList<Robot>();	
		rand = new Random();
		for(int i = 0; i < 90000; i++){
			String robotID;
			if(i < 10) robotID = "R0000";
			else if(i >= 10 && i < 100) robotID = "R000";
			else if(i >= 100 && i < 1000) robotID = "R00";
			else if (i >= 1000 && i < 10000) robotID = "R0";
			else robotID = "R";
			String clusterid = "C" + rand.nextInt(10) + rand.nextInt(10);
			
 			robots.add(new Robot((robotID + i), clusterid));
		}
		
		try {
			ServerSocket socket = new ServerSocket(60011);
			System.out.println("Server running. Waiting for client to connect...");
			Socket client = socket.accept();
			System.out.println("Connected to client: " + client.getLocalAddress());
			
			long sendBegin = System.currentTimeMillis();
			BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));
			//OutputStreamWriter outputStream = new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
			bw.flush();
			for(int j = 0; j < iterazioni; j++){
				long begin = System.currentTimeMillis();
					for(int i = 0; i < segnaliPerIterazione; i++){
						Robot robot = chooseRobot();
						if(robot.getDownSensors() > 2){
							System.out.println("Manutenzione su Robot #" + robot.getID());
							for(byte z = 0; z < 7; z++){
								if(!robot.getSensorValue(z)){
									Segnale segnale = new Segnale(String.valueOf(robot.getID()), String.valueOf(robot.getCluster()), z, true, System.currentTimeMillis());
									JSONObject json = new JSONObject();
									json.put("robotid", segnale.getRobotID());
									json.put("clusterid", segnale.getClusterID());
									json.put("sensornumber", segnale.getSensorNumber());
									json.put("timestamp", segnale.getTimestamp().getTime());
									json.put("sensorvalue", segnale.getValue());
									bw.write(json.toString().concat("\n"));
									robot.setSensor(z, true);
									robot.decrementDownSensors();
									System.out.println("Sent maintenance signal of iteration #" + i);
								}
							}
						}else{
						Segnale signalToSend = generaSegnaleRandom(robot);
						JSONObject json = new JSONObject();
						json.put("robotid", signalToSend.getRobotID());
						json.put("clusterid", signalToSend.getClusterID());
						json.put("sensornumber", signalToSend.getSensorNumber());
						json.put("timestamp", signalToSend.getTimestamp().getTime());
						json.put("sensorvalue", signalToSend.getValue());
						bw.write(json.toString().concat("\n"));
						System.out.println("Chunk #" + j + " - " + "Signal #" + i + " sent to Client #" + client.getLocalAddress());
						}
					}
					
				long end = System.currentTimeMillis();
				
				if((end - begin) < 60000L){
					try {
						Thread.sleep(60000L - (end - begin));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			//Segnale fantoccio per stoppare il client dal ricevere segnali
			bw.write("B\n");
			bw.flush();
			long sendEnd = System.currentTimeMillis();
			
			long sendDuration = (sendEnd - sendBegin) / 1000;
			
			
			System.out.println("Per generare e inviare " + iterazioni*segnaliPerIterazione + " segnali ci sono voluti " + sendDuration + " secondi");
			
			//Appena arriva il segnale di avvenuta ricezione chiudo tutto
			if(br.readLine().equals("B") ){
				br.close();
				bw.close();
				client.close();
				socket.close();
				System.out.println("Comunicazione Server/Client conclusa con successo. Shutdown del server...");
			}else{
				System.out.println("Il client non ha inviato il segnale di avvenuta ricezione. Shutdown del server...");
				br.close();
				bw.close();
				client.close();
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static Robot chooseRobot(){
		return robots.get(rand.nextInt(robots.size()));
	}
	
	private static Segnale generaSegnaleRandom(Robot robot){
		int choice = rand.nextInt(7);
		byte sensorNumber = (byte) choice;
		
		//Il tester si tiene in memoria la "situazione" dei robot così da non mandare
		//segnali insensati e.g. un sensore va down quando è già down...
		
		if(robot.getSensorValue(sensorNumber)){
			robot.setSensor(sensorNumber, false);
			robot.incrementDownSensors();
		}else{
			robot.setSensor(sensorNumber, true);
			robot.decrementDownSensors();
		}
		
		Segnale temp = new Segnale(String.valueOf(robot.getID()), String.valueOf(robot.getCluster()), sensorNumber, robot.getSensorValue(sensorNumber), System.currentTimeMillis());
		
		return temp;
	}
}

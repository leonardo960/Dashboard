package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Segnale implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2806045919769750244L;
	private String robotID;
	private String clusterID;
	private byte sensorNumber;
	private boolean value;
	private Timestamp timestamp;
	
	public Segnale(String robotID, String clusterID, byte sensorNumber, boolean value, long timestamp){
		this.robotID = robotID;
		this.clusterID = clusterID;
		this.sensorNumber = sensorNumber;
		this.value = value;
		this.timestamp = new Timestamp(timestamp);
	}
	
	
	public boolean getValue(){
		return value;
	}
	
	public Timestamp getTimestamp(){
		return timestamp;
	}
	
	public String getRobotID(){
		return robotID;
	}
	
	public byte getSensorNumber(){
		return sensorNumber;
	}
	
	public String getClusterID(){
		return clusterID;
	}
}

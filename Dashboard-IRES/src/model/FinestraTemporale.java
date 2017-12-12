package model;

import java.sql.Timestamp;

public class FinestraTemporale {
	private char[] robotid;
	private byte numSensore;
	private Timestamp sogliaSinistra;
	private Timestamp sogliaDestra;
	
	public FinestraTemporale(char[] robotid, byte numSensore, Timestamp sogliaSinistra, Timestamp sogliaDestra){
		this.robotid = robotid;
		this.numSensore = numSensore;
		this.sogliaSinistra = sogliaSinistra;
		this.sogliaDestra = sogliaDestra;
	}
	
	public char[] getRobotID(){
		return robotid;
	}
	
	public byte getNumSensore(){
		return numSensore;
	}
	
	public Timestamp getSogliaSinistra(){
		return sogliaSinistra;
	}
	
	public Timestamp getSogliaDestra(){
		return sogliaDestra;
	}
}

package model;

import java.sql.Timestamp;

public class FinestraTemporale {
	private String robotid;
	private Timestamp sogliaSinistra;
	private Timestamp sogliaDestra;
	
	public FinestraTemporale(String robotid, Timestamp sogliaSinistra, Timestamp sogliaDestra){
		this.robotid = robotid;
		this.sogliaSinistra = sogliaSinistra;
		this.sogliaDestra = sogliaDestra;
	}
	
	public String getRobotID(){
		return robotid;
	}
	
	public Timestamp getSogliaSinistra(){
		return sogliaSinistra;
	}
	
	public Timestamp getSogliaDestra(){
		return sogliaDestra;
	}
}

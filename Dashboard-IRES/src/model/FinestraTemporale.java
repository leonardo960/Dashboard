package model;

import java.sql.Timestamp;

public class FinestraTemporale {
	private String id_oggetto;
	private Timestamp sogliaSinistra;
	private Timestamp sogliaDestra;
	
	public FinestraTemporale(String id_oggetto, Timestamp sogliaSinistra, Timestamp sogliaDestra){
		this.id_oggetto = id_oggetto;
		this.sogliaSinistra = sogliaSinistra;
		this.sogliaDestra = sogliaDestra;
	}
	
	public String getId(){
		return id_oggetto;
	}
	
	public Timestamp getSogliaSinistra(){
		return sogliaSinistra;
	}
	
	public Timestamp getSogliaDestra(){
		return sogliaDestra;
	}
}

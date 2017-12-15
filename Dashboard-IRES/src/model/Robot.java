package model;

public class Robot {
	private boolean[] sensors;
	private String id;
	private String clusterid;
	private byte IR;
	private byte downSensors;
	//Il Robot viene creato solo quando ancora non lo conosciamo, tramite un segnale
	//di down. Perciò passo nel costruttore un indice (tipo byte per efficienza) a cui
	//settare false nell'array di booleani, il resto saranno true ovviamente.
	/**
	 * Initiates a Robot following this logic:
	 * @param downSignalIndex the index where the first down signal should go
	 * @param id the Robot's ID (taken from signal)
	 * @param cluster the Robot's Cluster (taken from signal)
	 */
	public Robot(String id, String clusterid){
		sensors = new boolean[7];
		sensors[0] = true;
		sensors[1] = true;
		sensors[2] = true;
		sensors[3] = true;
		sensors[4] = true;
		sensors[5] = true;
		sensors[6] = true;
		this.id = id;
		this.clusterid = clusterid;
		IR = 0;
		downSensors = 0;
	}
	
	public void setSensor(byte index, boolean value){
		sensors[index] = value;
	}
	
	public String getID(){
		return id;
	}
	
	public String getCluster(){
		return clusterid;
	}
	
	public byte getIR(){
		return IR;
	}
	
	public byte getDownSensors(){
		return downSensors;
	}
	
	public boolean getSensorValue(byte index){
		return sensors[index];
	}
	
	public void setIR(byte newIR){
		IR = newIR;
	}
	
	public void incrementDownSensors(){
		downSensors++;
	}
	
	public void decrementDownSensors(){
		downSensors--;
	}
}

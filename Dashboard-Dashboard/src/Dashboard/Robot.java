package Dashboard;

public class Robot {
	private boolean[] sensors;
	private String id;
	private String clusterid;
	private byte IR;
	private byte downSensors;

	public Robot(String id, String cluster){
		sensors = new boolean[7];
		sensors[0] = true;
		sensors[1] = true;
		sensors[2] = true;
		sensors[3] = true;
		sensors[4] = true;
		sensors[5] = true;
		sensors[6] = true;
		this.id = id;
		this.clusterid = cluster;
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

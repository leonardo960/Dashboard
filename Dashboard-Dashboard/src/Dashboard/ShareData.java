package Dashboard;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JFrame;

import model.Cluster;
import model.Robot;



public class ShareData {

	protected static JFrame window;
	protected static String lastUpdate;
	protected static HashMap<String, Cluster> c_map;
	protected static HashMap<String, Robot> r_map;
	public static Screen currentScreen;
	public void ShareDatas(){
		if(window == null){
			
			window = new JFrame();
			//Usato per prendere le dimensioni dello schermo
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			
			window.setSize(700,500);
			window.setLocation(dim.width/2-window.getSize().width/2, dim.height/2-window.getSize().height/2);
			
			window.setTitle("Dashboard");
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setVisible(true);
			window.getContentPane().setLayout(null);
		}
	}
	
	
	public void updateTime(){
		lastUpdate = new SimpleDateFormat("HH:mm:ss").format(new Date());
	}
	
	public void setCMap(HashMap<String,Cluster> c_map){
		this.c_map = c_map; 
	}
	
	
	public void setRMap(HashMap<String,Robot> r_map){
		this.r_map = r_map; 
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				ShareData sd = new ShareData();
				
				new Thread(){
					public void run(){
						try {
							Socket client = new Socket("localhost", 60012);
							ObjectInputStream in = new ObjectInputStream(client.getInputStream());
							while(true){
								HashMap<String, Cluster> clusters = (HashMap<String, Cluster>) in.readObject();
								HashMap<String, Robot> robots = (HashMap<String, Robot>) in.readObject();
								c_map = clusters;
								r_map = robots;
								lastUpdate = new SimpleDateFormat("HH:mm:ss").format(new Date());
								currentScreen.update();
								
							}
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
				
				try {
					
					//Oggetti di prova
					HashMap<String, Cluster> c_map = new HashMap<String, Cluster>();
					HashMap<String, Robot> r_map = new HashMap<String, Robot>();
					
					/*Robot r = new Robot("xr3", "yr3");
					Robot r2 = new Robot("xr4", "yr4");
					Robot r3 = new Robot("xr5", "yr3");
					Robot r4 = new Robot("xr6", "yr4");
					Robot r5 = new Robot("xr7", "yr4");
					Robot r6 = new Robot("xr8", "yr4");
					Robot r7 = new Robot("xr9", "yr4");
					Robot r8 = new Robot("xr10", "yr4");
					Robot r9 = new Robot("xr11", "yr4");
					Robot r0 = new Robot("xr12", "yr4");
					Robot r11 = new Robot("xr13", "yr4");
					Robot r12 = new Robot("xr14", "yr4");
					Robot r13 = new Robot("xr15", "yr4");
					Robot r14 = new Robot("xr16", "yr4");
					Robot r15 = new Robot("xr17", "yr4");
					Robot r16 = new Robot("xr18", "yr4");
					
					Cluster c = new Cluster("yr3");
					Cluster c2 = new Cluster("yr4");
					
					r.setSensor((byte) 0, false);
					r.setIR((byte) 66);
					c.setIR((byte) 46);
					
					c.getRobots().add(r);
					c.getRobots().add(r3);
					c.getRobots().add(r4);
					c.getRobots().add(r5);
					c.getRobots().add(r6);
					c.getRobots().add(r7);
					c.getRobots().add(r8);
					c.getRobots().add(r9);
					c.getRobots().add(r0);
					c.getRobots().add(r11);
					c.getRobots().add(r12);
					c.getRobots().add(r13);
					c.getRobots().add(r14);
					c.getRobots().add(r15);
					c.getRobots().add(r16);
					c2.getRobots().add(r2);
					c2.getRobots().add(r4);
					
					c_map.put("yr3", c);
					c_map.put("yr4", c2);
					
					r_map.put("xr3", r);
					r_map.put("xr4", r2);
					r_map.put("xr5", r3);
					r_map.put("xr6", r4);
					r_map.put("xr7", r5);
					r_map.put("xr8", r6);
					r_map.put("xr9", r7);
					r_map.put("xr10", r8);
					r_map.put("xr11", r9);
					r_map.put("xr12", r0);
					r_map.put("xr13", r11);
					r_map.put("xr14", r12);
					r_map.put("xr15", r13);
					r_map.put("xr16", r14);
					r_map.put("xr17", r15);
					r_map.put("xr18", r16);
					*/
					sd.setCMap(c_map);
					sd.setRMap(r_map);
					sd.ShareDatas();
					
					sd.updateTime();
					currentScreen = new Dashboard();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
});
	}
	
}

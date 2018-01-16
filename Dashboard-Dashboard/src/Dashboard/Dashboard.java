package Dashboard;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class Dashboard {
	
	private static JFrame window;
	private HashMap<String, Cluster> c_map;
	private HashMap<String, Robot> r_map;
	
	public Dashboard(HashMap<String, Cluster> c_map, HashMap<String, Robot> r_map){
		
		this.c_map = c_map;
		this.r_map = r_map;
		
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
		initialize();
	}
	
	//funzione che crea la parte grafica
	public void initialize(){
		
		//panel che contiene gli elementi visualizzati
		JPanel panel = new JPanel();
		panel.setBounds(0,0,700,500);
		panel.setLayout(new MigLayout());
		panel.setVisible(true);
		window.getContentPane().add(panel);
		
		JButton btn_cluster = new JButton("Controlla clusters");
		panel.add(btn_cluster, "pos 250px 60px, width 200, height 70");
		
		btn_cluster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.setVisible(false);
				new ViewClusters(window, c_map, r_map);
			}});
	
		JButton btn_robot = new JButton("Controlla robots");
		panel.add(btn_robot, "pos 250px 190px, width 200, height 70");
		
		btn_robot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.setVisible(false);
				new ViewRobots(window, c_map, r_map);
			}});
		
		JButton cerca = new JButton("Cerca per ID");
		panel.add(cerca, "pos 250px 320px, width 200, height 70");
		
		cerca.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.setVisible(false);
				new ViewSearch(window, c_map, r_map);
			}});
	
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					//Oggetti di prova
					HashMap<String, Cluster> c_map = new HashMap<String, Cluster>();
					HashMap<String, Robot> r_map = new HashMap<String, Robot>();
					
					Robot r = new Robot("xr3", "yr3");
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
					
					new Dashboard(c_map, r_map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
});
	}

}

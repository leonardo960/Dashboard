package Dashboard;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.Cluster;
import model.Robot;
import net.miginfocom.swing.MigLayout;

public class RobotListSensor extends ShareData implements Screen{
	private Cluster c;
	private int sensor;
	private ArrayList<JButton> robots_btns;
	private JPanel panel_list;
	private JPanel robots_list_pnl;
	private JLabel lblTime;
	//Passo il frame base come parametro
	public RobotListSensor(Cluster c, int sensor){
		this.c = c;
		this.sensor=sensor;
		
		initialize();
	}
	
	public void initialize(){
		
		robots_list_pnl = new JPanel();
		robots_list_pnl.setLayout(new MigLayout());
		robots_list_pnl.setBounds(0,0,700,500);
		robots_list_pnl.setVisible(true);
		window.getContentPane().add(robots_list_pnl);
		
		//Pannello che conterr� la lista di bottoni
		panel_list = new JPanel();
		panel_list.setLayout(new MigLayout("center center, wrap, gapy 5"));
		
		//Lista di bottoni, uno per ogni robot
		robots_btns = new ArrayList<JButton>();
		
		//For che aggiunge i bottoni alla lista, attribuendogli l'ID del robot
		for(Robot r: c.getRobots()){
			if(!r.getSensorValue((byte) sensor))
					robots_btns.add(new JButton(r.getID() + " IR: " + r.getIR() + "%"));
			}
		 
		
		//For che aggiunge i bottoni al panel
		for(JButton jb: robots_btns){
			if(r_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))).getIR()>=40)
				jb.setBackground(Color.red);
		  		panel_list.add(jb, "width 200, height 30");
				jb.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						robots_list_pnl.setVisible(false);
						currentScreen = new ViewRobot(r_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))));
					}});
		  }
		  
		
		//Scroll per la lista
		JScrollPane scroll = new JScrollPane(panel_list);
		scroll.setVerticalScrollBarPolicy ( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		robots_list_pnl.add(scroll, "pos 0px 0px, width 686, height 463");
		
		JButton btnHome = new JButton("Home");
		panel_list.add(btnHome, "pos 20px 10px, width 110, height 15");

		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robots_list_pnl.setVisible(false);
				currentScreen = new Dashboard();
		}});
		
		JButton btnBack = new JButton("Indietro");
		panel_list.add(btnBack, "pos 20px 55px, width 110, height 15");

		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robots_list_pnl.setVisible(false);
				currentScreen = new ViewCluster(c);
		}});
		

		JLabel lbl = new JLabel("Ultimo aggiornamento:");
		panel_list.add(lbl, "pos 480px 10px, width 110, height 15");
		
		lblTime = new JLabel(""+lastUpdate);
		panel_list.add(lblTime, "pos 480px 30px, width 110, height 15");

	}

	@Override
	public void update() {
		String newClusterObjID = c.getID();
		c = c_map.get(newClusterObjID);
		
		for(JButton jb : robots_btns){
			panel_list.remove(jb);
		}
		
		robots_btns.clear();
		
		for(Robot r: c.getRobots()){
			if(!r.getSensorValue((byte) sensor))
					robots_btns.add(new JButton(r.getID() + " IR: " + r.getIR() + "%"));
			}
		
		for(JButton jb: robots_btns){
			if(r_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))).getIR()>=threshold)
				jb.setBackground(Color.red);
		  		panel_list.add(jb, "width 200, height 30");
				jb.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						robots_list_pnl.setVisible(false);
						currentScreen = new ViewRobot(r_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))));
					}});
		  }
		
		lblTime.setText(lastUpdate);
	}
}
package Dashboard;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

public class ViewRobots {
	private JFrame window;
	private HashMap<String, Cluster> c_map;
	private HashMap<String, Robot> r_map;
	
	//Passo il frame base come parametro
	public ViewRobots(JFrame w, HashMap<String, Cluster> c_map, HashMap<String, Robot> r_map){
		window = w;
		this.c_map = c_map;
		this.r_map = r_map;
		
		initialize();
	}
	
	public void initialize(){
		
		JPanel robots_pnl = new JPanel();
		robots_pnl.setLayout(new MigLayout());
		robots_pnl.setBounds(0,0,700,500);
		robots_pnl.setVisible(true);
		window.getContentPane().add(robots_pnl);
		
		//Pannello che conterrà la lista di bottoni
		JPanel panel_list = new JPanel();
		panel_list.setLayout(new MigLayout("center center, wrap, gapy 5"));
		
		//Lista di bottoni, uno per ogni robot
		ArrayList<JButton> robots_btns = new ArrayList<JButton>();
		
		//For che aggiunge i bottoni alla lista, attribuendogli l'ID del robot
		for(Robot r: r_map.values())
			robots_btns.add(new JButton(r.getID()));
		 
		
		//For che aggiunge i bottoni al panel
		for(JButton jb: robots_btns){
			if(r_map.get(jb.getText()).getIR()>=40)
				jb.setBackground(Color.red);
		  		panel_list.add(jb, "width 200, height 30");
				jb.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						robots_pnl.setVisible(false);
						new ViewRobot(window, r_map.get(jb.getText()), c_map, r_map);
					}});
		  }
		  
		
		//Scroll per la lista
		JScrollPane scroll = new JScrollPane(panel_list);
		scroll.setVerticalScrollBarPolicy ( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		robots_pnl.add(scroll, "pos 0px 0px, width 686, height 463");
		
		JButton btnHome = new JButton("Home");
		panel_list.add(btnHome, "pos 20px 10px, width 110, height 15");

		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robots_pnl.setVisible(false);
				new Dashboard(c_map, r_map);
		}});

	}
}

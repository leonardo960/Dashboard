package Dashboard;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.Cluster;
import model.Robot;
import net.miginfocom.swing.MigLayout;

public class ViewRobots extends ShareData implements Screen{
	private JPanel panel_list;
	private ArrayList<JButton> robots_btns;
	private JPanel robots_pnl;
	private JLabel lblTime;
	//Passo il frame base come parametro
	public ViewRobots(){
		initialize();
	}
	
	public void initialize(){
		
		robots_pnl = new JPanel();
		robots_pnl.setLayout(new MigLayout());
		robots_pnl.setBounds(0,0,700,500);
		robots_pnl.setVisible(true);
		window.getContentPane().add(robots_pnl);
		
		//Pannello che conterrà la lista di bottoni
		panel_list = new JPanel();
		panel_list.setLayout(new MigLayout("center center, wrap, gapy 5"));
		
		//Lista di bottoni, uno per ogni robot
	    robots_btns = new ArrayList<JButton>();
		
		//For che aggiunge i bottoni alla lista, attribuendogli l'ID del robot
	    System.out.println(r_map.size());
	    for(Robot r: r_map.values())
			robots_btns.add(new JButton(r.getID() + " IR: "+ r_map.get(r.getID()).getIR() + "%"));
		
		
		//For che aggiunge i bottoni al panel
		if(!robots_btns.isEmpty()){
		for(JButton jb: robots_btns){
			if(r_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))).getIR()>=40)
				jb.setBackground(Color.red);
		  		panel_list.add(jb, "width 200, height 30");
				jb.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						robots_pnl.setVisible(false);
						new ViewRobot(r_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))));
					}});
		  }
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
				currentScreen = new Dashboard();
		}});
		
		
		JLabel lbl = new JLabel("Ultimo aggiornamento:");
		panel_list.add(lbl, "pos 480px 10px, width 110, height 15");
		
		lblTime = new JLabel(""+lastUpdate);
		panel_list.add(lblTime, "pos 480px 30px, width 110, height 15");

	}

	@Override
	public void update() {
		if(r_map.size() > robots_btns.size()){
			LinkedList<Robot> newRobots = new LinkedList<Robot>();
			for(Robot newRobot : r_map.values()){
				boolean trovato = false;
				for(JButton jb : robots_btns){
					if(jb.getText().contains(newRobot.getID())){
						trovato = true;
						break;
					}
				}
				if(!trovato){
					newRobots.add(newRobot);
				}
			}
			for(Robot toAdd : newRobots){
				JButton jb = new JButton(toAdd.getID() + " IR: "+ r_map.get(toAdd.getID()).getIR() + "%");
				robots_btns.add(jb);
				panel_list.add(jb, "width 200, height 30");
				jb.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						robots_pnl.setVisible(false);
						new ViewRobot(r_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))));
					}
				});
			}
		}else{
			for(JButton jb : robots_btns){
				Robot r = r_map.get(jb.getText().substring(0, jb.getText().indexOf(" ")));
				jb.setText(r.getID() + " IR: " + r.getIR() + "%");
			}
		}
		lblTime.setText(lastUpdate);
	}
}
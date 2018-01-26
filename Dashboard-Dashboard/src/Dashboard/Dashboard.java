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

public class Dashboard extends ShareData implements Screen{
	
	public Dashboard(){
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
				currentScreen = new ViewClusters();
			}});
	
		JButton btn_robot = new JButton("Controlla robots");
		panel.add(btn_robot, "pos 250px 190px, width 200, height 70");
		
		btn_robot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.setVisible(false);
				currentScreen = new ViewRobots();
			}});
		
		JButton cerca = new JButton("Cerca per ID");
		panel.add(cerca, "pos 250px 320px, width 200, height 70");
		
		cerca.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.setVisible(false);
				currentScreen = new ViewSearch();
			}});
	
	}

	@Override
	public void update() {
		//Nothing to update here
	}

}
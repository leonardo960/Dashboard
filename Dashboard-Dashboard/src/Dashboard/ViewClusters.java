package Dashboard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

public class ViewClusters extends ShareData{
	
	//Passo il frame base come parametro
	public ViewClusters(){
		initialize();
	}
	
	public void initialize(){
		
		JPanel clusters_pnl = new JPanel();
		clusters_pnl.setLayout(new MigLayout());
		clusters_pnl.setBounds(0,0,700,500);
		clusters_pnl.setVisible(true);
		window.getContentPane().add(clusters_pnl);
		
		//Pannello che conterr� la lista di bottoni
		JPanel panel_list = new JPanel();
		panel_list.setLayout(new MigLayout("center center, wrap, gapy 5"));
		
		//Lista di bottoni, uno per ogni cluster
		ArrayList<JButton> clusters_btns = new ArrayList<JButton>();
		
		//For che aggiunge i bottoni alla lista, attribuendogli l'ID del cluster
		for(Cluster c: c_map.values())
			clusters_btns.add(new JButton(c.getID()));
		 
		
		//For che aggiunge i bottoni al panel
		for(JButton jb: clusters_btns){
		  		panel_list.add(jb, "width 200, height 30");
				jb.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						clusters_pnl.setVisible(false);
						new ViewCluster(c_map.get(jb.getText()));
					}});
		  }
		  
		
		//Scroll per la lista
		JScrollPane scroll = new JScrollPane(panel_list);
		scroll.setVerticalScrollBarPolicy ( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		clusters_pnl.add(scroll, "pos 0px 0px, width 686, height 463");
		
		JButton btnHome = new JButton("Home");
		panel_list.add(btnHome, "pos 20px 10px, width 110, height 15");

		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clusters_pnl.setVisible(false);
				new Dashboard();
		}});
		
		JLabel lbl = new JLabel("Ultimo aggiornamento:");
		panel_list.add(lbl, "pos 480px 10px, width 110, height 15");
		
		JLabel lblTime = new JLabel(""+lastUpdate);
		panel_list.add(lblTime, "pos 480px 30px, width 110, height 15");

	}
}
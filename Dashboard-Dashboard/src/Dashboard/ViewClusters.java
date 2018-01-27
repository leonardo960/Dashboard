package Dashboard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.json.JSONException;
import org.json.JSONObject;

import model.Cluster;
import net.miginfocom.swing.MigLayout;

public class ViewClusters extends ShareData implements Screen{
	
	private ArrayList<JButton> clusters_btns;
	private JScrollPane scroll;
	private JLabel lblTime;
	private JPanel panel_list;
	private JPanel clusters_pnl;
	//Passo il frame base come parametro
	public ViewClusters(){
		initialize();
	}
	
	public void initialize(){
		
		clusters_pnl = new JPanel();
		
		clusters_pnl.setLayout(new MigLayout());
		clusters_pnl.setBounds(0,0,700,500);
		clusters_pnl.setVisible(true);
		window.getContentPane().add(clusters_pnl);
		
		//Pannello che conterrà la lista di bottoni
		panel_list = new JPanel();
		panel_list.setLayout(new MigLayout("center center, wrap, gapy 5"));
		
		//Lista di bottoni, uno per ogni cluster
		clusters_btns = new ArrayList<JButton>();
		
		//For che aggiunge i bottoni alla lista, attribuendogli l'ID del cluster
		for(Cluster c: c_map.values())
			clusters_btns.add(new JButton(c.getID() + " IR: "+ c_map.get(c.getID()).getIR() + "%"));
		 
		
		//For che aggiunge i bottoni al panel
		for(JButton jb: clusters_btns){
		  		panel_list.add(jb, "width 200, height 30");
				jb.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						clusters_pnl.setVisible(false);
						currentScreen = new ViewCluster(c_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))));
					}
				});
		}
		
		//Scroll per la lista
		scroll = new JScrollPane(panel_list);
		scroll.setVerticalScrollBarPolicy ( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		clusters_pnl.add(scroll, "pos 0px 0px, width 686, height 463");
		
		JButton btnHome = new JButton("Home");
		panel_list.add(btnHome, "pos 20px 10px, width 110, height 15");

		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clusters_pnl.setVisible(false);
				currentScreen = new Dashboard();
		}});
		

		JLabel lbl = new JLabel("Ultimo aggiornamento:");
		panel_list.add(lbl, "pos 480px 10px, width 110, height 15");
		
	    lblTime = new JLabel(""+lastUpdate);
		panel_list.add(lblTime, "pos 480px 30px, width 110, height 15");

	}

	@Override
	public void update() {
		if(c_map.size() > clusters_btns.size()){
			LinkedList<Cluster> newClusters = new LinkedList<Cluster>();
			for(Cluster newCluster : c_map.values()){
				boolean trovato = false;
				for(JButton jb : clusters_btns){
					if(jb.getText().contains(newCluster.getID())){
						trovato = true;
						break;
					}
				}
				if(!trovato){
					newClusters.add(newCluster);
				}
			}
			for(Cluster toAdd : newClusters){
				JButton jb = new JButton(toAdd.getID() + " IR: " + toAdd.getIR() + "%");
				clusters_btns.add(jb);
				panel_list.add(jb, "width 200, height 30");
				jb.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						clusters_pnl.setVisible(false);
						currentScreen = new ViewCluster(c_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))));
					}
				});
			}
		}else{
			for(JButton jb : clusters_btns){
				Cluster c = c_map.get(jb.getText().substring(0, jb.getText().indexOf(" ")));
				jb.setText(c.getID() + " IR: " + c.getIR() + "%");
			}
		}
		
		
		lblTime.setText(lastUpdate);
	}
}
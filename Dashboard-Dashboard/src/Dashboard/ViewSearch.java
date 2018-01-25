package Dashboard;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class ViewSearch extends ShareData{
	String searched;
	
	//Passo il frame base come parametro
	public ViewSearch(){
		searched = null;
		
		initialize();
	}
	
	public void initialize(){
		JPanel search_pnl = new JPanel();
		search_pnl.setLayout(new MigLayout());
		search_pnl.setBounds(0,0,700,500);
		search_pnl.setVisible(true);
		window.getContentPane().add(search_pnl);
		
		JLabel lbl = new JLabel("Ultimo aggiornamento:");
		lbl.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		search_pnl.add(lbl, "pos 230px 10px, width 115, height 15");
		
		JLabel lblTime = new JLabel(""+lastUpdate);
		lblTime.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		search_pnl.add(lblTime, "pos 400px 10px, width 110, height 15");
		
		JLabel search = new JLabel("Cerca per ID");
		search.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 25));
		search_pnl.add(search, "pos 275px 30px, width 150, height 30");
		
		JTextField search_txt = new JTextField();
		search_pnl.add(search_txt, "pos 275px 90px, width 150, height 30");
		
		JRadioButton cluster_rb = new JRadioButton("Cluster");
		cluster_rb.setActionCommand("cluster");
		cluster_rb.setMnemonic(KeyEvent.VK_C);
		cluster_rb.setSelected(true);
		
		JRadioButton robot_rb = new JRadioButton("Robot");
		robot_rb.setActionCommand("robot");
		robot_rb.setMnemonic(KeyEvent.VK_R);
		
		ButtonGroup bGroup = new ButtonGroup();
		bGroup.add(cluster_rb);
		bGroup.add(robot_rb);
		
		search_pnl.add(cluster_rb, "pos 280px 130px");
		search_pnl.add(robot_rb, "pos 355px 130px");
		
		JButton search_btn = new JButton("Cerca");
		search_pnl.add(search_btn, "pos 275px 160px, width 150, height 30");
		
		JButton id_searched = new JButton("");
		id_searched.setVisible(false);
		search_pnl.add(id_searched, "pos 275px 210px, width 150, height 30");
		
		
		search_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searched = search_txt.getText();
				
				if(searched.isEmpty())
					JOptionPane.showMessageDialog(window, "Inserire un ID", "Errore", JOptionPane.ERROR_MESSAGE);
				else{
						if(cluster_rb.isSelected()){
							if(c_map.containsKey(searched)){
								id_searched.setText("Cluster " + searched);
								id_searched.setVisible(true);
							}
							else{
								JOptionPane.showMessageDialog(window, "Cluster inesistente", "Errore", JOptionPane.ERROR_MESSAGE);
								search_txt.setText("");
							}
						}
						else{
							if(r_map.containsKey(searched)){
								id_searched.setText("Robot " + searched);
								id_searched.setVisible(true);
							}
							else{
								JOptionPane.showMessageDialog(window, "Robot inesistente", "Errore", JOptionPane.ERROR_MESSAGE);
								search_txt.setText("");
							}
						}
				}
			}
		});
		
		id_searched.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search_pnl.setVisible(false);
				if(id_searched.getText().substring(0,1).equals("C"))
					new ViewCluster(c_map.get(searched));
				else
					new ViewRobot(r_map.get(searched));
			}});
		
		JButton btnHome = new JButton("Home");
		search_pnl.add(btnHome, "pos 275px 420px, width 150, height 15");

		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search_pnl.setVisible(false);
				new Dashboard();
		}});
		
		
	}
}

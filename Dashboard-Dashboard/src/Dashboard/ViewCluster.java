package Dashboard;

import java.awt.Color;
import java.awt.Font;
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

import org.json.JSONException;

import model.Cluster;
import model.Robot;
import net.miginfocom.swing.MigLayout;

public class ViewCluster extends ShareData implements Screen{
	
	private Cluster c;
	private JLabel lblTime;
	private JLabel ir_cls;
	private int s1 = 0, s2 = 0, s3 = 0, s4 = 0, s5 = 0, s6 = 0, s7 = 0;
	private JLabel num_s1;
	private JLabel num_s2;
	private JLabel num_s3;
	private JLabel num_s4;
	private JLabel num_s5;
	private JLabel num_s6;
	private JLabel num_s7;
	private ArrayList<JButton> robots_btns;
	private JPanel panel_list;
	private JPanel pnl;
	private JLabel lista_rbt;
	public ViewCluster(Cluster c){
		this.c = c;
		initialize();
	}
	
	public void initialize(){
		
		pnl = new JPanel();
		pnl.setLayout(new MigLayout());
		pnl.setBounds(0,0,700,500);
		pnl.setVisible(true);
		window.getContentPane().add(pnl);
		
		JPanel cluster_pnl = new JPanel();
		cluster_pnl.setLayout(new MigLayout());
		cluster_pnl.setVisible(true);
		
		JLabel clst = new JLabel("Informazioni cluster");
		clst.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 20));
		cluster_pnl.add(clst, "width 300, height 25");
		
		JLabel lbl = new JLabel("Ultimo aggiornamento:");
		lbl.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		cluster_pnl.add(lbl, "pos 265px 10px, width 115, height 15");
		
		lblTime = new JLabel(""+lastUpdate);
		lblTime.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		cluster_pnl.add(lblTime, "pos 430px 10px, width 110, height 15");
		
		JLabel id = new JLabel("ID Cluster: ");
		id.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(id, "pos 20px 50px, width 100px, height 30px");
		
		JLabel id_cls = new JLabel(c.getID());
		id_cls.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(id_cls, "pos 130px 50px, width 100, height 30");
	
		JLabel ir = new JLabel("Inefficiency rate Cluster: ");
		ir.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(ir, "pos 20px 100px, width 100, height 30");
		
		ir_cls = new JLabel(""+ c.getIR() + "%");
		ir_cls.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(ir_cls, "pos 230px 100px, width 100, height 30");
		
		s1 = 0;
		s2 = 0;
		s3 = 0;
		s4 = 0;
		s5 = 0;
		s6 = 0;
		s7 = 0;

		for(Robot r: c.getRobots()){
			if(!r.getSensorValue((byte) 0))
				s1++;
			if(!r.getSensorValue((byte) 1))
				s2++;
			if(!r.getSensorValue((byte) 2))
				s3++;
			if(!r.getSensorValue((byte) 3))
				s4++;
			if(!r.getSensorValue((byte) 4))
				s5++;
			if(!r.getSensorValue((byte) 5))
				s6++;
			if(!r.getSensorValue((byte) 6))
				s7++;
		}
		
		JLabel sensor1 = new JLabel("Robots con sensore 1 down: ");
		sensor1.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(sensor1, "pos 20px 150px, width 100, height 30");
		
		num_s1 = new JLabel("" + s1);
		num_s1.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(num_s1, "pos 300px 150px, width 100, height 30");
		
		JButton show_rbt1 = new JButton("Visualizza");
		cluster_pnl.add(show_rbt1, "pos 410px 150px, width 100, height 30");
		
		show_rbt1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnl.setVisible(false);
				currentScreen = new RobotListSensor(c, 0);
		}});
		
		JLabel sensor2 = new JLabel("Robots con sensore 2 down: ");
		sensor2.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(sensor2, "pos 20px 200px, width 100, height 30");
		
		num_s2 = new JLabel(""+s2);
		num_s2.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(num_s2, "pos 300px 200px, width 100, height 30");
		
		JButton show_rbt2 = new JButton("Visualizza");
		cluster_pnl.add(show_rbt2, "pos 410px 200px, width 100, height 30");
		
		show_rbt2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnl.setVisible(false);
				currentScreen = new RobotListSensor(c, 1);
		}});
		
		JLabel sensor3 = new JLabel("Robots con sensore 3 down: ");
		sensor3.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(sensor3, "pos 20px 250px, width 100, height 30");
		
		num_s3 = new JLabel(""+s3);
		num_s3.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(num_s3, "pos 300px 250px, width 100, height 30");
		
		JButton show_rbt3 = new JButton("Visualizza");
		cluster_pnl.add(show_rbt3, "pos 410px 250px, width 100, height 30");
		
		show_rbt3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnl.setVisible(false);
				currentScreen = new RobotListSensor(c, 2);
		}});
		
		JLabel sensor4 = new JLabel("Robots con sensore 4 down: ");
		sensor4.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(sensor4, "pos 20px 300px, width 100, height 30");
		
		num_s4 = new JLabel(""+s4);
		num_s4.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(num_s4, "pos 300px 300px, width 100, height 30");
		
		JButton show_rbt4 = new JButton("Visualizza");
		cluster_pnl.add(show_rbt4, "pos 410px 300px, width 100, height 30");
		
		show_rbt4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnl.setVisible(false);
				currentScreen = new RobotListSensor(c, 3);
		}});
		
		JLabel sensor5 = new JLabel("Robots con sensore 5 down: ");
		sensor5.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(sensor5, "pos 20px 350px, width 100, height 30");
		
		num_s5 = new JLabel(""+s5);
		num_s5.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(num_s5, "pos 300px 350px, width 100, height 30");
		
		JButton show_rbt5 = new JButton("Visualizza");
		cluster_pnl.add(show_rbt5, "pos 410px 350px, width 100, height 30");
		
		show_rbt5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnl.setVisible(false);
				currentScreen = new RobotListSensor(c, 4);
		}});
		
		JLabel sensor6 = new JLabel("Robots con sensore 6 down: ");
		sensor6.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(sensor6, "pos 20px 400px, width 100, height 30");
		
		num_s6 = new JLabel(""+s6);
		num_s6.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(num_s6, "pos 300px 400px, width 100, height 30");
		
		JButton show_rbt6 = new JButton("Visualizza");
		cluster_pnl.add(show_rbt6, "pos 410px 400px, width 100, height 30");
		
		show_rbt6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnl.setVisible(false);
				currentScreen = new RobotListSensor(c, 5);
		}});

		JLabel sensor7 = new JLabel("Robots con sensore 7 down: ");
		sensor7.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(sensor7, "pos 20px 450px, width 100, height 30");
		
		num_s7 = new JLabel(""+s7);
		num_s7.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(num_s7, "pos 300px 450px, width 100, height 30");
		
		JButton show_rbt7 = new JButton("Visualizza");
		cluster_pnl.add(show_rbt7, "pos 410px 450px, width 100, height 30");
		
		show_rbt7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnl.setVisible(false);
				currentScreen = new RobotListSensor(c, 6);
		}});
		
		lista_rbt = new JLabel("Robots nel Cluster " + "#" + c.getRobots().size());
		lista_rbt.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		cluster_pnl.add(lista_rbt, "pos 20px 500px, width 100, height 30");
		
		//Pannello che conterrà la lista di bottoni
		panel_list = new JPanel();
		panel_list.setLayout(new MigLayout());
		
		//Lista di bottoni, uno per ogni robot nel cluster
		robots_btns = new ArrayList<JButton>();
		
		//For che aggiunge i bottoni alla lista, attribuendogli l'ID del robot
		for(Robot r: c.getRobots())
			robots_btns.add(new JButton(r.getID() + " IR: " + r.getIR() + "%"));
		
		//For che aggiunge i bottoni al panel
		int i = 0, pos = 0;
		for(JButton jb: robots_btns){
			pos = 40 * i;
			i++;
			panel_list.add(jb, "pos 0px " + pos +"px, width 200, height 30");
			
			if(r_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))).getIR()>=40)
				jb.setBackground(Color.RED);
	
			jb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pnl.setVisible(false);
					currentScreen = new ViewRobot(r_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))));
				}});
		}
		
		cluster_pnl.add(panel_list, "pos 20px 550px");
		
		//Scroll per la lista
		JScrollPane scroll = new JScrollPane(cluster_pnl);
		scroll.setVerticalScrollBarPolicy ( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		pnl.add(scroll, "pos 0px 0px, width 688, height 470");
		
		JButton btnHome = new JButton("Home");
		cluster_pnl.add(btnHome, "pos 540px 10px, width 110, height 15");

		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnl.setVisible(false);
				currentScreen = new Dashboard();
		}});
	}

	@Override
	public void update() {
		String newClusterObjID = c.getID();
		c = c_map.get(newClusterObjID);
		
		ir_cls.setText("" + c.getIR() + "%");
		s1 = 0;
		s2 = 0;
		s3 = 0;
		s4 = 0;
		s5 = 0;
		s6 = 0;
		s7 = 0;
		for(Robot r: c.getRobots()){
			if(!r.getSensorValue((byte) 0))
				s1++;
			if(!r.getSensorValue((byte) 1))
				s2++;
			if(!r.getSensorValue((byte) 2))
				s3++;
			if(!r.getSensorValue((byte) 3))
				s4++;
			if(!r.getSensorValue((byte) 4))
				s5++;
			if(!r.getSensorValue((byte) 5))
				s6++;
			if(!r.getSensorValue((byte) 6))
				s7++;
		}
		num_s1.setText(""+s1);
		num_s2.setText(""+s2);
		num_s3.setText(""+s3);
		num_s4.setText(""+s4);
		num_s5.setText(""+s5);
		num_s6.setText(""+s6);
		num_s7.setText(""+s7);
		
		if(c.getRobots().size() > robots_btns.size()){
			LinkedList<Robot> newRobots = new LinkedList<Robot>();
			for(Robot newRobot : c.getRobots()){
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
			int i = 0, pos = 0;
			for(Robot toAdd : newRobots){
				JButton jb = new JButton(toAdd.getID() + " IR: "+ toAdd.getIR() + "%");
				robots_btns.add(jb);
				pos = 40 * i;
				i++;
				panel_list.add(jb, "pos 0px " + pos +"px, width 200, height 30");
				jb.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						pnl.setVisible(false);
						currentScreen = new ViewRobot(r_map.get(jb.getText().substring(0, jb.getText().indexOf(" "))));
					}
				});
			}
		}else{
			for(JButton jb : robots_btns){
				Robot r = r_map.get(jb.getText().substring(0, jb.getText().indexOf(" ")));
				jb.setText(r.getID() + " IR: " + r.getIR() + "%");
			}
		}
		
		lista_rbt.setText("Robots nel Cluster " + "#" + c.getRobots().size());
		
		lblTime.setText(lastUpdate);
	}
}

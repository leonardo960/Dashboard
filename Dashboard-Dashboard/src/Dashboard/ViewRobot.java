package Dashboard;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class ViewRobot {
	private JFrame window;
	private Robot r;
	private HashMap<String, Cluster> c_map;
	private HashMap<String, Robot> r_map;
	
	public ViewRobot(JFrame w, Robot r, HashMap<String, Cluster> c_map, HashMap<String, Robot> r_map){
		window = w;
		this.r = r;
		this.c_map = c_map;
		this.r_map = r_map;
		
		initialize();
	}
	
	public void initialize(){
		
		JPanel robot_pnl = new JPanel();
		robot_pnl.setLayout(new MigLayout());
		robot_pnl.setBounds(0,0,700,500);
		robot_pnl.setVisible(true);
		window.getContentPane().add(robot_pnl);
		
		JLabel clst = new JLabel("Informazioni robot");
		clst.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 20));
		robot_pnl.add(clst, "width 300, height 25");
		
		JLabel id = new JLabel("ID robot: ");
		id.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(id, "pos 20px 30px, width 100px, height 30px");
		
		JLabel id_cls = new JLabel(r.getID());
		id_cls.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(id_cls, "pos 130px 30px, width 100, height 30");
	
		JLabel ir = new JLabel("Inefficiency rate robot: ");
		ir.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(ir, "pos 20px 80px, width 100, height 30");
		
		JLabel ir_rbt = new JLabel(""+r.getIR());
		ir_rbt.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(ir_rbt, "pos 230px 80px, width 100, height 30");
		
		JLabel sensor1 = new JLabel("Stato sensore 1: ");
		sensor1.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(sensor1, "pos 20px 130px, width 100, height 30");
		
		JLabel s1 = new JLabel();
		if(r.getSensorValue((byte) 0))
			s1.setText("UP");
		else
			s1.setText("DOWN");
		s1.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(s1, "pos 300px 130px, width 100, height 30");
		
		JLabel sensor2 = new JLabel("Stato sensore 2: ");
		sensor2.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(sensor2, "pos 20px 180px, width 100, height 30");
		
		JLabel s2 = new JLabel();
		if(r.getSensorValue((byte) 1))
			s2.setText("UP");
		else
			s2.setText("DOWN");
		s2.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(s2, "pos 300px 180px, width 100, height 30");
		
		JLabel sensor3 = new JLabel("Stato sensore 3: ");
		sensor3.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(sensor3, "pos 20px 230px, width 100, height 30");
		
		JLabel s3 = new JLabel();
		if(r.getSensorValue((byte) 2))
			s3.setText("UP");
		else
			s3.setText("DOWN");
		s3.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(s3, "pos 300px 230px, width 100, height 30");
		
		JLabel sensor4 = new JLabel("Stato sensore 4: ");
		sensor4.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(sensor4, "pos 20px 280px, width 100, height 30");
		
		JLabel s4 = new JLabel();
		if(r.getSensorValue((byte) 3))
			s4.setText("UP");
		else
			s4.setText("DOWN");
		s4.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(s4, "pos 300px 280px, width 100, height 30");
		
		JLabel sensor5 = new JLabel("Stato sensore 5: ");
		sensor5.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(sensor5, "pos 20px 330px, width 100, height 30");
		
		JLabel s5 = new JLabel();
		if(r.getSensorValue((byte) 4))
			s5.setText("UP");
		else
			s5.setText("DOWN");
		s5.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(s5, "pos 300px 330px, width 100, height 30");
		
		JLabel sensor6 = new JLabel("Stato sensore 6: ");
		sensor6.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(sensor6, "pos 20px 380px, width 100, height 30");
		
		JLabel s6 = new JLabel();
		if(r.getSensorValue((byte) 5))
			s6.setText("UP");
		else
			s6.setText("DOWN");
		s6.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(s6, "pos 300px 380px, width 100, height 30");

		JLabel sensor7 = new JLabel("Stato sensore 7: ");
		sensor7.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(sensor7, "pos 20px 430px, width 100, height 30");
		
		JLabel s7 = new JLabel();
		if(r.getSensorValue((byte) 6))
			s7.setText("UP");
		else
			s7.setText("DOWN");
		s7.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
		robot_pnl.add(s7, "pos 300px 430px, width 100, height 30");
		
		JButton btnHome = new JButton("Home");
		robot_pnl.add(btnHome, "pos 540px 10px, width 110, height 15");

		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot_pnl.setVisible(false);
				new Dashboard(c_map, r_map);
		}});
		
		JButton btnCluster = new JButton("Cluster");
		robot_pnl.add(btnCluster, "pos 540px 45px, width 110, height 15");

		btnCluster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot_pnl.setVisible(false);
				new ViewCluster(window, c_map.get(r.getCluster()), c_map, r_map);
		}});
	}
	
		
}
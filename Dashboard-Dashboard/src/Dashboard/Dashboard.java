package Dashboard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
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
		
		JButton cerca = new JButton("Cerca per ID");
		panel.add(cerca, "pos 250px 320px, width 200, height 70");
		
		cerca.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.setVisible(false);
				currentScreen = new ViewSearch();
			}});
	
		JButton soglia = new JButton("Imposta soglia IR");
		panel.add(soglia, "pos 250px 190px, width 200, height 70");
		
		soglia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Inserire il valore soglia: ");
				threshold = Integer.valueOf(input);
			}});
	}

	@Override
	public void update() {
		//Nothing to update here
	}

}
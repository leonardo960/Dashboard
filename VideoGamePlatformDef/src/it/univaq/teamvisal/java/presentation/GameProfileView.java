package it.univaq.teamvisal.java.presentation;

import javax.swing.JPanel;

import it.univaq.teamvisal.java.DatabaseConnectionException;
import it.univaq.teamvisal.java.ScreenView;
import it.univaq.teamvisal.java.ScreenViewSuper;
import it.univaq.teamvisal.java.business.impl.JDBCGameManager;
import it.univaq.teamvisal.java.business.impl.ScreenController;
import it.univaq.teamvisal.java.business.model.Game;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeMap;
import java.awt.event.ActionEvent;

public class GameProfileView extends ScreenViewSuper implements ScreenView {
	private JPanel card;
	private JLabel gameTitle;
	private JButton reviewsBtn;
	private JLabel gameDescription;
	private Game displayedGame;
	
	public GameProfileView(){
		screenName = "GAMEPROFILESCREEN";
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public JPanel initialize() {
		card = new JPanel();
		card.setLayout(null);
		
		gameDescription = new JLabel("");
		gameDescription.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 13));
		gameDescription.setVerticalAlignment(SwingConstants.TOP);
		gameDescription.setHorizontalAlignment(SwingConstants.CENTER);
		gameDescription.setForeground(Color.WHITE);
		gameDescription.setBounds(110, 129, 285, 138);
		card.add(gameDescription);
		
		gameTitle = new JLabel("");
		gameTitle.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 25));
		gameTitle.setHorizontalAlignment(SwingConstants.CENTER);
		gameTitle.setForeground(Color.WHITE);
		gameTitle.setBounds(133, 11, 238, 110);
		card.add(gameTitle);
		
		JButton play = new JButton("Gioca!");
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playGame();
			}
		});
		play.setBackground(Color.BLACK);
		play.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 15));
		play.setForeground(Color.WHITE);
		play.setFocusable(false);
		play.setBounds(107, 278, 130, 43);
		card.add(play);
		
		reviewsBtn = new JButton("Recensioni");
		reviewsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScreenController.setScreen("GAMEREVIEWSCREEN");
				((GameReviewView) ScreenController.getLoadedScreens().get("GAMEREVIEWSCREEN")).populateList(displayedGame);
			}
		});
		reviewsBtn.setBackground(Color.BLACK);
		reviewsBtn.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 15));
		reviewsBtn.setForeground(Color.WHITE);
		reviewsBtn.setFocusable(false);
		reviewsBtn.setBounds(265, 278, 130, 43);
		card.add(reviewsBtn);
		
		JButton back = new JButton("Indietro");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScreenController.setPreviousScreen(screenName);
			}
		});
		back.setBackground(Color.BLACK);
		back.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 15));
		back.setForeground(Color.WHITE);
		back.setFocusable(false);
		back.setBounds(190, 332, 130, 43);
		card.add(back);
		
		JLabel background = new JLabel("");
		background.setIcon(new ImageIcon("C:\\Users\\Leonardo Formichetti\\git\\VideoGamePlatformDef\\bg.jpg"));
		background.setBounds(0, 0, 500, 500);
		card.add(background);
		
		return card;
	}

	@Override
	protected void clearTextFields() {
		//NEVER USED
	}
	
	public void populateFields(Game game){
		gameTitle.setText(game.getGameTitle());
		gameDescription.setText("<html><p>" + game.getDescription() + "</p></html>");
		displayedGame = game;
	}
	
	private void playGame(){
		ProcessBuilder pb = new ProcessBuilder("java", "-jar", "\"C:\\Users\\Leonardo Formichetti\\git\\VideoGamePlatformDef\\games\\" + displayedGame.getGameTitle() + ".jar\"");
		try{ 
			Process p;
			p = pb.start();
			p.waitFor();
			System.out.println("Il gioco ha finito di eseguire.");
		}catch(IOException | InterruptedException e){
			if(e instanceof IOException){
				JOptionPane.showMessageDialog(card, "Errore di I/O nell'aprire il gioco", "Errore", JOptionPane.ERROR_MESSAGE);
			}else if(e instanceof InterruptedException){
				JOptionPane.showMessageDialog(card, "L'applicazione � ripartita senza aspettare il gioco", "Errore", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		
	}
}
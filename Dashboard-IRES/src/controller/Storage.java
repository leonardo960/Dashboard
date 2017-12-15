package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import model.Segnale;
import model.FinestraTemporale;

public class Storage {
	static private Connection con;
	
	static public void inizializza(){
		con = dbConnect();
	}
	static private Connection dbConnect() {
		Connection con = null;
		try {
		con = DriverManager.getConnection("jdbc:mysql://localhost/dashboard?useSSL=true", "root", "lorenzo96");
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
		
	}
	
	static public void apriFinestraTemporaleRobot(Segnale segnale){
		String sql = "insert into finestra_temporale "
				+ "(id_oggetto, sogliaSinistra, sogliaDestra) values (?, ?, null)";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, String.valueOf(segnale.getRobotID()));
			ps.setTimestamp(2, segnale.getTimestamp());
			ps.executeUpdate();
			
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void chiudiFinestraTemporaleRobot(Segnale segnale){
		String sql = "update finestra_temporale set sogliaDestra = ? where id_oggetto = ? "
				+ "order by sogliaSinistra desc limit 1";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, String.valueOf(segnale.getRobotID()));
			ps.setTimestamp(2, segnale.getTimestamp());
			ps.executeUpdate();
			
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void apriFinestraTemporaleCluster(Segnale segnale){
		String sql = "insert into finestra_temporale "
				+ "(id_oggetto, sogliaSinistra, sogliaDestra) values (?, ?, null)";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, String.valueOf(segnale.getClusterID()));
			ps.setTimestamp(2, segnale.getTimestamp());
			ps.executeUpdate();
			
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void chiudiFinestraTemporaleCluster(Segnale segnale){
		String sql = "update finestra_temporale set sogliaDestra = ? where id_oggetto = ? "
				+ "order by sogliaSinistra desc limit 1";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, String.valueOf(segnale.getClusterID()));
			ps.setTimestamp(2, segnale.getTimestamp());
			ps.executeUpdate();
			
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public HashMap<String, LinkedList<FinestraTemporale>> prelevaFinestreTemporali(){
		HashMap<String, LinkedList<FinestraTemporale>> finestreTemporali = new HashMap<String, LinkedList<FinestraTemporale>>();
		
		String sql = "select * from finestra_temporale";
		try {
			Statement statement = con.createStatement();
	
			ResultSet rs = statement.executeQuery(sql);
			
			while(rs.next()){
				String id = rs.getString("id_oggetto");
				
				if(finestreTemporali.containsKey(id)){
					finestreTemporali.get(id).add(new FinestraTemporale(id, rs.getTimestamp("sogliaSinistra"), rs.getTimestamp("sogliaDestra")));
				}else{
					finestreTemporali.put(id, new LinkedList<FinestraTemporale>());
					finestreTemporali.get(id).add(new FinestraTemporale(id, rs.getTimestamp("sogliaSinistra"), rs.getTimestamp("sogliaDestra")));
				}
			}
			
			rs.close();
			statement.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finestreTemporali;
	}
	
	static public void rimuoviFinestreInattive(Timestamp oneHourAgo){
		String sql = "delete from finestra_temporale where sogliaDestra < ?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setTimestamp(1, oneHourAgo);
			ps.executeUpdate();
			
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

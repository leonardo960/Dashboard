package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;

import model.Segnale;
import model.FinestraTemporale;

public class Storage {
	static private Connection con;
	static private PreparedStatement batchInsert;
	static private PreparedStatement batchUpdate;
	static private int batchInsertCount;
	static private int batchInsertSize;
	static private int batchUpdateCount;
	static private int batchUpdateSize;
	
	static public void inizializza(String[] args){
		con = dbConnect(args);
		//Procedura di creazione database
		//*********************************
		//*Da finire quando ho connessione*
		//*********************************
		/*try {
			Statement create = con.createStatement();
			String createSQL = "create database dashboard";
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		batchInsertCount = 0;
		batchInsertSize = 1000;
		batchUpdateCount = 0;
		batchUpdateSize = 1000;
		try {
			batchInsert = con.prepareStatement("insert into finestra_temporale (id_oggetto, sogliaSinistra, sogliaDestra) values (?, ?, null)");
			batchUpdate = con.prepareStatement("update finestra_temporale set sogliaDestra = ? where id_oggetto = ? order by sogliaSinistra desc limit 1");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static private Connection dbConnect(String[] args) {
		Connection con = null;
		try {
		con = DriverManager.getConnection("jdbc:mysql://localhost/?useSSL=true", args[0], args[1]);
		con.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
		
	}
	
	
	static public void apriFinestraTemporaleRobot(Segnale segnale){
		try {
			batchInsert.setString(1, String.valueOf(segnale.getRobotID()));
			batchInsert.setTimestamp(2, segnale.getTimestamp());
			batchInsert.addBatch();
			if(++batchInsertCount % batchInsertSize == 0){
				batchInsert.executeBatch();
				con.commit();
				batchInsert.clearBatch();
				batchInsertCount = 0;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void chiudiFinestraTemporaleRobot(Segnale segnale){
		try {
			batchUpdate.setTimestamp(1, segnale.getTimestamp());
			batchUpdate.setString(2, String.valueOf(segnale.getRobotID()));
			batchUpdate.addBatch();
			System.out.println("batchUpdateCount:" + batchUpdateCount);
			if(++batchUpdateCount % batchUpdateSize == 0){
				//Devo assicurarmi prima di flushare le insert o si creano problemi
				//del tipo che l'update non trova la riga (che doveva essere inserita
				//da una insert i cui parametri sono rimasti nel batch che deve
				//ancora flushare)
				batchInsert.executeBatch();
				batchInsert.clearBatch();
				batchInsertCount = 0;
				batchUpdate.executeBatch();
				con.commit();
				batchUpdate.clearBatch();
				batchUpdateCount = 0;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void commitChanges(){
		try {
			batchInsert.executeBatch();
			con.commit();
			batchUpdate.executeBatch();
			con.commit();
			batchInsert.clearBatch();
			batchUpdate.clearBatch();
			batchUpdateCount = 0;
			batchInsertCount = 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	static public void apriFinestraTemporaleCluster(Segnale segnale){
		try {
			batchInsert.setString(1, String.valueOf(segnale.getClusterID()));
			batchInsert.setTimestamp(2, segnale.getTimestamp());
			batchInsert.addBatch();
			if(++batchInsertCount % batchInsertSize == 0){
				batchInsert.executeBatch();
				con.commit();
				batchInsert.clearBatch();
				batchInsertCount = 0;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void chiudiFinestraTemporaleCluster(Segnale segnale){
		try {
			batchUpdate.setTimestamp(1, segnale.getTimestamp());
			batchUpdate.setString(2, String.valueOf(segnale.getClusterID()));
			System.out.println("batchUpdateCount:" + batchUpdateCount);
			if(++batchUpdateCount % batchUpdateSize == 0){
				//Stesso discorso di chiudiFinestraTemporaleRobot,
				//devo assicurarmi che le insert siano state fatte prima
				//delle update
				batchInsert.executeBatch();
				batchInsert.clearBatch();
				batchInsertCount = 0;
				batchUpdate.executeBatch();
				con.commit();
				batchUpdateCount = 0;
				batchUpdate.clearBatch();
			}
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

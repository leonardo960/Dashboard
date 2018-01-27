package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
	static private List<PreparedStatement> insertBatches;
	static private List<PreparedStatement> updateBatches;
	static private List<Integer> insertBatchesCounters;
	static private List<Integer> updateBatchesCounters;
	static private List<Boolean> flushChecks;
	static private AtomicInteger workingBatch;
	static private AtomicInteger toBeFlushed;
	static private int batchesNum;
	static public void inizializza(String[] args){
		//con = dbConnect(args);
		
		//Procedura di creazione database
		/*try {
			Statement create = con.createStatement();
			String createSQL = "create database dashboard";
			create.execute(createSQL);
			createSQL = "use dashboard";
			create.execute(createSQL);
			createSQL = "create table finestra_temporale("
					+ "id int primary key auto_increment,"
					+ "id_oggetto varchar(7) default \"errore\","
					+ "sogliaSinistra timestamp default null,"
					+ "sogliaDestra timestamp default null);";
			create.execute(createSQL);
			create.close();
			con.commit();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		new Thread(){
			public void run(){
				con = dbConnect(args);
				batchInsertCount = 0;
				batchInsertSize = 50;
				batchUpdateCount = 0;
				batchUpdateSize = 50;
				try {
					batchesNum = 10;
					workingBatch = new AtomicInteger(0);
					toBeFlushed = new AtomicInteger(0);
					insertBatches = Collections.synchronizedList(new ArrayList<PreparedStatement>(batchesNum));
					updateBatches = Collections.synchronizedList(new ArrayList<PreparedStatement>(batchesNum));
					insertBatchesCounters = Collections.synchronizedList(new ArrayList<Integer>(batchesNum));
					updateBatchesCounters = Collections.synchronizedList(new ArrayList<Integer>(batchesNum));
					flushChecks = Collections.synchronizedList(new ArrayList<Boolean>(batchesNum));
					for(int i = 0; i < batchesNum; i++){
						insertBatches.add(i, con.prepareStatement("insert into finestra_temporale (id_oggetto, sogliaSinistra, sogliaDestra) values (?, ?, null)"));
						updateBatches.add(i, con.prepareStatement("update finestra_temporale set sogliaDestra = ? where id_oggetto = ? order by sogliaSinistra desc limit 1"));
						insertBatchesCounters.add(i, 0);
						updateBatchesCounters.add(i, 0);
						flushChecks.add(i, false);
					}
					
					//batchInsert = con.prepareStatement("insert into finestra_temporale (id_oggetto, sogliaSinistra, sogliaDestra) values (?, ?, null)");
					//batchUpdate = con.prepareStatement("update finestra_temporale set sogliaDestra = ? where id_oggetto = ? order by sogliaSinistra desc limit 1");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				while(true){
					if(flushChecks.get(toBeFlushed.get())){
						try {
							insertBatches.get(toBeFlushed.get()).executeBatch();
							updateBatches.get(toBeFlushed.get()).executeBatch();
							insertBatches.get(toBeFlushed.get()).clearBatch();
							updateBatches.get(toBeFlushed.get()).clearBatch();
							insertBatchesCounters.set(toBeFlushed.get(), 0);
							updateBatchesCounters.set(toBeFlushed.get(), 0);
							flushChecks.set(toBeFlushed.get(), false);
							toBeFlushed.set((toBeFlushed.get() + 1) % batchesNum);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
		}.start();
		
	}
	static private Connection dbConnect(String[] args) {
		Connection con = null;
		try {
		con = DriverManager.getConnection("jdbc:mysql://localhost/?useSSL=true", args[0], args[1]);
		//con.setAutoCommit(false);
		Statement useDBStatement = con.createStatement();
		String useDBcommand = "use dashboard";
		useDBStatement.execute(useDBcommand);
		//con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
		
	}
	
	
	/*static public void apriFinestraTemporaleRobot(Segnale segnale){
		try {
			batchInsert.setString(1, segnale.getRobotID());
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
	}*/
	
	static public void apriFinestraTemporaleRobot(Segnale segnale){
		try {
			int temp = workingBatch.get();
			insertBatches.get(temp).setString(1, segnale.getRobotID());
			insertBatches.get(temp).setTimestamp(2, segnale.getTimestamp());
			insertBatches.get(temp).addBatch();
			//insertBatchesCounters.set(temp, insertBatchesCounters.get(temp)+1);
			/*if(insertBatchesCounters.get(workingBatch.get()) == batchInsertSize){
				flushChecks.set(workingBatch.get(), true);
				workingBatch.set((workingBatch.get() + 1) % batchesNum);
			}*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*static public void chiudiFinestraTemporaleRobot(Segnale segnale){
		try {
			batchUpdate.setTimestamp(1, segnale.getTimestamp());
			batchUpdate.setString(2, segnale.getRobotID());
			batchUpdate.addBatch();
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
	}*/
	
	static public void chiudiFinestraTemporaleRobot(Segnale segnale){
		try {
			int temp = workingBatch.get();
			updateBatches.get(temp).setTimestamp(1, segnale.getTimestamp());
			updateBatches.get(temp).setString(2, segnale.getRobotID());
			updateBatches.get(temp).addBatch();
			//updateBatchesCounters.set(temp, updateBatchesCounters.get(temp)+1);
			
			/*if(updateBatchesCounters.get(workingBatch.get()) == batchUpdateSize){
				flushChecks.set(workingBatch.get(), true);
				workingBatch.set((workingBatch.get() + 1) % batchesNum);
			}*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*static public void commitChanges(){
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
	}*/
	
	static public void commitChanges(){
		flushChecks.set(workingBatch.get(), true);
		workingBatch.set((workingBatch.get() + 1) % batchesNum);
	}
	
	/*static public void apriFinestraTemporaleCluster(Segnale segnale){
		try {
			batchInsert.setString(1, segnale.getClusterID());
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
	}*/
	
	static public void apriFinestraTemporaleCluster(Segnale segnale){
		try {
			int temp = workingBatch.get();
			insertBatches.get(temp).setString(1, segnale.getClusterID());
			insertBatches.get(temp).setTimestamp(2, segnale.getTimestamp());
			insertBatches.get(temp).addBatch();
			//insertBatchesCounters.set(temp, insertBatchesCounters.get(temp)+1);
			/*if(insertBatchesCounters.get(workingBatch.get()) == batchInsertSize){
				flushChecks.set(workingBatch.get(), true);
				workingBatch.set((workingBatch.get() + 1) % batchesNum);
			}*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/*static public void chiudiFinestraTemporaleCluster(Segnale segnale){
		try {
			batchUpdate.setTimestamp(1, segnale.getTimestamp());
			batchUpdate.setString(2, segnale.getClusterID());
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
	}*/
	
	static public void chiudiFinestraTemporaleCluster(Segnale segnale){
		try {
			int temp = workingBatch.get();
			updateBatches.get(temp).setTimestamp(1, segnale.getTimestamp());
			updateBatches.get(temp).setString(2, segnale.getClusterID());
			updateBatches.get(temp).addBatch();
			//updateBatchesCounters.set(temp, updateBatchesCounters.get(temp)+1);
			/*if(updateBatchesCounters.get(workingBatch.get()) == batchUpdateSize){
				flushChecks.set(workingBatch.get(), true);
				workingBatch.set((workingBatch.get() + 1) % batchesNum);
			}*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public HashMap<String, LinkedList<FinestraTemporale>> prelevaFinestreTemporali(Connection con){
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

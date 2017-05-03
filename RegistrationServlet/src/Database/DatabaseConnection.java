package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatabaseConnection {
	private static Connection connection = null;
	
	public DatabaseConnection() {
		try {
			Class.forName("org.postgresql.Driver");

			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/nakup", "postgres",
					"123456");
			connection.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");
		//return connection;
	}
	
	public String createHuman(String name){	
		String sql = "SELECT id FROM clovek "
				+ "ORDER BY id desc "
				+ "LIMIT 1";
		String newHumanID = null;
		try{
			Statement stm = connection.createStatement();			
			ResultSet results = stm.executeQuery(sql); //zisti posledne maximalne ID
			
			if (results.next()) {
				ResultSetMetaData metadata = results.getMetaData();
				String columnName = metadata.getColumnName(1).toString();
				newHumanID = Integer.toString(Integer.valueOf(results.getObject(columnName).toString()) + 1).toString();
			}
			else newHumanID = "1";
			
			sql = "BEGIN; INSERT INTO CLOVEK (id, name) VALUES ("+newHumanID + " , '" + name +"')";
			stm.executeUpdate(sql);
			connection.commit();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return newHumanID;
	}
	
	public List<String> allHumansGroups(String humanID){
		//String g = null;
		List<String> groups = new ArrayList<String>();
		String sql = "SELECT id, name, zoznam FROM skupina as s"
				+ "join skupina_clovek as sc"
				+ "on sc.clovek_id = "+ humanID;
		try{
			Statement stm = connection.createStatement();
			ResultSet results = stm.executeQuery(sql);
			ResultSetMetaData metadata = results.getMetaData();
			String[] columnName = new String[metadata.getColumnCount()]; 
			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				columnName[i - 1] = metadata.getColumnName(i).toString();
			}			
			while (results.next()) {
				Object[] row = new Object[metadata.getColumnCount()-1];
				for (int i = 0; i < metadata.getColumnCount()-1; i++) {
					row[i] = results.getObject(columnName[i]);
				}
				String g = row[0].toString() + "\n" + row[1] + "\n" + row[2].toString().length() + "\n" + row[2] +"\n"; 
				groups.add(g);
			}
			results.close();
			stm.close();
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return groups;
	}
	
	public String createGroup(String humanID, String groupName){
		String sql = "SELECT id FROM skupina "
				+ "ORDER BY id desc "
				+ "LIMIT 1";
		String newGroupID = null;
		try{
			Statement stm = connection.createStatement();			
			ResultSet results = stm.executeQuery(sql); //zisti posledne maximalne ID
			
			if (results.next()) {
				ResultSetMetaData metadata = results.getMetaData();
				String columnName = metadata.getColumnName(1).toString();
				newGroupID = Integer.toString(Integer.valueOf(results.getObject(columnName).toString()) + 1).toString();
			}
			else newGroupID = "1";
			
			sql = "BEGIN; INSERT INTO SKUPINA (id, name, zoznam) VALUES ("+newGroupID + " , '" + groupName +"', ' ')";
			stm.executeUpdate(sql);
			sql = "INSERT INTO SKUPINA_CLOVEK (skupina_id, clovek_id) VALUES ("+newGroupID + " , '" + humanID +")";
			stm.executeUpdate(sql);
			connection.commit();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return newGroupID;
	}
	
	public String addHumanToGroup(String humanID, String groupID){
		String sql = "SELECT zoznam FROM skupina "
				+ "where id = " + groupID;
		String zoznam = null;
		try{
			Statement stm = connection.createStatement();			
			ResultSet results = stm.executeQuery(sql); 
			
			if (results.next()) {
				ResultSetMetaData metadata = results.getMetaData();
				String columnName = metadata.getColumnName(1).toString();
				zoznam = results.getObject(columnName).toString();
			}
			
			sql = "INSERT INTO SKUPINA_CLOVEK (skupina_id, clovek_id) VALUES ("+groupID + " , '" + humanID +")";
			stm.executeUpdate(sql);
			connection.commit();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		
		return zoznam;
	}
	

}

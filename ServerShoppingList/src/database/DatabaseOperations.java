package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatabaseOperations {
	private static Connection connection = null;
	
	public DatabaseOperations() {
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
		/*String sql = "SELECT id FROM clovek "
				+ "ORDER BY id desc "
				+ "LIMIT 1";*/
		System.out.println("Vytvaram cloveka");
		String sql = "SELECT max(id) FROM clovek";
		String newHumanID = null;
		try{
			Statement stm = connection.createStatement();			
			ResultSet results = stm.executeQuery(sql); //zisti posledne maximalne ID
			
			if (results.next()) {
				/*ResultSetMetaData metadata = results.getMetaData();
				String columnName = metadata.getColumnName(1).toString();
				newHumanID = Integer.toString(Integer.valueOf(results.getObject(columnName).toString()) + 1).toString();*/
				newHumanID = results.getString("max");
				newHumanID = Integer.toString(Integer.parseInt(newHumanID) + 1);
			}
			else newHumanID = "1";
			
			sql = "BEGIN; INSERT INTO CLOVEK (id, name) VALUES ("+newHumanID + " , '" + name +"')";
			stm.executeUpdate(sql);
			connection.commit();
			System.out.println("Clovek vytvoreny");
		}
		catch(SQLException e){
			e.printStackTrace();
			
		}
		return newHumanID;
	}
	
	/*public List<String> allHumansGroups(String humanID){
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
	}*/
	
	/*public String createGroup(String humanID, String groupName){
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
	}*/
	
	/*public String addHumanToGroup(String humanID, String groupID){
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
	}*/
	
	public String addGroup(String humanIDGroupName){
		String humanID = humanIDGroupName.substring(0,humanIDGroupName.indexOf("\n"));
		String groupName = humanIDGroupName.substring(humanIDGroupName.indexOf("\n")+1);
		String groupID = null;
		
		String sql = "SELECT max(id) FROM skupina ";
		
		
		try{
			Statement stm = connection.createStatement();			
			ResultSet results = stm.executeQuery(sql); 
			
			if (results.next()) {
				groupID = Integer.toString((results.getInt("max") + 1));
			}
			else groupID = "1";
			
			sql = "BEGIN; INSERT INTO SKUPINA (id, name, zoznam) VALUES ("+groupID + " , '" + groupName +"', '')";
			stm.executeUpdate(sql);
			sql = "INSERT INTO SKUPINA_CLOVEK (skupina_id, clovek_id) VALUES ("+groupID + " , " + humanID +")";
			stm.executeUpdate(sql);
			connection.commit();
			
		}
		catch(SQLException e){
				
				e.printStackTrace();
		}
		return groupID;
		
	}
	
	public String leaveGroup(String humanIDGroupID){
		String humanID = humanIDGroupID.substring(0,humanIDGroupID.indexOf("\n"));
		String groupID = humanIDGroupID.substring(humanIDGroupID.indexOf("\n")+1);
		
		try{
			Statement stm = connection.createStatement();		
			
			String sql = "BEGIN; DELETE FROM SKUPINA_CLOVEK WHERE skupina_id = " + groupID + " AND clovek_id = " + humanID;
			stm.executeUpdate(sql);
			
			sql = "SELECT count(*) FROM SKUPINA_CLOVEK WHERE skupina_id = " + groupID;
			ResultSet results = stm.executeQuery(sql);
			results.next();
			
			if(results.getInt("count") == 0){
			    connection.commit();
				sql = "BEGIN; DELETE FROM SKUPINA WHERE id = " + groupID;
				stm.executeUpdate(sql);
			}

			connection.commit();
			
		}
		catch(SQLException e){
				e.printStackTrace();
		}
		
		
		return "OK";
	}
	
	public String showGroupText(String groupID){
		String zoznam = null;
		try{
			Statement stm = connection.createStatement();		
						
			String sql = "SELECT zoznam FROM SKUPINA WHERE id = " + groupID;
			ResultSet results = stm.executeQuery(sql);
			results.next();
			
			zoznam = results.getString("zoznam");

			connection.commit();
			
		}
		catch(SQLException e){
				e.printStackTrace();
			}
		
		return zoznam;
	}
	
	public String addToGroup(String humanIDGroupID){
		String humanID = humanIDGroupID.substring(0,humanIDGroupID.indexOf("\n"));
		String groupID = humanIDGroupID.substring(humanIDGroupID.indexOf("\n")+1);
		String nazovSkupiny = null;
		String sql = "BEGIN;INSERT INTO skupina_clovek (skupina_id, clovek_id) VALUES ("+groupID + " , '" + humanID +")";
		try{
			Statement stm = connection.createStatement();	
			stm.executeUpdate(sql);
			
			
			sql = "SELECT name FROM skupina WHERE id = " + groupID;
			ResultSet results = stm.executeQuery(sql);
			results.next();
			nazovSkupiny = results.getString("name");
			
			connection.commit();
		}
		catch(SQLException e){
				e.printStackTrace();
			}
		
		return nazovSkupiny;		
	}
	
	
	public String addItemToGroup(String groupIDText){
		String groupID = groupIDText.substring(0,groupIDText.indexOf("\n"));
		String text = groupIDText.substring(groupIDText.indexOf("\n")+1);
		String novyZoznam = null;
		
		String sql = "SELECT zoznam FROM skupina where id = " + groupID;
		
		try{
			Statement stm = connection.createStatement();	
			ResultSet results = stm.executeQuery(sql);
			
			results.next();
			novyZoznam = results.getString("zoznam");
			if(novyZoznam.length()<=0) novyZoznam = text;
			else novyZoznam = novyZoznam + "\n" + text;	
			sql = "BEGIN;UPDATE skupina SET zoznam = '" + novyZoznam + "' WHERE id = " + groupID;
			stm.executeUpdate(sql);
			connection.commit();
		}
		catch(SQLException e){
				e.printStackTrace();
			}
		
		return "OK";
		
	}
	
	public String deleteText(String groupIDRow){
		String groupID = groupIDRow.substring(0,groupIDRow.indexOf("\n"));
		int row = Integer.parseInt(groupIDRow.substring(groupIDRow.indexOf("\n")+1));
		String zoznam = null;
		String novyZoznam = "";
		
		System.out.println(Integer.toString(row));
		
		String sql = "SELECT zoznam FROM skupina WHERE id = " + groupID;
		
		try{
			Statement stm = connection.createStatement();	
			ResultSet results = stm.executeQuery(sql);
			
			results.next();
			zoznam = results.getString("zoznam");
			int i = 0;
			while(zoznam.contains("\n"))
			{
				if(i != row) novyZoznam = novyZoznam + zoznam.substring(0,zoznam.indexOf("\n")+1);
				zoznam = zoznam.substring(zoznam.indexOf("\n")+1);
				i++;
			}
			if( i!=row) novyZoznam = novyZoznam + zoznam;
			sql = "BEGIN;UPDATE skupina SET zoznam = '" + novyZoznam + "' WHERE id = " + groupID;
			stm.executeUpdate(sql);
			connection.commit();
			
		}
		catch(SQLException e){
				e.printStackTrace();
			}

		return "OK";
	}
	
	public String allGroups(String humanID)
	{
		String skupiny = "";
		String sql = "SELECT id,name FROM skupina as s "
				+ "join skupina_clovek as sc "
				+ "on s.id = sc.skupina_id "
				+ "where sc.clovek_id = " + humanID;
		try{
			Statement stm = connection.createStatement();	
			ResultSet results = stm.executeQuery(sql);
			while(results.next()){
				String ID = results.getString("id");
				skupiny = skupiny + ID + "\n";
				String nazov = results.getString("name");
				skupiny = skupiny + nazov + "\n";
			}
			//skupiny = skupiny.substring(0, skupiny.length()-1);
			System.out.println(skupiny);
		}
		catch(SQLException e){
				e.printStackTrace();
			}
		
		return skupiny;
	}
}

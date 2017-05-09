package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


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
	}
	
	public String createHuman(String name){	

		System.out.println("Vytvaram cloveka");
		String sql = "SELECT max(id) FROM clovek";
		String newHumanID = null;
		try{
			Statement stm = connection.createStatement();			
			ResultSet results = stm.executeQuery(sql); 
			
			if (results.next()) {
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
				sql = "DELETE FROM SKUPINA WHERE id = " + groupID;
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
		String list = null;
		try{
			Statement stm = connection.createStatement();		
						
			String sql = "SELECT zoznam FROM SKUPINA WHERE id = " + groupID;
			ResultSet results = stm.executeQuery(sql);
			results.next();
			
			list = results.getString("zoznam");

			connection.commit();
			
		}
		catch(SQLException e){
				e.printStackTrace();
			}
		
		return list;
	}
	
	public String addToGroup(String humanIDGroupID){
		String humanID = humanIDGroupID.substring(0,humanIDGroupID.indexOf("\n"));
		String groupID = humanIDGroupID.substring(humanIDGroupID.indexOf("\n")+1);
		String groupName = null;
		String sql = "SELECT count(*) FROM skupina WHERE ID = " + groupID;
		try{
			Statement stm = connection.createStatement();
			ResultSet results = stm.executeQuery(sql);
			results.next();
			if(results.getInt("count") == 0)
			{
				return "CHYBA";
			}
			
			
			sql = "BEGIN;INSERT INTO skupina_clovek (skupina_id, clovek_id) VALUES ("+groupID + " , '" + humanID +")";
			stm.executeUpdate(sql);
			
			
			sql = "SELECT name FROM skupina WHERE id = " + groupID;
			results = stm.executeQuery(sql);
			results.next();
			groupName = results.getString("name");
			
			connection.commit();
		}
		catch(SQLException e){
				e.printStackTrace();
			}
		
		return groupName;		
	}
	
	
	public String addItemToGroup(String groupIDText){
		String groupID = groupIDText.substring(0,groupIDText.indexOf("\n"));
		String text = groupIDText.substring(groupIDText.indexOf("\n")+1);
		String newList = null;
		
		String sql = "SELECT zoznam FROM skupina where id = " + groupID;
		
		try{
			Statement stm = connection.createStatement();	
			ResultSet results = stm.executeQuery(sql);
			
			results.next();
			newList = results.getString("zoznam");
			if(newList.length()<=0) newList = text;
			else newList = newList + "\n" + text;	
			sql = "BEGIN;UPDATE skupina SET zoznam = '" + newList + "' WHERE id = " + groupID;
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
		String list = null;
		String newList = "";
		
		System.out.println(Integer.toString(row));
		
		String sql = "SELECT zoznam FROM skupina WHERE id = " + groupID;
		
		try{
			Statement stm = connection.createStatement();	
			ResultSet results = stm.executeQuery(sql);
			
			results.next();
			list = results.getString("zoznam");
			
			int rowCount = list.length() - list.replace("\n", "").length();
			System.out.println(rowCount);
			if(rowCount == 0) 
			{
				newList = "";
			}
			else 
			{
				int i = 0;
				while(list.contains("\n"))
				{
					if(i != row)
					{
						newList = newList + list.substring(0,list.indexOf("\n")+1);
					}
					list = list.substring(list.indexOf("\n")+1);
					i++;
				}
				
				if( i != row)
				{
					newList = newList + list;
				}
			}
			sql = "BEGIN;UPDATE skupina SET zoznam = '" + newList + "' WHERE id = " + groupID;
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
		String groups = "";
		String sql = "SELECT id,name FROM skupina as s "
				+ "join skupina_clovek as sc "
				+ "on s.id = sc.skupina_id "
				+ "where sc.clovek_id = " + humanID;
		try{
			Statement stm = connection.createStatement();	
			ResultSet results = stm.executeQuery(sql);
			while(results.next()){
				String ID = results.getString("id");
				groups = groups + ID + "\n";
				String name = results.getString("name");
				groups = groups + name + "\n";
			}
			//skupiny = skupiny.substring(0, skupiny.length()-1);
			System.out.println(groups);
		}
		catch(SQLException e){
				e.printStackTrace();
			}
		
		return groups;
	}
}

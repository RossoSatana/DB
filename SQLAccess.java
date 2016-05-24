package db.restlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SQLAccess {
	protected Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public void connection() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			connect = DriverManager
					.getConnection("jdbc:mysql://localhost:3306/DBTESI?characterEncoding=UTF-8&useSSL=false",
							"root", "moonlight3");
					/*.getConnection("jdbc:mysql://localhost:3306/dbtesi?characterEncoding=UTF-8&useSSL=false",
												"root", "root"); */
		} catch (Exception e) {
			throw e;
		}
	}

	private String resultset_to_json(ResultSet rs) throws SQLException {
		JSONArray jarr = new JSONArray();
		while (rs.next()){
			HashMap<String, String> row = new HashMap<String, String>();
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) 
				row.put(rs.getMetaData().getColumnName(i), String.valueOf(rs.getObject(i)));
			jarr.put(new JSONObject(row));
		}
		return jarr.toString();
	}

	public String allUser ()  throws SQLException {
		statement = connect.createStatement();
		resultSet = statement.executeQuery("select * from USER");

		return resultset_to_json(resultSet);
	} 

	private boolean checkUser (String user)  throws SQLException {
		statement = connect.createStatement();
		resultSet = statement.executeQuery("select * from USER where ID = " + "'" + user + "'");

		if (!resultSet.next())
			return false; 	//user doesn't exist in DB

		return true;		//user exist in DB 
	} 

	private boolean checkOwner (String user, int COD_M) throws SQLException {
		statement = connect.createStatement();
		resultSet = statement.executeQuery("select * " +
				"from USER u, MONSTER m " +
				"where u.ID = " + "'" + user + "' " +
				"and m.COD_M = " + COD_M + " " +
				"and u.ID = m.ID_OWNER");

		if (!resultSet.next())
			return false; 	//user isn't the owner of monster COD_M

		return true;		//user is owner of COD_M 
	} 
	
	private boolean checkOwner (String user, String w_name) throws SQLException {
		statement = connect.createStatement();
		resultSet = statement.executeQuery("select * " +
				"from USER u, WEARABLE_OWNED w " +
				"where u.ID = " + "'" + user + "' " +
				"and w.W_NAME = " + "'" + w_name + "' " +
				"and u.ID = w.ID_OWNER");

		if (!resultSet.next())
			return false; 	//user isn't the owner of item w_name

		return true;		//user is owner of item w_name
	} 
	
	private String findOwner (int COD_M) throws SQLException {		//return ID of the owner
		statement = connect.createStatement();
		resultSet = statement.executeQuery("select ID_OWNER " +
				"from MONSTER_OWNED " +
				"where COD_M = " + COD_M );

		if (!resultSet.next())
			return null; 	//user isn't the owner of item w_name

		return resultSet.getString("ID_OWNER");		//user is owner of item w_name
	} 
	
	public int nEquipped (String user, String w_name) throws SQLException {		// ritorna la quantità di oggetti equipaggiati
		int nEquip;
		statement = connect.createStatement();
		resultSet = statement.executeQuery("select W_EQUIPPED " +
				"from USER u, WEARABLE_OWNED w " +
				"where u.ID = " + "'" + user + "' " +
				"and w.W_NAME = " + "'" + w_name + "' " +
				"and u.ID = w.ID_OWNER");

		if (!resultSet.next())
			return 0;
		
		nEquip= resultSet.getInt("W_EQUIPPED");
		return nEquip;
	} 
	
	public int nOwned (String user, String w_name) throws SQLException {		// ritorna la quantità di oggetti posseduti
		int nOwned;
		statement = connect.createStatement();
		resultSet = statement.executeQuery("select W_QUANTITY " +
				"from USER u, WEARABLE_OWNED w " +
				"where u.ID = " + "'" + user + "' " +
				"and w.W_NAME = " + "'" + w_name + "' " +
				"and u.ID = w.ID_OWNER");

		if (!resultSet.next())
			return 0;
		
		nOwned= resultSet.getInt("W_QUANTITY");
		return nOwned;
	} 
	
	public int nAvailable(String user, String w_name) throws SQLException {		// ritorna la quantità di oggetti posseduti non in uso
		return nOwned(user, w_name) - nEquipped(user, w_name);		//user is owner of item w_name
	} 
	
	public String insertUser (String user, String pw)  throws SQLException {
		if (checkUser(user) == true){
			return "Name already taken \n";			
		}
		statement = connect.createStatement();
		preparedStatement = connect
				.prepareStatement("insert into USER (ID, PW, LVL, MANA) values (?, ?, 1, 10)");

		preparedStatement.setString(1, user);
		preparedStatement.setString(2, pw);
		preparedStatement.executeUpdate();
		return "Utente registrato";	    
	} 

	public String loginUser (String user, String pw) throws SQLException { 

		statement = connect.createStatement();
		resultSet = statement.executeQuery("select * from USER where ID = " + "'" + user + "'" + " and PW = " + "'" + pw + "'");

		if (!resultSet.next())
			return "Login error: username or password mismatched \n"; 

		String response = "You are now logged in as: " + user;
		return response; 
	}

	public String deleteUser (String user) throws SQLException { 

		statement = connect.createStatement();
		statement.executeUpdate("delete from USER where ID = " + "'" + user + "'");

		String response = "User " + user + " has been deleted";
		return response; 
	}

	public String mEquip (int COD_M, String w_name) throws SQLException { 
		String user = findOwner(COD_M);
		if (checkOwner(user, w_name) == false){
			return "Error: item " + w_name + " not found in " + user + " inventory";
		}
		
		if (nAvailable(user, w_name) < 1){
			return "Error: Not enought " + w_name + " available";
		}
		
		statement = connect.createStatement();	
		statement.executeUpdate("insert into EQUIPPED (W_NAME, COD_M) values " +
				"( " + "'" + w_name + "'" + ", " + COD_M + " )");
		
		int nEquipped = nEquipped(user, w_name) -1;
		statement.executeUpdate("UPDATE WEARABLE_OWNED " +
				"SET W_EQUIPPED = " + nEquipped + " " +
				"where W_NAME = '" + w_name + "' " +
				"and ID_OWNER = '" + user + "'");

		String response = "Item " + w_name + " has been equipped from monster " + COD_M;
		return response; 		
	}

	public String mUnequip (int COD_M, String w_name) throws SQLException { 
		String user = findOwner(COD_M);
		if (checkOwner(user, w_name) == false){
			return "Error: item " + w_name + " not found in " + user + " inventory";
		}
		
		statement.executeUpdate("delete from EQUIPPED " +
				"where COD_M = " + COD_M + " " +
				"and W_NAME = '" + w_name  + "'");
		
		int nEquipped = nEquipped(user, w_name) -1;
		statement.executeUpdate("UPDATE WEARABLE_OWNED " +
				"SET W_EQUIPPED = " + nEquipped + " " +
				"where W_NAME = '" + w_name + "' " +
				"and ID_OWNER = '" + user + "'");
		
		String response = "Item " + w_name + " has been unequipped from monster " + COD_M;
		return response; 
	}

	public String mInfo (String denomination) throws SQLException {
		statement = connect.createStatement();
		resultSet = statement.executeQuery(
				"select * " +
				"from MONSTER " +
				"where  DENOMINATION = " + "'" + denomination + "'");

		String response;
		response = resultset_to_json (resultSet);
		return response;
	}

	public String mInfo (int COD_M)	throws SQLException {
		statement = connect.createStatement();
		resultSet = statement.executeQuery(
				"select * " +
				"from MONSTER_OWNED " +
				"where COD_M = " + COD_M );

		String response;
		response = resultset_to_json (resultSet);
		return response;
	}

	public String mFighting (String user) throws SQLException { 

		if (checkUser(user) == false) {
			return "{Error : User not found}";
		}

		statement = connect.createStatement();
		resultSet = statement.executeQuery(
				"select * " +
				"from MONSTER_FIGHTING mf, MONSTER_OWNED mo " +
				"where  mo.ID_OWNER = " + "'" + user + "'" + " " +
				"and mo.COD_M = mf.COD_M ");

		String response;
		response = resultset_to_json (resultSet);	

		return response; 
	}

	public String mAbility (int COD_M) throws SQLException { 

		statement = connect.createStatement();
		resultSet = statement.executeQuery(
				"select * " +
				"from ABILITY a, MONSTER_ABILITY ma " +
				"where  ma.COD_M = " + COD_M + " " +
				"and ma.A_NAME = a.A_NAME ");

		String response;
		response = resultset_to_json (resultSet);

		return response; 
	}

	public String mEquipped (int COD_M) throws SQLException { 

		statement = connect.createStatement();
		resultSet = statement.executeQuery(
				"select * " +
				"from EQUIPPED e, WEARABLE w " +
				"where  e.COD_M = " + COD_M + " " +
				"and e.W_NAME = w.W_NAME ");

		String response;
		response = resultset_to_json (resultSet);

		return response; 
	}
	
	public String mCollection (String user) throws SQLException { 
		statement = connect.createStatement();
		resultSet = statement.executeQuery(
	 		"select * " +
			"from MONSTER_OWNED mo " +
			"where  mo.ID_OWNER = '" + user + "'");
	
		String response;
		response = resultset_to_json (resultSet);

  		return response; 
  	}

} 
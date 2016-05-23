package restlet.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SQLAccess {
	protected Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public void connection() throws Exception {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost:3306/DBTESI?characterEncoding=UTF-8&useSSL=false",
							"root", "moonlight3");

		} catch (Exception e) {
			throw e;
		}/* finally {
      close();
    }*/

	}

	private String resultset_to_json(ResultSet rs) throws SQLException {
		JSONArray jarr = new JSONArray();
		while (rs.next()){
			HashMap<String, String> row = new HashMap<String, String>();
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) 
				row.put(rs.getMetaData().getColumnName(i), String.valueOf(rs.getObject(i)));
			jarr.add(new JSONObject(row));
		}
		return jarr.toString();
	}

	public String allUser ()  throws SQLException {
		JSONObject obj=new JSONObject();
		// Statements allow to issue SQL queries to the database
		statement = connect.createStatement();
		// Result set get the result of the SQL query
		resultSet = statement.executeQuery("select * from USER");
		String response="";

		return resultset_to_json(resultSet);

		/* while (resultSet.next()) {
          // It is possible to get the columns via name
          // also possible to get the columns via the column number
          // which starts at 1
          // e.g. resultSet.getSTring(2);
    	  obj.put("user", resultSet.getString("ID"));
    	  obj.put("pw", resultSet.getString("PW"));
    	  obj.put("lvl", resultSet.getString("LVL"));
      /*    Date date = resultSet.getDate("datum");  
         response += obj.toJSONString();
       }
     // return response;
   /*   preparedStatement = connect.prepareStatement("insert into USER (ID, PW) values ('Leti', 'M')");
      preparedStatement.executeUpdate();
      writeResultSet(resultSet);

      preparedStatement = connect
              .prepareStatement("insert into USER (ID, PW, LVL) values (?, ?, ?)");
          // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
          // Parameters start with 1
          preparedStatement.setString(1, "Biagio");
          preparedStatement.setString(2, "k");
          preparedStatement.setString(3, "10");
          preparedStatement.executeUpdate();

      resultSet = statement
      .executeQuery("select * from feedback.comments");
      writeMetaData(resultSet); */

	} 

	public boolean checkUser (String user)  throws SQLException {
		statement = connect.createStatement();
		resultSet = statement.executeQuery("select * from USER where ID = " + "'" + user + "'");

		if (!resultSet.next())
			return false; 	//user doesn't exist in DB

		return true;		//user exist in DB 
	} 

	public String insertUser (String user, String pw)  throws SQLException {
		// Statements allow to issue SQL queries to the database
		statement = connect.createStatement();

		preparedStatement = connect
				.prepareStatement("insert into USER (ID, PW, LVL, MANA) values (?, ?, 1, 10)");
		// Parameters start with 1
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



	private void writeMetaData(ResultSet resultSet) throws SQLException {
		//   Now get some metadata from the database
		// Result set get the result of the SQL query

		System.out.println("The columns in the table are: ");

		System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
		for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
			System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
		}
	}

	private void writeResultSet(ResultSet resultSet) throws SQLException {
		// ResultSet is initially before the first data set
		while (resultSet.next()) {
			// It is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g. resultSet.getSTring(2);
			String user = resultSet.getString("ID");
			/*    Date date = resultSet.getDate("datum");  */
			System.out.println("User: " + user);
		}
	}

	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

} 
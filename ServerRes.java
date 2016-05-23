package restlet.DB;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.JSONArray;
import java.util.List;

import restlet.DB.*;

import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ServerRes extends ServerResource {  

	@Get  
	public String handleConnection() throws SQLException, ParseException, JSONException {  
		String response = "";
		
		//System.out.println(getClientInfo());
		
		SQLAccess db = new SQLAccess();			//Apro connessione a DB
			try {
				db.connection();
			} catch (Exception e) {
				System.out.println("Errore connessione DB");
			}
			
		List Segm = getReference().getSegments();	
		JSONArray jarr;
		JSONObject jobj;
		JSONParser parser = new JSONParser();
		
		if (Segm.get(0).equals("User")) {
			response = db.allUser();
			 
			jarr = new JSONArray(response);
			
			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("User: " + jobj.get("ID"));
			}
			
			return response;
		}
		
		if (Segm.get(0).equals("Fillo")) {
			response = "Fillo prova \n";
			return response;
		} 
		
		if (Segm.get(0).equals("Register")){
			String user = (String) Segm.get(1);
			String pw = (String) Segm.get(2);
			if (db.checkUser(user) == true){
				return "Name already taken \n";			
			}
			if (pw.length() < 3){
				return "Inserire una password con almeno 3 caratteri";				
			}
			return db.insertUser(user, pw);
		}
		
		if (Segm.get(0).equals("Login")){
			String user = (String) Segm.get(1);
			String pw = (String) Segm.get(2);
			return db.loginUser(user, pw);			
		}
		
		if (Segm.get(0).equals("mInfo")){
			
			String denomination = (String) Segm.get(1);			
			response = db.mInfo(denomination);
			
			jarr = new JSONArray(response);
			
			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("Monster: " + jobj.get("DENOMINATION"));
			}
			return response;
		}
		
		if (Segm.get(0).equals("moInfo")){
			
			int COD_M = Integer.parseInt((String) Segm.get(1));			
			response = db.mInfo(COD_M);
			
			jarr = new JSONArray(response);
			
			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("Monster: " + jobj.get("NAME"));
			}
			return response;
		}
		
		if (Segm.get(0).equals("mFighting")){
			String user = (String) Segm.get(1);
			response = db.mFighting(user);
			
			jarr = new JSONArray(response);
			
			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("Monster: " + jobj.get("NAME"));
			}
			return response;
		}
		
		
		if (Segm.get(0).equals("mAbility")){
			int COD_M = Integer.parseInt((String) Segm.get(1));
			response = db.mAbility(COD_M);
			
			jarr = new JSONArray(response);
			
			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("Ability: " + jobj.get("A_NAME"));
			}
			return response;
		}
		
		if (Segm.get(0).equals("mEquipped")){
			int COD_M = Integer.parseInt((String) Segm.get(1));
			response = db.mEquipped(COD_M);
			
			jarr = new JSONArray(response);
			
			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("Equip: " + jobj.get("W_NAME"));
			}
			return response;
		}
		
		response = "Operazioni possibili: \n";
		response += "Utenti esistenti: http://localhost:8080/User \n";
		response += "Login: http://localhost:8080/Login/ID/PW \n";
		response += "Register: http://localhost:8080/Register/ID/PW \n";
		
		return response;
	}


	public static void main(String[] args) throws Exception {  
		new Server(Protocol.HTTP, 8080, ServerRes.class).start();  
	}

}  
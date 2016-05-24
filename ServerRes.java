package db.restlet;

import db.restlet.*;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import db.restlet.SQLAccess;
import db.restlet.ServerRes;


public class ServerRes extends ServerResource {  

	public String handleConnection() throws SQLException, JSONException {  
		String response = "";
		//System.out.println(getClientInfo());

		SQLAccess db = new SQLAccess();			//Apro connessione a DB
		try {
			db.connection();
		} catch (Exception e) {
			System.out.println("Error: DB connection");
		}

		List Segm = getReference().getSegments();	
		JSONArray jarr;
		JSONObject jobj;
		
		if (Segm.get(0).equals("User")) {		// http://localhost:8080/User (Visualizza tutti gli utenti registrati)
			response = db.allUser();

			jarr = new JSONArray(response);

			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("User: " + jobj.get("ID"));
			}

			return response;
		}

		if (Segm.get(0).equals("Register")){		// http://localhost:8080/Register/ID/PW (Registra l'utente con ID e PW)
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

		if (Segm.get(0).equals("Login")){			// http://localhost:8080/Login/ID/PW
			String user = (String) Segm.get(1);
			String pw = (String) Segm.get(2);
			return db.loginUser(user, pw);			
		}

		if (Segm.get(0).equals("deleteUser")){		// http://localhost:8080/deleteUser/ID
			String user = (String) Segm.get(1);
			return db.deleteUser(user);
		}

		if (Segm.get(0).equals("eqip")){		// http://localhost:8080/insertEquipped/W_NAME/COD_M
			String W_NAME = ((String) Segm.get(1)).replace("%20", " ");
			int COD_M = Integer.parseInt((String) Segm.get(2));

			return db.equip(COD_M, W_NAME);
		}

		if (Segm.get(0).equals("uneuip")){		// http://localhost:8080/unequip/W_NAME/COD_M
			String w_name = ((String) Segm.get(1)).replace("%20", " ");
			int COD_M = Integer.parseInt((String) Segm.get(2));

			return db.unequip(COD_M, w_name);
		}

		if (Segm.get(0).equals("mInfo")){		// http://localhost:8080/mInfo/denomination

			String denomination = (String) Segm.get(1);			
			response = db.mInfo(denomination);

			jarr = new JSONArray(response);

			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("Monster: " + jobj.get("DENOMINATION"));
			}
			return response;
		}

		if (Segm.get(0).equals("moInfo")){		// http://localhost:8080/moInfo/COD_M

			int COD_M = Integer.parseInt((String) Segm.get(1));			
			response = db.mInfo(COD_M);

			jarr = new JSONArray(response);

			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("Monster: " + jobj.get("NAME"));
			}
			return response;
		}

		if (Segm.get(0).equals("mFighting")){		// http://localhost:8080/mFighting/ID
			String user = (String) Segm.get(1);
			response = db.mFighting(user);

			jarr = new JSONArray(response);

			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("Monster: " + jobj.get("NAME"));
			}
			return response;
		}


		if (Segm.get(0).equals("mAbility")){		// http://localhost:8080/mAbility/COD_M
			int COD_M = Integer.parseInt((String) Segm.get(1));
			response = db.mAbility(COD_M);

			jarr = new JSONArray(response);

			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("Ability: " + jobj.get("A_NAME"));
			}
			return response;
		}

		if (Segm.get(0).equals("mEquipped")){		// http://localhost:8080/mEquipped/COD_M
			int COD_M = Integer.parseInt((String) Segm.get(1));
			response = db.mEquipped(COD_M);

			jarr = new JSONArray(response);

			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("Equip: " + jobj.get("W_NAME"));
			}
			return response;
		}
		
		if (Segm.get(0).equals("mCollection")){		// http://localhost:8080/mCollection/ID
			String user = (String) Segm.get(1);
			response = db.mCollection(user);

			jarr = new JSONArray(response);

			for (int i=0; i<jarr.length(); i++){
				jobj = jarr.getJSONObject(i);
				System.out.println("Monster: " + jobj.get("NAME"));
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
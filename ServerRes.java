package db.restlet;

import db.restlet.*;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import db.restlet.SQLAccess;
import db.restlet.ServerRes;


public class ServerRes extends ServerResource {  
	SQLAccess db = new SQLAccess();			//Apro connessione a DB	
	
	@Get  
	public String handleConnection() throws SQLException, JSONException {  
		String response = "";
		//System.out.println(getClientInfo());

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
			
			if (pw.length() < 3){
				return "{\"Error\": \"Inserire una password con almeno 3 caratteri\"}";				
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

		if (Segm.get(0).equals("mEquip")){		// http://localhost:8080/mEquip/W_NAME/COD_M
			String W_NAME = ((String) Segm.get(1)).replace("%20", " ");
			int COD_M = Integer.parseInt((String) Segm.get(2));

			return db.mEquip(COD_M, W_NAME);
		}

		if (Segm.get(0).equals("mUnequip")){		// http://localhost:8080/mUnequip/W_NAME/COD_M
			String w_name = ((String) Segm.get(1)).replace("%20", " ");
			int COD_M = Integer.parseInt((String) Segm.get(2));

			return db.mUnequip(COD_M, w_name);
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
		
		if (Segm.get(0).equals("nAvailable")){		// http://localhost:8080/nAvailable/ID/W_NAME
			String user = ((String) Segm.get(1)).replace("%20", " ");
			String w_name = ((String) Segm.get(2)).replace("%20", " ");
			int disp = db.nAvailable(user, w_name);

			return "You have " +  disp + " object unequipped";
		}
		
		if (Segm.get(0).equals("sCollection")){		// http://localhost:8080/nAvailable/ID/W_NAME
			String user = ((String) Segm.get(1)).replace("%20", " ");
			return db.sCollection(user);
		}
		
		if (Segm.get(0).equals("wCollection")){		// http://localhost:8080/nAvailable/ID/W_NAME
			String user = ((String) Segm.get(1)).replace("%20", " ");
			return db.wCollection(user);
		}
		
		if (Segm.get(0).equals("wCollectionType")){		// http://localhost:8080/wCollectionType/ID/W_TYPE
			String user = ((String) Segm.get(1)).replace("%20", " ");
			String type = ((String) Segm.get(2)).replace("%20", " ");
			return db.wCollectionType(user, type);
		}
		
		if (Segm.get(0).equals("matchMaking")){			// http://localhost:8080/matchMaking
			return db.matchMaking();
		}
		
		if (Segm.get(0).equals("joinMatchMaking")){		// http://localhost:8080/joinMatchMaking/ID
			String user = ((String) Segm.get(1)).replace("%20", " ");
			return db.joinMatchMaking(user);
		}
		
		if (Segm.get(0).equals("exitMatchMaking")){		// http://localhost:8080/exitMatchMaking/ID
			String user = ((String) Segm.get(1)).replace("%20", " ");
			return db.exitMatchMaking(user);
		}

		if (Segm.get(0).equals("learnAbility")){		// http://localhost:8080/learnAbility/COD_M/A_NAME
			int COD_M = Integer.parseInt((String) Segm.get(1));
			String a_name = ((String) Segm.get(2)).replace("%20", " ");
			return db.learnAbility(a_name, COD_M);
		}
		if (Segm.get(0).equals("mAddTeam")){			// http://localhost:8080/mAddTeam/COD_M
			int COD_M = Integer.parseInt((String) Segm.get(1));
			return db.mAddTeam(COD_M);
		}
		
		if (Segm.get(0).equals("mRemoveTeam")){			// http://localhost:8080/mRemoveTeam/COD_M
			int COD_M = Integer.parseInt((String) Segm.get(1));
			return db.mRemoveTeam(COD_M);
		}
		
		if (Segm.get(0).equals("lvlUp")){			// http://localhost:8080/lvlUp/COD_M
			int COD_M = Integer.parseInt((String) Segm.get(1));
			if (db.checkExp(COD_M) == false)
				return "Not enought exp for lvlup";
			return db.lvlUp(COD_M);
		}
		
		if (Segm.get(0).equals("lvlAvg")){		// http://localhost:8080/lvlAvg/ID
			String user = ((String) Segm.get(1)).replace("%20", " ");	
			return Integer.toString(db.lvlAvg(user));
		}
		
		if(Segm.get(0).equals("aAttack")){											//localhost:8080/attack/COD_M/COD_MA
			int COD_M = Integer.parseInt((String) Segm.get(1));
			int COD_MA = Integer.parseInt((String) Segm.get(2));
			response = db.aAttack(COD_M, COD_MA);
			return response;
		}
		
		if(Segm.get(0).equals("aMove")){												//localhost:8080/move/COD_M/pos
			int COD_M = Integer.parseInt((String) Segm.get(1));
			int pos = Integer.parseInt((String) Segm.get(2));
			response = db.aMove(COD_M, pos);
			return response;
		}
		
		if(Segm.get(0).equals("ability")){												//localhost:8080/ability/COD_M/COD_MA/a_name 
			int COD_M = Integer.parseInt((String) Segm.get(1));		
			int COD_MA = Integer.parseInt((String) Segm.get(2));
			String ability = ((String) Segm.get(3)).replace("%20", " ");
			response =  db.aAbility(COD_M, COD_MA, ability);
			return response;
		}
		
		if (Segm.get(0).equals("searchMatch")){		// http://localhost:8080/searchMatch/ID
			String user = ((String) Segm.get(1)).replace("%20", " ");
			if (db.searchMatch(user) == true){
				db.exitMatchMaking(user);	//user deleted from the queue of matchmaking
				return "Match found! Your foe is: " + db.findFoe(user);
			}
			return "Still searching for a foe";
		}
		
		if(Segm.get(0).equals("createGame")){	// http://localhost:8080/createGame/ID1/ID2
			String id1 = ((String) Segm.get(1)).replace("%20", " ");
			String id2 = ((String) Segm.get(2)).replace("%20", " ");
			return db.createGame(id1, id2);
		}
				
		if(Segm.get(0).equals("clearGames")){	// http://localhost:8080/clearGames
			return db.clearGames();
		}
		
		if(Segm.get(0).equals("clearqueue")){	// http://localhost:8080/clearGames
			return db.clearQueue();
		}
				
		if(Segm.get(0).equals("startMatching")){	// http://localhost:8080/startMatching
			matching();
			return "end matching";
		}
		
		response = "Operazioni possibili: \n";
		response += "Utenti esistenti: http://localhost:8080/User \n";
		response += "Login: http://localhost:8080/Login/ID/PW \n";
		response += "Register: http://localhost:8080/Register/ID/PW \n";

		return response;
	}
	
	private int loadQueue (List <User> queue) throws SQLException, JSONException{
		String jusers = db.matchMaking();
		JSONArray jarr;
		JSONObject jobj;
		jarr = new JSONArray(jusers);
		int i, j;
		
		for (i=0; i<jarr.length(); i++){
			jobj = jarr.getJSONObject(i);
			queue.add(new User (jobj.getString("ID"), jobj.getInt("LVL")));
		}
		return i;
	}
	
	public void matching () throws SQLException, JSONException{
		List <User> queue = new ArrayList <User> ();
		int i, j;
		
		for (i=0; loadQueue(queue) >= 2 && i<queue.size(); ){			
			for (j=0; j<queue.size(); j++){
				if (i==j) j++; 
				System.out.println("ID1 lvv: " +  queue.get(i).lvl + " ID2 lvv: " +  queue.get(j).lvl);
				if (queue.get(i).lvl-3 <= queue.get(j).lvl && queue.get(i).lvl+3 >= queue.get(j).lvl){
					db.createGame(queue.get(i).id, queue.get(j).id);

					db.exitMatchMaking(queue.get(i).id);
					db.exitMatchMaking(queue.get(j).id);
					queue.clear();
					break;
				}
			}
			if (j == queue.size())
				i++;
		}
		System.out.println("Not enought player in queue");
	}
	
	public static void main(String[] args) throws Exception {  
		new Server(Protocol.HTTP, 8080, ServerRes.class).start();  
	}

}  

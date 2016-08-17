package db.restlet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MatchManager {
private SQLAccess db = new SQLAccess();
	
private JSONArray jarr;
private JSONObject jobj;

	public class Tempored implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0)  {
			try {
				jarr = new JSONArray(db.Games());
				
				for(int i=0; i<jarr.length(); i++){
					jobj = jarr.getJSONObject(i);
					
					if(jobj.getString("STATUS").equalsIgnoreCase("in_progress") && (Integer.parseInt(db.loadingsec(jobj.getString("ID1")))*(-1)>=40)){	//se la partita non è terminata faccio gli opportuni controlli
						if(db.allDead(jobj.getString("ID1"))){
							db.setGameStatus(jobj.getString("ID1"), "ended");
							db.setGameWinner(jobj.getString("ID2"));
						}
						if(db.allDead(jobj.getString("ID2"))){
							db.setGameStatus(jobj.getString("ID2"), "ended");
							db.setGameWinner(jobj.getString("ID1"));
						}
					}
				}
				
			} catch (SQLException | JSONException e) {
				System.out.println("MatchManager error");				
				e.printStackTrace();
			}
		}
	}
	
	
	public class CleanerTempored implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0)  {
			try {
					db.clearGames();
			} catch (SQLException | JSONException e) {
				System.out.println("Cleaner error");				
				e.printStackTrace();
			}
		}
	}
	
	public MatchManager (){
		super();
		
		try {
			db.connection();
		} catch (Exception e) {
			System.out.println("Error: DB connection");
		}
		
		Tempored matchManager = new Tempored();
		CleanerTempored cleaner = new CleanerTempored();
		Timer m = new Timer (5000, matchManager);		//intervallo di tempo che temporizza il matching
		Timer c = new Timer (30000, cleaner);		//intervallo di tempo che temporizza il matching
		m.start();	
		c.start();
	}
}

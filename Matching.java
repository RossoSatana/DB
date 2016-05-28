package db.restlet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.ServerResource;

public class Matching {
	SQLAccess db = new SQLAccess();
	
	public class Tempored implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0)  {
			try {
				matching();
			} catch (SQLException | JSONException e) {
				System.out.println("Matching connection failed");				
				e.printStackTrace();
			}
		}
	}
	
	public Matching() {
		super();
		
		try {
			db.connection();
		} catch (Exception e) {
			System.out.println("Error: DB connection");
		}
		
		Tempored listener = new Tempored();
		Timer t = new Timer (5000, listener);		//intervallo di tempo che temporizza il matching
		t.start();
	}

	private int loadQueue (List <User> queue) throws SQLException, JSONException{
		String jusers = db.matchMaking();
		JSONArray jarr;
		JSONObject jobj;
		jarr = new JSONArray(jusers);
		int i;
		
		for (i=0; i<jarr.length(); i++){
			jobj = jarr.getJSONObject(i);
			queue.add(new User (jobj.getString("ID"), jobj.getInt("LVL")));
		}
		return i;
	}
	
	public void matching () throws SQLException, JSONException{
		List <User> queue = new ArrayList <User> ();
		int i, j;
		int dq = loadQueue(queue);	//dimensione queue
		
		for (i=0; dq >= 2 && i<queue.size(); ){			
			for (j=i+1; j<queue.size(); j++){
				System.out.println("ID1 lvl: " +  queue.get(i).lvl + " ID2 lvl: " +  queue.get(j).lvl);
				if (queue.get(i).lvl-3 <= queue.get(j).lvl && queue.get(i).lvl+3 >= queue.get(j).lvl){
					db.createGame(queue.get(i).id, queue.get(j).id);

					db.exitMatchMaking(queue.get(i).id);
					db.exitMatchMaking(queue.get(j).id);
					queue.clear();
					dq = loadQueue(queue);
					break;
				}
			}
				i++;
		}
		System.out.println("Not enought player in queue");
	}	
	
}

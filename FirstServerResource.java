import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class FirstServerResource extends ServerResource {  

	@Get  
	public String handleConnection() {  
		String response = null;
		
		System.out.println(getClientInfo());	
		
		if (getReference().getLastSegment().equals("Leti")) {
			response = "Fillo doppio obeso";
		}  
		else 	if (getReference().getLastSegment().equals("Fillo")) {
			response = "Fillo obeso";
		}  
		return response;
	}


	public static void main(String[] args) throws Exception {  
		new Server(Protocol.HTTP, 8080, FirstServerResource.class).start();  
	}

}  
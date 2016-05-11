import java.io.IOException;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

//  Nella macchina che funge da server, scrivi su terminale:
//	# iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080

public class FirstClientResource {
	public static void main(String[] args) throws ResourceException, IOException {
		// Create the client resource  
		ClientResource resource = new ClientResource("http://82.58.143.205:8080/Leti");
		ClientResource resource2 = new ClientResource("http://82.58.143.205:8080/Fillo");

		// Write the response entity on the console
		resource.get().write(System.out); 
		resource2.get().write(System.out); 
		
	}
}
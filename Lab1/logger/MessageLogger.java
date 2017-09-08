package logger;

import java.io.FileNotFoundException;
import java.util.*;

import messagePasser.Configuration;
import messagePasser.TimeStampedMessage;
import messagePasser.Node;
import messagePasser.Rule;

public class MessageLogger {
	private String serverName = "Logger";
	private int serverPort = -1;
	Configuration config;
	
	List<TimeStampedMessage> inbox = Collections.synchronizedList(new ArrayList<TimeStampedMessage>());
	List<TimeStampedMessage> inboxDelay = Collections.synchronizedList(new ArrayList<TimeStampedMessage>());
 
    public MessageLogger(String configuration_filename) throws FileNotFoundException{
        // Read in configuration file and stores into some data structure
        this.config = new Configuration(configuration_filename);
        
        // Creates one other thread to handle incoming connection setup
        for(Node node: this.config.getNodes()){ 
            if(node.getName().equals(this.serverName)){
                this.serverPort = node.getPort();
                break;
            }
        }
        
        if(serverPort == -1){
        	System.out.println("Error: Unable to find self in configuration.");
        	System.exit(1);
        }

        LoggerServerThread listener = new LoggerServerThread(this.serverPort, this);
        new Thread(listener).start();
        System.out.println("Running on port: "+ this.serverPort); 
    }
	
	// Function to store message in correct order
	public void store(TimeStampedMessage incoming){
		// Check against rules
		for(Rule r : config.getRecvRules()){
			if(r.match(incoming)){
				switch(r.getAction()){
				case "drop":
					return;
				case "dropAfter":
					return;
				case "delay":
					inboxDelay.add(incoming);
					return;
				case "duplicate":
					inbox.add(incoming);
					inbox.add(new TimeStampedMessage(incoming));
					return;
				}
				break; // Only first rule applies
			}
		}
		int index = inbox.size();
		for(int x = inbox.size()-1; x >= 0; x--){
			index = x;
			if(incoming.get_timestamp().isNewer(inbox.get(x).get_timestamp())==1){
				index = x+1;
				break;
			}
			// concurrent case
			if(incoming.get_timestamp().isNewer(inbox.get(x).get_timestamp())==-1){
				if(incoming.get_source().compareTo(inbox.get(x).get_source() ) > 0){
					index = x;
					break;
				}	
			}
		}
		//System.out.println("Log: Added file");
		//System.out.println("Putting in index: "+ index);
		inbox.add(index, incoming);
		
		while(inboxDelay.size() > 0){
			incoming = inboxDelay.remove(0);
			for(int x = inbox.size()-1; x >= 0; x--){
				index = x;
				if(incoming.get_timestamp().isNewer(inbox.get(x).get_timestamp())==1){
					index = x+1;
					break;
				}
				// concurrent case
				if(incoming.get_timestamp().isNewer(inbox.get(x).get_timestamp())==-1){
					if(incoming.get_source().compareTo(inbox.get(x).get_source() ) > 0){
						index = x;
						break;
					}
					
				}
			}
			inbox.add(index, incoming);
			//System.out.println("Log: Added delayed file");
			//System.out.println("Putting delayed in index: "+ index);
		}
		
	}
	
	// Fetch logged messages
	public TimeStampedMessage fetch(int backCount){
		if(backCount < inbox.size()){
			return inbox.get((inbox.size()-1)-backCount);
		}
		return null;
	}
}

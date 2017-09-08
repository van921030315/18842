package messagePasser;

import clockService.*;
import multicast.Multicast;

import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class MessagePasser {
	// ClockService config
	ClockServiceFactory factory;
	ClockService clock;
	String clockType = "vector"; // Type of clock service
	int clockNum = 0; // Number of nodes for vector clock
	boolean loggingSend = false;
	boolean loggingReceive = false;
	
	private String serverName;
	private int serverPort = 18842;
	//private int sequenceNumber;
	private HashMap<String, Integer> sequenceNumberMap = new HashMap<String, Integer>();
	
	// Hashmap for sockets
	private static HashMap<String, ObjectOutputStream> addressBook = new HashMap<String, ObjectOutputStream>();
	public HashMap<String, ArrayList<String>> groupBook = new HashMap<String, ArrayList<String>>();
	
	// Configuration
	Configuration config;
	
	// ArrayList to store incoming and outgoing messages
	private ArrayList<TimeStampedMessage> outboxDelay = new ArrayList<TimeStampedMessage>();
	List<TimeStampedMessage> inbox = Collections.synchronizedList(new ArrayList<TimeStampedMessage>());
	List<TimeStampedMessage> inboxDelay = Collections.synchronizedList(new ArrayList<TimeStampedMessage>());
	List<TimeStampedMessage> multicastInbox = Collections.synchronizedList(new ArrayList<TimeStampedMessage>());
	List<TimeStampedMessage> multicastInboxDelay = Collections.synchronizedList(new ArrayList<TimeStampedMessage>());
	
	// Extra array to hold all multicast messages returned
	ArrayList<TimeStampedMessage> doneMulti = new ArrayList<TimeStampedMessage>();
	// Multicast Service
	Multicast mc = null;
	
	public MessagePasser(String configuration_filename, String local_name, String clockType) throws FileNotFoundException{
        // Read in configuration file and stores into some data structure
        this.config = new Configuration(configuration_filename);
        this.serverName = local_name;
       
        System.out.println("my name is: "+this.serverName);
        
        
        // Create ClockService
        factory = new ClockServiceFactory();
        clock = factory.getClockService(clockType,  clockNum, local_name, this.config.getNodes());
        
        if(!this.config.getGroups().isEmpty()){
        	this.mc = new Multicast(this.generateGroups(), local_name, this);
        }
        // Creates one other thread to handle incoming connection setup
        for(Node node: this.config.getNodes()){
        	//System.out.println("config name "+ node.getName()); 
            if(node.getName().equals(this.serverName)){
                this.serverPort = node.getPort();
                System.out.println("Running on port: "+ this.serverPort); 
            }
        }
        
        ServerThread listener = new ServerThread(this.serverPort, this);
        new Thread(listener).start();
        // Sets up configuration (all socket connections to other nodes should be done)
        for(Node node: this.config.getNodes()){
            if(!node.getName().equals(this.serverName)){
                // Start a new thread as a client
                SendingThread sender = new SendingThread(node, local_name, MessagePasser.addressBook);
                new Thread(sender).start();
                
            }
        }
    }
	
    // Send rule processing here
	public void send(Message message){
		// Drop if invalid address
		if(!addressBook.containsKey(message.get_destination())){
			return;
		}
		// Set source, sequence number, 
		message.set_source(this.serverName);
		if(sequenceNumberMap.containsKey(message.get_destination())){
			message.set_seqNum(sequenceNumberMap.get(message.get_destination()));
			sequenceNumberMap.put(message.get_destination(), new Integer(sequenceNumberMap.get(message.get_destination()).intValue() +1));
		}
		else{
			sequenceNumberMap.put(message.get_destination(), new Integer(1));
			message.set_seqNum(0);
		}
		// TODO: check if it is a multicast message, only update timestamp for regular message
		//TimeStampedMessage msg = (TimeStampedMessage) message;
		TimeStampedMessage tsm = new TimeStampedMessage(message, clock.getTimeStamp());
		/*
		if(msg.isMulticast()){
			tsm = msg;
		}else{
			tsm = new TimeStampedMessage(message, clock.getTimeStamp());
		}*/
		// Write to logger if logging messages
		if(loggingSend){ 
			try {
				addressBook.get("Logger").writeObject(tsm);
			} catch (IOException e) {
				System.out.println("MessagePasser: Failed to log message to logger.");
				e.printStackTrace();
			}
		}
				
		// Check against rules
		for(Rule r : config.getSendRules()){
			if(r.match(message)){
				switch(r.getAction()){
				case "drop":
					return;
				case "dropAfter":
					return;
				case "delay":
					outboxDelay.add(tsm);
					return;
				case "duplicate":
					outboxDelay.add(new TimeStampedMessage(tsm));
					break;
				}
				break; // Only first rule applies
			}
		}
		
		try {
			// Send out message if not dropped or delayed
			addressBook.get(tsm.get_destination()).reset();
			addressBook.get(tsm.get_destination()).writeObject(tsm);
			//tsm.displayMessage();
			while(!outboxDelay.isEmpty()){ // Send out any delayed messages
				TimeStampedMessage m = outboxDelay.remove(0);
				addressBook.get(m.get_destination()).writeObject(m);
			}
		} catch (IOException e) {
			System.out.println("Error: failure to send message.");
			e.printStackTrace();
		}
	}
	
	// Function to store received message from ReceivingThread
	public void store(TimeStampedMessage incoming){
		// Check if multicast message
		if(incoming.isMulticast() || incoming.get_kind().equals("multicast")){
			// Check against rules
			for(Rule r : config.getRecvRules()){
				if(r.match(incoming)){
					switch(r.getAction()){
					case "drop":
						return;
					case "dropAfter":
						return;
					case "delay":
						multicastInboxDelay.add(incoming);
						return;
					case "duplicate":
						multicastInbox.add(incoming);
						multicastInbox.add(new TimeStampedMessage(incoming));
						return;
					}
					break; // Only first rule applies
				}
			}
			
			// Update clock service's time
			TimeStamp newTS = incoming.get_timestamp();
			clock.updateClockTime(newTS);
			//incoming.set_timestamp(newTS); // Don't update timestamp in message
			
			// Write to logger if logging messages
			if(loggingReceive){ 
				try {
					addressBook.get("Logger").writeObject(incoming);
				} catch (IOException e) {
					System.out.println("MessagePasser: Failed to log message to logger.");
					e.printStackTrace();
				}
			}
			
			multicastInbox.add(incoming);
			while(!multicastInboxDelay.isEmpty()){
				TimeStampedMessage m = multicastInboxDelay.remove(0);
				multicastInbox.add(m);
			}
			
		}
		else{
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
			// Update clock service's time
			TimeStamp newTS = incoming.get_timestamp();
			clock.updateClockTime(newTS);
			incoming.set_timestamp(newTS);
			
			// Write to logger if logging messages
			if(loggingReceive){ 
				try {
					addressBook.get("Logger").writeObject(incoming);
				} catch (IOException e) {
					System.out.println("MessagePasser: Failed to log message to logger.");
					e.printStackTrace();
				}
			}
			
			inbox.add(incoming);
			while(!inboxDelay.isEmpty()){
				TimeStampedMessage m = inboxDelay.remove(0);
				inbox.add(m);
			}
		}
		
	}
	
	// Receive rule processing here
	public TimeStampedMessage receive(){
		int loops = 0;
		while(true){
			if(inbox.isEmpty()){
				if(loops == 20){
					return null;
				}
				loops++;
				try {
					Thread.sleep(100); // 10ms
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else{
				break;
			}
		}
		return inbox.remove(0);
	}
	
	public ArrayList<TimeStampedMessage> fetchMulticast(){
		int loops = 0;
		while(true){
			if(multicastInbox.isEmpty()){
				if(loops == 20){
					return null;
				}
				loops++;
				try {
					Thread.sleep(100); // 10ms
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else{
				break;
			}
		}
		
		return new ArrayList<TimeStampedMessage>(multicastInbox);
	}
	
	public void inserttoMulticastInbox(TimeStampedMessage msg){
		this.inbox.add(msg);
	}
	
	// Used to trigger "event" that changes timestamp
	public String event() {
		return clock.getTimeStamp().string();
	}
	
	public String time(){
		return clock.getTime().string();
	}

	public void logReceive() {
		loggingReceive = !loggingReceive;
	}
	
	public void logSend() {
		loggingSend = !loggingSend;
	}

	public void puttoInbox(TimeStampedMessage msg) {
		inbox.add(msg);
		int i = doneMulti.size()-1;
		
		while(true){
			if(msg.getMulticastTS().isNewer(doneMulti.get(i).getMulticastTS()) == 1 || msg.getMulticastTS().isNewer(doneMulti.get(i).getMulticastTS()) == -1){
				doneMulti.add(i+1, msg);
				break;
			}
			else if(i == 0){
				doneMulti.add(0, msg);
				break;
			}
			else{
				i--;
			}
		}
		
	}
	
	public ArrayList<TimeStampedMessage> getAllMulti(){
		return doneMulti;
	}
	
	public HashMap<String, ArrayList<Node> > generateGroups(){
		HashMap<String, ArrayList<Node> > groups = new HashMap<String, ArrayList<Node> >();
		for(Group g: this.config.getGroups()){
			ArrayList<Node> nodes = new ArrayList<Node>();
			for(String m: g.getMembers()){
				nodes.add(this.config.getNode(m));
			}
			groups.put(g.getGroupId(), nodes);
			
		}
		return groups;
	}
	
	public void multicast(TimeStampedMessage msg, String groupName){
		this.mc.multicast(groupName, msg);
	}
	
	public ClockService getClock(){
		return this.clock;
	}
}


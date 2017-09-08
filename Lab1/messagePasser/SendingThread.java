package messagePasser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class SendingThread implements Runnable{
	private Node node;
	private int port;
	private String name;
	private String ip;
	private HashMap<String, ObjectOutputStream> addressBook;
	private Socket sender;
	private ObjectOutputStream outstream;
	private ObjectInputStream instream;

	public SendingThread(Node node, String localname, HashMap<String, ObjectOutputStream> addressBook){
		this.port = node.getPort();
		this.name = node.getName();
		this.ip = node.getIp();
		this.node = node;
		this.addressBook = addressBook;
		if(this.addressBook == null){
			this.addressBook = new HashMap<String, ObjectOutputStream>();
		}
	}

	@Override
	public void run() {
		// try setting up connection with server every 2 seconds;
//		System.out.println("Sending Thread: Attempting to set up connection.");
		
		while(this.setupConnection() != 0){
			try {
				Thread.sleep(1000);
//				System.out.println("Sending Thread: Sleep");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
		
		System.out.println("\nSending Thread: Connected to "+this.name);
		
		// add the outputstream to the addressbook
		this.addressBook.put(this.name, this.outstream);
		
	}
	
	private int setupConnection(){
		// open a socket 
		try {
	           sender = new Socket(this.ip, this.port);
	    }
	    catch (IOException e) {
	        //System.out.println(e);
	        return -1;
	    }
		// create outputstream and inputstream
//		System.out.println("Sending Thread: Created sending socket.");
		try {
			this.outstream = new ObjectOutputStream(sender.getOutputStream()); 
			this.instream = new ObjectInputStream(sender.getInputStream());
		}
		catch (IOException e) {
			System.out.println("Failed to setup object streams.");
			return -1;
		}
//		System.out.println("Sending Thread: Returning form setupConnection().");
		return 0;
		
	}
	
	public Node getDestinationId(){
		return this.node;
	}

	public int checkConnection(Object obj){
		try {
			this.outstream.writeObject(obj);
		} catch (IOException e) {
			
			e.printStackTrace();
			System.out.println("Pipe broken"); 
			return -1;
		}
		try {
			System.out.println(this.instream.readObject().toString());
		} catch (ClassNotFoundException e) { // unsure if the 
			// inputstream should be check here
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;

	}
	
	// for debugging purpose
	public void resetOutputStream(){
		if(this.checkConnection(new Message(this.name, "ConnCheck", "")) != 0){
			try {
				this.outstream = new ObjectOutputStream(sender.getOutputStream()); 
				this.instream = new ObjectInputStream(sender.getInputStream());
			}
			catch (IOException e) {
			       System.out.println(e);
			       return;
			    }
			this.addressBook.put(this.name, this.outstream);
		}
	}
}

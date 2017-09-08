package messagePasser;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ReceivingThread implements Runnable{
	protected Socket clientSocket = null;
    protected MessagePasser self = null;
    
    public ReceivingThread(Socket client, MessagePasser self){
    	this.clientSocket = client;
    	this.self = self;
    }

	public void run() {
		try{
			@SuppressWarnings("unused") // Needed to setup connection
			ObjectOutputStream output = new ObjectOutputStream( this.clientSocket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream( this.clientSocket.getInputStream());
			while(true){
				TimeStampedMessage msg = (TimeStampedMessage) input.readObject();
				//TimeStampedMessage msg = new TimeStampedMessage((TimeStampedMessage) input.readObject());
				//msg.displayMessage();
				self.store(msg);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

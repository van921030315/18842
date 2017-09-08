package logger;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messagePasser.TimeStampedMessage;

public class LoggerReceivingThread implements Runnable{
	protected Socket clientSocket = null;
    protected MessageLogger self = null;
    
    public LoggerReceivingThread(Socket client, MessageLogger self){
    	this.clientSocket = client;
    	this.self = self;
    }

	public void run() {
		try{
			@SuppressWarnings("unused")
			ObjectOutputStream output = new ObjectOutputStream( this.clientSocket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream( this.clientSocket.getInputStream());
			while(true){
				TimeStampedMessage msg = (TimeStampedMessage) input.readObject();
				self.store(msg);
				//System.out.println("Received a message: "+msg.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

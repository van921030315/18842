package logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoggerServerThread implements Runnable{
	protected ServerSocket server = null;
	private int port;
	private MessageLogger self;
	
	public LoggerServerThread(int port, MessageLogger messagePasser){
		this.port = port;
		this.self = messagePasser;		
	}
	
	@Override
	public void run() {
		this.OpenSocket();
		
		while(true){
			Socket client = null;
			// Try to accept the connection from the client
			try {
				client = this.server.accept();
			} catch (IOException e) {
	            System.out.println("Server Thread: Failure accepting connection");
				String error = "Client cannot be conncted to the server";
	            throw new RuntimeException(error, e);
			}
			
			// Start a new thread to handle requests from the client
			System.out.println("Logger: New client connected.");
			new Thread(new LoggerReceivingThread(client, this.self)).start();
		}
		
	}
	
	private void OpenSocket(){
		try{
			this.server = new ServerSocket(this.port);
		} catch (IOException e) {
			String error = "Not able to open the port: "+ this.port;
            throw new RuntimeException(error, e);
        }
	}
}

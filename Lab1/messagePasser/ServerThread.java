package messagePasser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable{
	protected ServerSocket server = null;
	private int port;
	private MessagePasser self;
	
	public ServerThread(int port, MessagePasser messagePasser){
		this.port = port;
		this.self = messagePasser;		
	}
	
	@Override
	public void run() {
		this.OpenSocket();
		
		while(true){
			Socket client = null;
			// try to accept the connection from the client
			try {
				client = this.server.accept();
			} catch (IOException e) {
	            System.out.println("Server Thread: Failure accepting connection");
				String error = "Client cannot be conncted to the server";
	            throw new RuntimeException(error, e);
			}
			
			// start a new thread to handle requests from the client
			new Thread(
	                new ReceivingThread(client, this.self)).start();
//			System.out.println("Server Thread: Started receive thread with "+client.getInetAddress() + ":" +client.getPort());
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

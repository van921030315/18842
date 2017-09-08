package demoApp;

import java.io.FileNotFoundException;
import java.util.Scanner;

import messagePasser.Message;
import messagePasser.MessagePasser;
import messagePasser.TimeStampedMessage;

public class DemoAppLab1 {
	private static String configLocation = "configuration.yaml";
	private static String clockType = "vector";
	
	public static void main(String args[]){
		MessagePasser mp = null;
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		
		if(args.length != 1){ // Check for name
			System.out.println("Usage: java DemoApp [NodeName]");
			System.exit(1);
		}
		
		try { // Create MessagePasser
			mp = new MessagePasser(configLocation, args[0], clockType);
		} catch (FileNotFoundException e) {
			System.out.println("Error: Unable to find config file at "+configLocation);
			e.printStackTrace();
			System.exit(1);
		}
		
		while(true){
			System.out.println("Select an option:");
			System.out.println("1. Send a message");
			System.out.println("2. Receive a message");
			System.out.println("3. Trigger timestamped event.");
			System.out.println("4. Flip logging for received messages. (Default off)");
			System.out.print("Choice: ");

			String choice = input.nextLine();
			if(choice.equals("1")){
				System.out.println("-----");
				System.out.print("Input destination name: ");
				String dest = input.nextLine();
				System.out.print("Input kind: ");
				String kind = input.nextLine();
				System.out.print("Input data: ");
				String data = input.nextLine();
				mp.send(new Message(dest, kind, data));
				System.out.println("-----");
			}
			else if(choice.equals("2")){
				System.out.println("-----");
				TimeStampedMessage mess = mp.receive();
				if(mess == null){
					System.out.println("No message received.");
					System.out.println("-----");
					continue;
				}
				System.out.println("Message received.");
				System.out.println("Timestamp: " + mess.get_timestamp().string());
				System.out.println("From: "+ mess.get_source());;
				System.out.println("Sequence: "+ mess.get_seqNum());
				System.out.println("Kind: "+ mess.get_kind());
				if(mess.get_duplicate()){
					System.out.println("Dupe: true");
				}
				System.out.println("Data: " + (String)mess.get_data());
				System.out.println("-----");
			}
			else if(choice.equals("3")){
				System.out.println("Event trigger with timestamp: " + mp.event());
			}
			else if(choice.equals("4")){
				mp.log();
			}
			else{
				continue;
			}
		}
	}
}

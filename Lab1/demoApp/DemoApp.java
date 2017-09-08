package demoApp;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import messagePasser.Message;
import messagePasser.MessagePasser;
import messagePasser.TimeStamp;
import messagePasser.TimeStampedMessage;

public class DemoApp {
	private static String configLocation = "configuration.yaml";
	private static String clockType = "logical";
	
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
			System.out.println("4. Flip logging for sent messages. (Default off)");
			System.out.println("5. Flip logging for received messages. (Default off)");
			System.out.println("6. Get current system time.");
			System.out.println("7. Send multicast message");
			System.out.println("8. DEBUG: View multicast messages");
			
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
				if(mess.isMulticast()){
					System.out.println("Multicast Group: " + mess.getMulticastDest());
					System.out.println("Multicast Timestamp: "+mess.getMulticastTS().string());
				}
				System.out.println("Data: " + (String)mess.get_data());
				System.out.println("-----");
			}
			else if(choice.equals("3")){
				System.out.println("Event trigger with timestamp: " + mp.event());
			}
			else if(choice.equals("4")){
				mp.logSend();
			}
			else if(choice.equals("5")){
				mp.logReceive();
			}
			else if(choice.equals("6")){
				System.out.println("Current time: " + mp.time());
			}
			else if(choice.equals("7")){
				System.out.println("-----");
				System.out.print("Input group name: ");
				String group = input.nextLine();
				System.out.print("Input kind: ");
				String kind = input.nextLine();
				System.out.print("Input data: ");
				String data = input.nextLine();
				TimeStampedMessage msg = new TimeStampedMessage("", kind, data);
				mp.multicast(msg, group);
				System.out.println("-----");
			}
			else if(choice.equals("8")){ // Prints out only multicast messages, relies on incoming list to be ordered
				System.out.println("-----Concurrent Multicast Message Block-----");
				ArrayList<TimeStampedMessage> content = mp.getAllMulti();
				TimeStamp prevts = null;
				
				for(int x = 0; x < content.size(); x++){
					TimeStampedMessage mess = content.get(x);
					if(prevts == null) prevts = mess.get_timestamp();
					
					System.out.println("From: "+ mess.get_source());
					System.out.println("To: "+ mess.get_destination());
					System.out.println("Multicast Group: " + mess.getMulticastDest());
					System.out.println("Multicast Timestamp: "+mess.getMulticastTS().string());
					System.out.println("Kind: "+ mess.get_kind());
					System.out.println("Data: " + (String)mess.get_data());
					if(mess.get_timestamp().isNewer(prevts) != -1){
						prevts = mess.get_timestamp();
						System.out.println("-------- End Block ---------");
						System.out.println("-----Concurrent Multicast Message Block-----");
					}
				}
				
			}
			else{
				continue;
			}
		}
	}
}

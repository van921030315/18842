package demoApp;

import java.io.FileNotFoundException;
import java.util.Scanner;

import logger.MessageLogger;
import messagePasser.TimeStampedMessage;

public class LoggerAppLab1 {
	private static String configLocation = "configuration.yaml";
	
	public static void main(String args[]){
		MessageLogger mp = null;
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		int choice = 0;
		
		try { // Create MessagePasser
			mp = new MessageLogger(configLocation);
		} catch (FileNotFoundException e) {
			System.out.println("Error: Unable to find config file at "+configLocation);
			e.printStackTrace();
			System.exit(1);
		}
		
		while(true){
			System.out.println("Enter how many messages to show:");
			try{
				choice = input.nextInt();
			}
			catch (java.util.InputMismatchException e) {
	            continue;
	        }
			
			System.out.println("Getting "+ choice + " messages...");
			
			for(int x = (choice-1); x >= 0; x--){
				TimeStampedMessage mess = mp.fetch(x);
				if(mess == null){ // Skip till we have a message)
					continue;
				}
				System.out.println("-----Message-----");
				System.out.println("Timestamp: " + mess.get_timestamp().string());
				System.out.println("From: "+ mess.get_source());
				System.out.println("To: "+ mess.get_destination());
				System.out.println("Sequence: "+ mess.get_seqNum());
				System.out.println("Kind: "+ mess.get_kind());
				if(mess.get_duplicate()){
					System.out.println("Dupe: true");
				}
				System.out.println("Data: " + (String)mess.get_data());
				System.out.println("-----------------");
			}
			
		}
	}
}

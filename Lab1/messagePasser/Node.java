package messagePasser;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private String name;
	private int port;
	private String ip;
	private ArrayList<String> affilations;
	
	public Node(String name, int port, String ip, ArrayList<String> affiliation){
		this.name = name;
		this.port = port;
		this.ip = ip;
		this.affilations = affiliation;
		
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getIp(){
		return this.ip;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public  ArrayList<String> getAffilations(){
		return this.affilations;
	}
	
}

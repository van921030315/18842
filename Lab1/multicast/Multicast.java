package multicast;
import messagePasser.*;
import java.util.*;

public class Multicast {
	//Node self;
	String self;
	HashMap<String, ArrayList<Node> > groups;
	 // key: group id value: hold back queue associated with each group
	HashMap<String, LinkedList<TimeStampedMessage> > holdbackQueus; 
	MessagePasser messagePasser;
	ArrayList<TimeStampedMessage> receiveList;
	
	
	
	public Multicast(HashMap<String, ArrayList<Node> > Mygroups, String name, MessagePasser mp){
		this.self = name;
		this.groups = Mygroups;
		this.messagePasser = mp;
		this.holdbackQueus = new HashMap<String, LinkedList<TimeStampedMessage> >();
		for(String group: this.groups.keySet()){
			LinkedList<TimeStampedMessage> newlist = new LinkedList<TimeStampedMessage>();
			this.holdbackQueus.put(group, newlist);
		}
	}
	
	// if this is the first time receiving a multicast message, flood the message to all
	// group members except self and the sender. Called by receive function in messagepasser
	public ArrayList<TimeStampedMessage> floodMessage(TimeStampedMessage msg){
		String group = msg.getMulticastDest();
		ArrayList<Node> members = this.groups.get(group);
		ArrayList<TimeStampedMessage> Gmessage = new ArrayList<TimeStampedMessage>();
		
		for(Node member: members){
			if(member.getName().equals(msg.get_source()) || 
					member.getName().equals(this.self))
				continue;
			TimeStampedMessage newMsg = new TimeStampedMessage(msg);
			newMsg.set_dest(member.getName());
			this.messagePasser.send(newMsg);
			Gmessage.add(newMsg);
		}
		// push the message to the hold back queue
		//this.holdbackQueus.get(group).add(msg);
		
		return Gmessage;
	}
	
	public void multicast(String groupname, Message msg){
		msg.set_source(self);
		TimeStampedMessage tsm = new TimeStampedMessage(msg, this.messagePasser.getClock().getTimeStamp());
		tsm.setMulticast();
		tsm.setMulticastDest(groupname);
		this.sendMulticastMessage(tsm);
	}
	
	public void sendMulticastMessage(TimeStampedMessage msg){
		//msg.setMulticast();
		String group = msg.getMulticastDest();
		ArrayList<Node> members = this.groups.get(group);
		ArrayList<TimeStampedMessage> Gmessage = new ArrayList<TimeStampedMessage>();
		// TODO: seqNumber? Probably don't need since we use timestamps
		// TODO: get multicast TS
		for(Node member: members){
			if(member.getName().equals(this.self))
				continue;
			TimeStampedMessage newMsg = new TimeStampedMessage(msg);
			// TODO: add multicast TS here with newMsg.setMulticastTS(ts)
			newMsg.set_dest(member.getName());
			this.messagePasser.send(newMsg);
			Gmessage.add(newMsg);
		}
		// push the message to the hold back queue
		
	}
	
	public boolean hasMessage(TimeStampedMessage msg){
		// sender group name timestamp
		// message is different 
		// Check if source, multicastDest, and multicastTS match to see if its same message
		
		for(TimeStampedMessage t : this.receiveList){
			if(t.get_source().equals(msg.get_source())){
				if(t.getMulticastTS().isNewer(msg.getMulticastTS()) == -1){
					if(t.getMulticastDest().equals(msg.getMulticastDest())){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void deliverMulticastMessage(TimeStampedMessage msg) {
		if(!this.hasMessage(msg)){
			this.receiveList.add(msg);
			this.holdbackQueus.get(msg.getMulticastDest()).add(msg);
			this.floodMessage(msg);
			// TODO: Deliver message to application level
			// I think you can just use deliver(msg) here?
		}
		// add the message
		
	}
	
	public void deliver(TimeStampedMessage msg){
		// TODO: sort message by vector timestamp to maintain casual order
		this.holdbackQueus.get(msg.getMulticastDest()).remove(msg);
		this.messagePasser.puttoInbox(msg);
	}
	
	
}

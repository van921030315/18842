package messagePasser;

import java.io.Serializable;

public class TimeStampedMessage extends Message implements Serializable  {
	private static final long serialVersionUID = 1232172033143272249L;
	
	TimeStamp ts = null;
	String multicastDest = ""; // Name of group
	TimeStamp multicastTS = null; // Multicast specific timestamp
	Boolean isMulticast = false;


	public TimeStampedMessage(String dest, String kind, Object data) {
		super(dest, kind, data);
	}
	
	public TimeStampedMessage(TimeStampedMessage m) {
		super(m);
		this.ts = new TimeStamp(m.get_timestamp());
		this.multicastDest = m.getMulticastDest();
		this.multicastTS = m.getMulticastTS();
		this.isMulticast = m.isMulticast();
	}
	
	public TimeStampedMessage(Message m, TimeStamp ts) {
		super(m);
		this.ts = new TimeStamp(ts);
	}
	
	public TimeStamp get_timestamp(){
		return this.ts;
	}
	
	public void set_timestamp(TimeStamp stamp){
		this.ts = stamp;
	}
    
    public void displayMessage(){
        System.out.println("---------Message---------\n");
        System.out.println("Src: "+this.get_source() + "\n");
        System.out.println("Dest: "+this.get_destination() + "\n");
        System.out.println("Kind: " +this.get_kind()+ "\n");
        System.out.println("SeqNum: "+this.get_seqNum()+ "\n");
        System.out.println("TimeStamp: "+ this.get_timestamp().string()+ "\n");
        System.out.println("-------------------------\n"); 
    }

	public void set_dest(String mem) {
		this.destination = mem;		
	}
	
	public void setMulticastDest(String dest){
		multicastDest = dest;
	}
	
	public String getMulticastDest(){
		return this.multicastDest;
	}
	
	public TimeStamp getMulticastTS() {
		return multicastTS;
	}

	public void setMulticastTS(TimeStamp multicastTS) {
		this.multicastTS = multicastTS;
	}
	
	public void setMulticast(){
		this.isMulticast = true;
	}
	
	public boolean isMulticast(){
		return this.isMulticast;
	}
}
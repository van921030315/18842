package messagePasser;
import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = -824824081843568066L;
	
	String source = null; // Source name
	String destination = null; // Destination name
	int sequenceNumber; // Sequence number
	Boolean dupe = false; // Duplicate message?
	String kind = null; // What kind of data
	Object data; // Data itself
	int mutiSeq = -1; // sequence number for multicast
	
	public Message(String dest, String kind, Object data){
		this.destination = dest;
		this.kind = kind;
		this.data = data;
	}
	
	// Constructor to duplicate a message for dupe
	public Message(Message m){
		this.destination = new String(m.get_destination());
		this.kind = new String(m.get_kind());
		this.source = new String(m.get_source());
		this.sequenceNumber = m.sequenceNumber;
		this.data = new String((String)m.get_data());
		m.dupe = true;
	}
	
	// These setters and getters are used by MessagePasser.send( ), not your app
	public void set_source(String source){ // name of sending process
		this.source = source;
	}
	
	public String get_source(){
		return source;
	}
	
	public String get_destination(){
		return destination;
	}
	
	public String get_kind(){
		return kind;
	}
	
	public void set_seqNum(int sequenceNumber){
		this.sequenceNumber = sequenceNumber;
	}
	
	public int get_seqNum(){
		return sequenceNumber;
	}
	
	public void set_multiseqNum(int sequenceNumber){
		this.mutiSeq = sequenceNumber;
	}
	
	public int get_multiseqNum(){
		return this.mutiSeq;
	}
	
	
	public void set_duplicate(Boolean dupe){
		this.dupe = dupe;
	}
	
	public Boolean get_duplicate(){
		return dupe;
	}
	
	public Object get_data(){
		return this.data;
	}
	
}
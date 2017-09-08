package clockService;

import java.util.ArrayList;
import java.util.HashMap;
import messagePasser.Node;
import messagePasser.TimeStamp;

public class VectorClockService implements ClockService {
	private HashMap<String, Integer> timeStampVector = new HashMap<String, Integer>();
	private String myName;
	
	private Boolean debug = false;
	
	
	public VectorClockService(int size, String name, ArrayList<Node> nodes){
		this.myName = name;
		// initialize all elemetns in the timestamp vector to 0
		for (Node node: nodes){
			this.timeStampVector.put(node.getName(), 0);
		}
		
	}
	
	public TimeStamp getTimeStamp(){
		// increment the local clock and return the vector
		this.timeStampVector.put(myName, this.getSelfTime()+1);
		if(debug){
			System.out.println("===========Show time stamp==========\n");
			for(String node: this.timeStampVector.keySet()){
				System.out.println(node+":"+this.timeStampVector.get(node)+"\n");
			}
			System.out.println("====================================\n");
		}
		return new TimeStamp(this.timeStampVector);
	}
	
	public TimeStamp getTime(){
		this.timeStampVector.put(myName, this.getSelfTime());
		return new TimeStamp(this.timeStampVector);
	}
	
	public void updateClockTime(TimeStamp ts) {
		if(debug)
			System.out.println("Updating vector clock\n");
		// TODO Auto-generated method stub
		this.getTimeStamp();
		assert(ts.get_type().equals("vector"));
		@SuppressWarnings("unchecked")
		HashMap<String, Integer> other = (HashMap<String, Integer>) ts.get_time();
		
		for( String node: other.keySet()){
			if(other.get(node) > this.timeStampVector.get(node)){
				this.timeStampVector.put(node, other.get(node));
			}
		}
	}
	
	private Integer getSelfTime(){
		return this.timeStampVector.get(myName);
	}

	@Override
	public String getClockType() {
		return "vector";
	}
}

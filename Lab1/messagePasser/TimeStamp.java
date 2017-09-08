package messagePasser;

import java.io.Serializable;
import java.util.HashMap;

public class TimeStamp implements Serializable  {
	private static final long serialVersionUID = -4247956580040193102L;
	String type = "vector";
	private Object ts = null;
	
	// Logical timestamp
	public TimeStamp(Integer ts){
		this.type = "logical";
		this.ts = ts;
	}
	
	// Vector timestamp
	public TimeStamp(HashMap<String, Integer> ts){
		this.type = "vector";
		this.ts = ts;
	}
	
	// Used for cloning timestamp
	public TimeStamp(TimeStamp timeStamp){
		if(timeStamp.get_type().equals("logical")){
			this.type = "logical";
		}
		else {
			this.type = "vector";
		}
		this.ts = timeStamp.get_time();
	}
	
	// Returns if timestamp is larger or not
	public int isNewer(TimeStamp incoming){
		if(type.equals("logical")){
			if((Integer)incoming.get_time() < (Integer)this.ts){
				return 1; // This stamp is newer than other stamp
			}
			else if((Integer)incoming.get_time() > (Integer)this.ts){
				return 0;
			}
			return -1;
		}
		else{
			@SuppressWarnings("unchecked")
			HashMap<String, Integer> self = (HashMap<String, Integer>) this.ts;
			@SuppressWarnings("unchecked")
			HashMap<String, Integer> other = (HashMap<String, Integer>) incoming.get_time();
			Boolean equal = true;
			Boolean lessorequal= true;
			Boolean moreorequal = true;
			for (String node: self.keySet()){
				if (self.get(node) != other.get(node)){
					equal = false;
				}else{
				}
				if(self.get(node) > other.get(node)){
					moreorequal = false;
				}
				if(self.get(node) < other.get(node)){
					lessorequal = false;
				}
				
			}
			if(lessorequal && !equal){
				return 1;
			}else if(moreorequal && !equal ){
				return 0;
			}else {
				return -1;
			}

		}
		//return -1; //-1 for concurrent
	}
	

	
	// Returns value as a string
	public String string(){
		if(this.type.equals("logical")){
			return ts.toString();
		}
		else{
			String clockstr = "|";
			@SuppressWarnings("unchecked")
			HashMap<String, Integer> vectorclock = (HashMap<String, Integer>) this.ts;
			for(String node: vectorclock.keySet()){
				clockstr = clockstr+node+":"+vectorclock.get(node)+ "|";
			}
			return clockstr;
		}
	}
	
	public Object get_time(){
		return this.ts;
	}
	
	public String get_type(){
		return this.type;
	}
}

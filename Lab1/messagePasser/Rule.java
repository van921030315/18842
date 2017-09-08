package messagePasser;

import java.util.Map;

public class Rule {
	private String action;
	private String src;  	// null if not specified
	private String dest;	// null if not specified
	private String kind;	// null if not specified
	private int seqnum;		// -1 if not specified
	private int duplicate;	// -1 if not specified
	
	public Rule(Map<String, Object> rule) {
		// TODO Auto-generated constructor stub
		if(!rule.containsKey("action")){
			System.err.println("ERR: Invalid rule!");
		}else{
			this.action = (String) rule.get("action");
			
			this.src = (String) rule.get("src");
			this.dest = (String) rule.get("dest");
			this.kind = (String) rule.get("kind");
			
			if(rule.get("seqNum") == null){
				this.seqnum = -1;
			}else{
				this.seqnum = (Integer) rule.get("seqNum");
			}
			if(rule.get("duplicate") == null){
				this.duplicate = -1; // for unspecified
			}else{
				this.duplicate = (Boolean) rule.get("duplicate")? 1 : 0;
			}
		}
		
	}
	
	public String getAction(){
		return this.action;
	}
	
	public String getSrc(){
		return this.src;
	}
	
	public String getDest(){
		return this.dest;
	}
	
	public String getKind(){
		return this.kind;
	}
	
	public int getSeqnum(){
		return this.seqnum;
	}
	
	public int getDuplicate(){
		return this.duplicate;
	}
	
	public boolean match(Message msg){
		// dropAfter rule match
		if(this.action.equals("dropAfter")){
			if(this.dest == null || msg.get_destination().equals(this.dest)){
				if(this.src == null || msg.get_source().equals(this.src)){
					if(this.seqnum == -1 || msg.get_seqNum() > this.seqnum){
						if(this.kind == null || this.kind.equals(msg.get_kind())){
							if(this.duplicate == -1 || (msg.get_duplicate() ? 1 : 0) ==  this.duplicate){
								return true;
							}
						}
					}
				}
			}
		}
		else{ // Any rule except dropAfter
			if(this.dest == null || (this.dest != null && msg.get_destination().equals(this.dest))){
				if(this.src == null || (this.src != null && msg.get_source().equals(this.src))){
					if(this.seqnum == -1 || msg.get_seqNum() == this.seqnum){
						if(this.kind == null || (this.kind != null && this.kind.equals(msg.get_kind()))){
							if(this.duplicate == -1 || (msg.get_duplicate() ? 1 : 0) ==  this.duplicate){
								//System.out.println("Rule: Bad");
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}

	

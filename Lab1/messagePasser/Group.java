package messagePasser;

import java.util.List;
import java.util.Map;

public class Group {
	String GroupID;
	List<String> members;
	
	@SuppressWarnings("unchecked")
	public Group( Map<String, Object> group){
		this.GroupID = (String) group.get("name");
		this.members = (List<String>) group.get("members");
	}
	
	public String getGroupId(){
		return this.GroupID;
	}
	
	public List<String> getMembers(){
		return this.members;
	}
}

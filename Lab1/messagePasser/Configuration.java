package messagePasser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

import org.yaml.snakeyaml.*;

public class Configuration {
	ArrayList<Node> nodes = new ArrayList<Node>();
	ArrayList<Rule> sendRules = new ArrayList<Rule>();
	ArrayList<Rule> recvRules = new ArrayList<Rule>();
	ArrayList<Group> groups = new ArrayList<Group>();


	List<Map<String, Object>> config_list;
	List<Map<String, Object>> sendRules_list;
	List<Map<String, Object>> recvRules_list;
	List<Map<String, Object>> group_list;
	
	Pattern p = Pattern.compile("-");

    
    File configFile = null;


	// YAML references: https://gist.github.com/ericlee996/3688218
    
	@SuppressWarnings("unchecked")
	public Configuration(String configuration_filename) throws FileNotFoundException {
		Yaml file = new Yaml();
        /*
        try {
            URLreader reader = new URLreader(configuration_filename);
            configFile = reader.getFile();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(-1);
        }*/
        
		@SuppressWarnings("unchecked")
		Map<String, List<Map<String, Object>>> load =
        (Map<String, List<Map<String, Object>>>) file.load(new FileInputStream(configuration_filename));
		config_list = load.get("configuration");
		if(config_list == null){
			System.err.println("ERR: Configuration load failed!");
		}
		for(Map<String, Object> config: config_list){
			//System.out.println(config.get("port"));
			//Map<String, Object> group = (Map<String, Object>) config.get("memberOf");
			ArrayList<String> aff = null;
			Object s = config.get("memberOf");
			if(s!=null){
				//System.out.println(s.toString());
				//System.out.println(s.getClass()+"\n");
				if(s.getClass().toString().contains("ArrayList")){
					aff = (ArrayList<String>) s;
					//System.out.println("ArrayList!\n");
				}else{
					aff = new ArrayList<String> ();
					s = (String)s;
					String[] affs = this.p.split((CharSequence) s);
					for(String a: affs){
						aff.add(a.replaceAll(" ", ""));
					}
				}
			}
			Node new_node = new Node((String) config.get("name"), (Integer)config.get("port"), (String) config.get("ip"), aff);
			nodes.add(new_node);
		}
		
		
		sendRules_list = load.get("sendRules");
		if(sendRules_list == null){
			System.out.println("No send rule specified!");
		}else{
			for(Map<String, Object> rule: sendRules_list){
				Rule new_rule = new Rule(rule);
				this.sendRules.add(new_rule);
			}
		}
		
		recvRules_list = load.get("receiveRules");
		if(recvRules_list == null){
			System.out.println("No send rule specified!");
		}else{
			for(Map<String, Object> rule: recvRules_list){
				Rule new_rule = new Rule(rule);
				this.recvRules.add(new_rule);
			}
		}
		
		group_list = load.get("groups");
		if(group_list == null){
			System.out.println("No group rule specified!");
		}else{
			for(Map<String, Object> group: group_list){
				Group new_group = new Group(group);
				this.groups.add(new_group);
			}
		}
		
		
		
		
		
	}
	
	public ArrayList<Node> getNodes(){
		if(nodes.size() == 0){
			System.out.println("The required nodes list does not exist!");
			return null;
		}
		return nodes;
	}
	
	public Node getNode(String node_name){
		for(Node n: this.nodes){
			if(n.getName().equals(node_name))
				return n;
		}
		System.out.println("Member is dead");
		return null;
	}
	
	public ArrayList<Rule> getSendRules(){
		return this.sendRules;
	}
	
	public ArrayList<Rule> getRecvRules(){
		return this.recvRules;
	}
	
	public ArrayList<Group> getGroups(){
		return this.groups;
	}
	
	//public generateSendRules()
}

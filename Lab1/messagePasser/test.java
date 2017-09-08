package messagePasser;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class test {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		System.out.println("test");
		Configuration file = new Configuration("configuration.yaml");
		System.out.println("test");
		
		//Node mynode = file.getNodes().get(0);
		for(Node mynode: file.getNodes()){
		System.out.println(mynode.getName());
		System.out.println(mynode.getIp());
		System.out.println(mynode.getPort());
		System.out.println("Members of:\n");
		if(mynode.getAffilations()==null)
			continue;
		for(String s: mynode.getAffilations()){
			
			System.out.println("  "+s+"\n");
		}
		}
		/*
		Rule mysendrule = file.getRecvRules().get(0);
		System.out.println(mysendrule.getDest());
		System.out.println(mysendrule.getDuplicate());
		System.out.println(mysendrule.getSrc());
		System.out.println(mysendrule.getSeqnum());
*/
		ArrayList<Group> groups = file.getGroups();
		for(Group g: groups){
			System.out.println(g.getGroupId());
			for(String name: g.getMembers()){
				System.out.println(name);
			}
		}



	}

}

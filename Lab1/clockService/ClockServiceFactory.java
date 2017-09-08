package clockService;
import java.util.ArrayList;

import messagePasser.Node;

public class ClockServiceFactory{

	public ClockService getClockService(String type, int size, String self, ArrayList<Node> nodes){
		if(type.equals("logical")){
			return new LogicalClockService();
		}
		else if(type.equals("vector")){
			return new VectorClockService(size,  self, nodes);
		}
		return null;
	}
}

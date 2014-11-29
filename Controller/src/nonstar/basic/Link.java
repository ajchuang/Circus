package nonstar.basic;

import java.util.HashMap;

public class Link {
	HashMap<Switch, Integer> mapSwPort;
	int lambda;
	
	public Link() {
		super();
		mapSwPort = new HashMap<Switch, Integer>(2);
		lambda = 0;	// lambda = 0 to present global link
	}
	
	public boolean setSwitch(Switch sw, int port) {
		if (mapSwPort.containsKey(sw))
			return false;
		System.out.println("LinksetSwitch:" + sw + "/port" + port);
		mapSwPort.put(sw, port);
		return true;
	}
	
	public void setLambda(int l) {
		System.out.println("LinksetLambda:" + l);
		lambda = l;
	}
	
	public int getPort(Switch sw) {
		if (mapSwPort.containsKey(sw))
			return mapSwPort.get(sw);
		return -1;
	}
	
	public boolean isPeer(Switch sw) {
		return mapSwPort.containsKey(sw);
	}
}

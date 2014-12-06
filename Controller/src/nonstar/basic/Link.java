package nonstar.basic;

import java.util.HashMap;

public class Link {
	Switch srcSw, dstSw;
	HashMap<Switch, Integer> mapSwPort;
	int lambda;

	public Link() {
		super();
		srcSw = null;
		dstSw = null;
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

	public int getLambda() {
		return lambda;
	}

	public int getPort(Switch sw) {
		if (mapSwPort.containsKey(sw))
			return mapSwPort.get(sw);
		return -1;
	}

	public boolean isPeer(Switch sw) {
		return mapSwPort.containsKey(sw);
	}

	public void setSource(Switch sw) {
		srcSw = sw;
	}

	public boolean isSource(Switch sw) {
		if (srcSw != null && srcSw == sw)
			return true;
		return false;
	}

	public void setDestination(Switch sw) {
		dstSw = sw;
	}

	public boolean isDestination(Switch sw) {
		if (dstSw != null && dstSw == sw)
			return true;
		return false;
	}
}

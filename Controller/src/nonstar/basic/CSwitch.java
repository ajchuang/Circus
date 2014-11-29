package nonstar.basic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CSwitch extends Switch {
	public CSwitch() {
		super();
		mapPortSwitch = new HashMap<Integer, Switch>();
		mapSwitchPort = new HashMap<Switch, Integer>();
		mapPortLambda = new HashMap<Integer, HashSet<Integer>>();
	}
	
	@Override
	public void setId(int swId) {
		id = swId;
	}
	
	public String toString() {
		return "Sw" + Integer.toString(id);
	}
	
	@Override
	public boolean connectSwitch(int port, Switch sw) {
		if (mapPortSwitch.containsKey(port) || mapSwitchPort.containsKey(sw))
			return false;
		System.out.println("CSconnectSw:" + sw.toString() + "/port" + port);
		mapPortSwitch.put(port, sw);
		mapSwitchPort.put(sw, port);
		return true;
	}
	
	@Override
	public boolean allocLambda(int port, int lambda) {
		HashSet<Integer> lambdaSet;
		
		lambdaSet = mapPortLambda.get(port);
		if (lambdaSet == null) {
			lambdaSet = new HashSet<Integer>();
			mapPortLambda.put(port, lambdaSet);
		}
		System.out.println("CSallocLambda:port" + port + "/lambda" + lambda);
		return lambdaSet.add(lambda);
	}
	
	@Override
	public boolean freeLambda(int port, int lambda) {
		HashSet<Integer> lambdaSet;
		
		lambdaSet = mapPortLambda.get(port);
		if (lambdaSet == null)
			return false;
		System.out.println("CSfreeLambda:port" + port + "/lambda" + lambda);
		return lambdaSet.remove(lambda);
	}
	
	@Override
	public int getAvaiableLambda(int port) {
		HashSet<Integer> lambdaSet;
		
		lambdaSet = mapPortLambda.get(port);
		if (lambdaSet == null)
			return 1;
		int l = 1;
		while (lambdaSet.contains(l))
			l++;
		System.out.println("CSgetLambda:port" + port + "/lambda" + l);
		return l;
	}
	
	@Override
	public boolean testLambda(int port, int lambda) {
		HashSet<Integer> lambdaSet;
		
		lambdaSet = mapPortLambda.get(port);
		if (lambdaSet == null)
			return false;
		System.out.println("CStestLambda:port" + port + "/lambda" + lambda);
		return lambdaSet.contains(lambda);
	}
	
	@Override
	public Switch getNeighborSwitch(int port) {
		return mapPortSwitch.get(port);
	}

	@Override
	public Set<Integer> getPortSet() {
		return mapPortSwitch.keySet();
	}
	
	@Override
	public int getOutputPort(Switch tgtSw) {
		return mapSwitchPort.get(tgtSw);
	}
}

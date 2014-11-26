package nonstar.basic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PSwitch extends Switch {
	public PSwitch() {
		super();
		mapPortSwitch = new HashMap<Integer, Switch>();
		mapPortLambda = new HashMap<Integer, HashSet<Integer>>();
	}
	
	@Override
	public boolean allocLambda(int port, int lambda) {
		HashSet<Integer> lambdaSet;
		
		lambdaSet = mapPortLambda.get(port);
		if (lambdaSet == null) {
			lambdaSet = new HashSet<Integer>();
			mapPortLambda.put(port, lambdaSet);
		}
		return lambdaSet.add(lambda);
	}
	
	@Override
	public boolean freeLambda(int port, int lambda) {
		HashSet<Integer> lambdaSet;
		
		lambdaSet = mapPortLambda.get(port);
		if (lambdaSet == null)
			return false;
		return lambdaSet.remove(lambda);
	}
	
	@Override
	public int getAvaiableLambda(int port) {
		return 0;
	}
	
	@Override
	public Switch getNeighborSwitch(int port) {
		return mapPortSwitch.get(port);
	}
	
	@Override
	public Set<Integer> getPortSet() {
		return mapPortSwitch.keySet();
	}
}

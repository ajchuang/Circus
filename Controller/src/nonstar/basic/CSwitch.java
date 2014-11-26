package nonstar.basic;

import java.util.HashMap;
import java.util.HashSet;

public class CSwitch extends Switch {
	public CSwitch() {
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
}

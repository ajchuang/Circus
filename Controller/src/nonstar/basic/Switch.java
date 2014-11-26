package nonstar.basic;

import java.util.HashMap;
import java.util.HashSet;

public abstract class Switch {
	HashMap<Integer, Switch> mapPortSwitch;
	HashMap<Integer, HashSet<Integer>> mapPortLambda;
	
	public boolean allocLambda(int port, int lambda) {
		return false;
	}
	
	public boolean freeLambda(int port, int lambda) {
		return false;
	}
	
	public int getAvaiableLambda(int port) {
		return 0;
	}
}

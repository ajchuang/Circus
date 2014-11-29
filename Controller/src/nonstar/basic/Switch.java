package nonstar.basic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class Switch {
	int id;
	HashMap<Integer, Switch> mapPortSwitch;
	HashMap<Switch, Integer> mapSwitchPort;
	HashMap<Integer, HashSet<Integer>> mapPortLambda;
	
	public void setId(int swId) {
	}
	
	public boolean connectSwitch(int port, Switch sw) {
		return false;
	}
	
	public boolean allocLambda(int port, int lambda) {
		return false;
	}
	
	public boolean freeLambda(int port, int lambda) {
		return false;
	}
	
	public int getAvaiableLambda(int port) {
		return 0;
	}
	
	public boolean testLambda(int port, int lambda) {
		return false;
	}
	
	public Switch getNeighborSwitch(int port) {
		return null;
	}
	
	public Set<Integer> getPortSet() {
		return null;
	}
	
	public int getOutputPort(Switch tgtSw) {
		return -1;
	}
}

package nonstar.basic;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class Switch {
	int id;
	HashMap<Integer, Switch> mapPortSwitch;
	HashMap<Switch, Integer> mapSwitchPort;
	HashMap<Integer, HashSet<Integer>> mapPortLambda;
	ObjectOutputStream objOutStream;

	public void setId(int swId) {
		id = swId;
	}

	public int getId() {
		return -1;
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

	public ObjectOutputStream getObjOutStream() {
		return null;
	}
}

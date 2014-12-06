package nonstar.basic;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PSwitch extends Switch {
	HashSet<InetAddress> connectedIp;

	public PSwitch(ObjectOutputStream oos) {
		super();
		id = -1;
		mapPortSwitch = new HashMap<Integer, Switch>();
		mapSwitchPort = new HashMap<Switch, Integer>();
		mapPortLambda = new HashMap<Integer, HashSet<Integer>>();
		objOutStream = oos;
		connectedIp = new HashSet<InetAddress>();
	}

	public boolean addIp(InetAddress ip) {
		return connectedIp.add(ip);
	}

	public boolean connectedToIp(InetAddress ip) {
		return connectedIp.contains(ip);
	}

	@Override
	public void setId(int swId) {
		id = swId;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Sw" + Integer.toString(id);
	}

	@Override
	public boolean connectSwitch(int port, Switch sw) {
		if (mapPortSwitch.containsKey(port) || mapSwitchPort.containsKey(sw))
			return false;
		System.out.println(this + "PSconnectSw:" + sw.toString() + "/port" + port);
		mapPortSwitch.put(port, sw);
		mapSwitchPort.put(sw, port);
		return true;
	}

	@Override
	public boolean allocLambda(int port, int lambda) {
		if (port < 0 || lambda <= 0)
			return false;

		HashSet<Integer> lambdaSet;

		lambdaSet = mapPortLambda.get(port);
		if (lambdaSet == null) {
			lambdaSet = new HashSet<Integer>();
			mapPortLambda.put(port, lambdaSet);
		}
		System.out.println("PSallocLambda:port" + port + "/lambda" + lambda);
		return lambdaSet.add(lambda);
	}

	@Override
	public boolean freeLambda(int port, int lambda) {
		if (port < 0 || lambda <= 0)
			return false;

		HashSet<Integer> lambdaSet;

		lambdaSet = mapPortLambda.get(port);
		if (lambdaSet == null)
			return false;
		System.out.println("PSfreeLambda:port" + port + "/lambda" + lambda);
		return lambdaSet.remove(lambda);
	}

	@Override
	public int getAvaiableLambda(int port) {
		if (port < 0)
			return -1;

		HashSet<Integer> lambdaSet;

		lambdaSet = mapPortLambda.get(port);
		if (lambdaSet == null)
			return 1;
		int l = 1;
		while (lambdaSet.contains(l))
			l++;
		System.out.println("PSgetLambda:port" + port + "/lambda" + l);
		return l;
	}

	@Override
	public boolean testLambda(int port, int lambda) {
		if (port < 0 || lambda <= 0)
			return false;

		HashSet<Integer> lambdaSet;

		lambdaSet = mapPortLambda.get(port);
		if (lambdaSet == null)
			return false;
		System.out.println("PStestLambda:port" + port + "/lambda" + lambda);
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

	@Override
	public ObjectOutputStream getObjOutStream () {
		return objOutStream;
	}
}

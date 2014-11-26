package nonstar.network;

import java.util.HashMap;
import java.util.Set;

import nonstar.basic.Flow;
import nonstar.basic.Link;
import nonstar.basic.Switch;
import nonstar.interpreter.NetworkTopo;

public class NetworkEnv implements NetworkTopo {
	HashMap<Integer, Switch> mapIdSwitch;
	HashMap<Link, Flow> mapLinkFlow;
	
	public NetworkEnv() {
		super();
		mapIdSwitch = new HashMap<Integer, Switch>();
		mapLinkFlow = new HashMap<Link, Flow>();
	}

	@Override
	public Flow getCurrCircuit(int src, int dst) {
		// TODO Auto-generated method stub
		/*
		 * Link is used to describe the complete link here
		 * Search src and dst in the link to find the Flow
		 */
		Switch srcSw = mapIdSwitch.get(src);
		Switch dstSw = mapIdSwitch.get(dst);
		Set<Link> linkSet = mapLinkFlow.keySet();
		
		for (Link l : linkSet) {
			if (l.isPeer(srcSw) && l.isPeer(dstSw))
				return mapLinkFlow.get(l);
		}
		
		return null;
	}

	@Override
	public Flow setupCircuit(int src, int dst) {
		// TODO Auto-generated method stub
		return null;
	}
	

}

package nonstar.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
		/**
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
	
	private LinkedList<Switch> getPath(Switch srcSw, Switch dstSw) {
		HashMap<Switch, Integer> swDist = new HashMap<Switch, Integer>();
		HashMap<Switch, Switch> swPrev= new HashMap<Switch, Switch>();
		HashSet<Switch> toVisitSw = new HashSet<Switch>();
		
		/* Initialize tovisitSw */
		for (int i : mapIdSwitch.keySet()) {
			toVisitSw.add(mapIdSwitch.get(i));
		}
		swDist.put(srcSw, 0);
		
		while (!toVisitSw.isEmpty()) {
			/* Find minimum distance node */
			Switch swMin = null;
			int dist = -1;
			for (Switch sw : toVisitSw) {
				if (swDist.get(sw) != null) {
					if ((dist < 0) || (swDist.get(sw) < dist)) {
						dist = swDist.get(sw);
						swMin = sw;
					}
				}
			}
			
			toVisitSw.remove(swMin);
			if (swMin == dstSw)
				break;
			
			/* Compute neighbors */
			for (int port : swMin.getPortSet()) {
				Switch sw = swMin.getNeighborSwitch(port);
				int alt = swDist.get(swMin) + 1;
				if (((swDist.get(sw)) == null) || (alt < swDist.get(sw))) {
					swDist.put(sw, alt);
					swPrev.put(sw, swMin);
				}
			}
		}
		
		/* Read shortest path */
		Switch tmpSw = dstSw;
		LinkedList<Switch> path = new LinkedList<Switch>();
		while (swPrev.get(tmpSw) != null) {
			path.addFirst(tmpSw);
			tmpSw = swPrev.get(tmpSw);
		}
		path.addFirst(tmpSw);
		
		return path;
	}

	@Override
	public Flow setupCircuit(int src, int dst) {
		// TODO Auto-generated method stub
		/**
		 * 
		 */
		if (src == dst)
			return null;
		
		Switch srcSw = mapIdSwitch.get(src);
		Switch dstSw = mapIdSwitch.get(dst);
		
		LinkedList<Switch> path = getPath(srcSw, dstSw);
		
		/* Establish links between switches in the path */
		
		return null;
	}
	

}

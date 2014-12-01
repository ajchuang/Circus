package nonstar.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nonstar.basic.CSwitch;
import nonstar.basic.Flow;
import nonstar.basic.Link;
import nonstar.basic.PSwitch;
import nonstar.basic.Switch;
import nonstar.interpreter.NetworkTopo;

public class NetworkEnv implements NetworkTopo {
	static int idLast = 0;
	HashMap<Integer, Switch> mapIdSwitch;
	HashMap<Link, Flow> mapLinkFlow;
	
	public NetworkEnv() {
		super();
		mapIdSwitch = new HashMap<Integer, Switch>();
		mapLinkFlow = new HashMap<Link, Flow>();
	}
	
	public int addSwitch(Switch sw) {
		if (mapIdSwitch.containsValue(sw))
			return -1;
		int id = idLast++;
		System.out.println("NEaddSw:" + id + "/" + sw.toString());
		sw.setId(id);
		mapIdSwitch.put(id, sw);
		return id;
	}
	
	public boolean connectSwitch(Switch swL, int portL, Switch swR, int portR) {
		if (!mapIdSwitch.containsValue(swL) || !mapIdSwitch.containsValue(swR))
			return false;
		swL.connectSwitch(portL, swR);
		swR.connectSwitch(portR, swL);
		return true;
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
		
		for (Link l : linkSet)
			if (l.isPeer(srcSw) && l.isPeer(dstSw))
				return mapLinkFlow.get(l);
		
		return null;
	}
	
	private Link establishLink(Switch srcSw, Switch dstSw) {
		Link newLink = new Link();
		int srcPort = srcSw.getOutputPort(dstSw);
		int dstPort = dstSw.getOutputPort(srcSw);
		int srcLambda = srcSw.getAvaiableLambda(srcPort);
		int dstLambda = dstSw.getAvaiableLambda(dstPort);
		
		while (srcLambda != dstLambda) {
			if (!dstSw.testLambda(dstPort,  srcLambda))
				dstLambda = srcLambda;
			else if (!srcSw.testLambda(srcPort, dstLambda))
				srcLambda = dstLambda;
			else {
				do {
					srcLambda++;
				} while (srcSw.testLambda(srcPort, srcLambda));
				do {
					dstLambda++;
				} while (dstSw.testLambda(dstPort, dstLambda));
			}
		}
		
		srcSw.allocLambda(srcPort, srcLambda);
		newLink.setSwitch(srcSw, srcPort);
		dstSw.allocLambda(dstPort, dstLambda);
		newLink.setSwitch(dstSw, dstPort);
		newLink.setLambda(srcLambda);
		
		System.out.println("establish:" + srcSw.toString() + "/" + srcPort + "=" + srcLambda + "=" + dstSw.toString() + "/" + dstPort);
		return newLink;
	}
	
	private Flow getFlow(Switch srcSw, Switch dstSw) {
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
		Flow flow = new Flow();
		while (swPrev.get(tmpSw) != null) {
			flow.addLinkFirst(establishLink(swPrev.get(tmpSw), tmpSw));
			flow.addSwFirst(tmpSw);
			tmpSw = swPrev.get(tmpSw);
		}
		flow.addSwFirst(tmpSw);
		
		return flow;
	}
	
	private boolean walkFlow(Switch srcSw, Switch dstSw, Flow flow) {
		Iterator<Switch> iterSw = flow.getSwIter();
		Iterator<Link> iterLink = flow.getLinkIter();
		Link LinkIn = null, LinkOut = null;
		Switch tmpSw = null;
		
		while (iterSw.hasNext()) {
			tmpSw = iterSw.next();
			System.out.print(tmpSw + "=>");
			if (tmpSw == srcSw) {
				System.out.print("Setup in PSwitch:");
				if (iterLink.hasNext())
					LinkOut = iterLink.next();
				else
					System.out.println("Fatal error");
				System.out.println("outport " + LinkOut.getPort(tmpSw) + "/lambda " + LinkOut.getLambda());
			} else if (tmpSw == dstSw) {
				System.out.print("Setup out PSwitch:");
				LinkIn = LinkOut;
				System.out.println("inport " + LinkIn.getPort(tmpSw) + "/lambda " + LinkIn.getLambda());
			} else {
				System.out.print("Setup CSwitch:");
				LinkIn = LinkOut;
				if (iterLink.hasNext())
					LinkOut = iterLink.next();
				else
					System.out.println("Fatal error");
				System.out.print("inport " + LinkIn.getPort(tmpSw) + "/lambda " + LinkIn.getLambda());
				System.out.println(";outport " + LinkOut.getPort(tmpSw) + "/lambda " + LinkOut.getLambda());
			}
		}
		
		return true;
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
		
		Flow flow = getFlow(srcSw, dstSw);
		if (flow != null) {
			Link newLink = new Link();
			newLink.setSwitch(srcSw, -1);
			newLink.setSwitch(dstSw, -1);
			mapLinkFlow.put(newLink, flow);
		}
		
		/* Send control messages */
		walkFlow(srcSw, dstSw, flow);
		
		return flow;
	}
	
	public static void main (String args[]) {
		PSwitch swL1 = new PSwitch();
		PSwitch swL2 = new PSwitch();
		PSwitch swR1 = new PSwitch();
		PSwitch swR2 = new PSwitch();
		CSwitch sw11 = new CSwitch();
		CSwitch sw12 = new CSwitch();
		CSwitch sw21 = new CSwitch();
		CSwitch sw22 = new CSwitch();
		NetworkEnv netEnv = new NetworkEnv();
		
		netEnv.addSwitch(swL1);
		netEnv.addSwitch(swL2);
		netEnv.addSwitch(swR1);
		netEnv.addSwitch(swR2);
		
		netEnv.addSwitch(sw11);
		netEnv.addSwitch(sw12);
		netEnv.addSwitch(sw21);
		netEnv.addSwitch(sw22);
		
		netEnv.connectSwitch(swL1, 1, sw11, 1);
		netEnv.connectSwitch(swL2, 1, sw21, 1);
		netEnv.connectSwitch(swR1, 1, sw12, 1);
		netEnv.connectSwitch(swR2, 1, sw22, 1);
		netEnv.connectSwitch(sw11, 2, sw12, 2);
		netEnv.connectSwitch(sw21, 2, sw22, 2);
		netEnv.connectSwitch(sw11, 3, sw21, 3);
		netEnv.connectSwitch(sw12, 3, sw22, 3);
		netEnv.connectSwitch(sw11, 4, sw22, 4);
		netEnv.connectSwitch(sw21, 4, sw12, 4);
		
		netEnv.setupCircuit(0, 2);
		//System.out.println(netEnv.getCurrCircuit(0, 2));
		netEnv.setupCircuit(1, 2);
		netEnv.setupCircuit(2, 3);
	}
}

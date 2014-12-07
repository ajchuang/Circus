package nonstar.controller;

import nonstar.basic.Flow;

public abstract class NetworkTopo {

	protected Controller constroller;
	
	public NetworkTopo(Controller constroller) {
		super();
		this.constroller = constroller;
	}
	
	public abstract Flow getCurrCircuit(int src, int dst);
	public abstract Flow setupCircuit(int src, int dst);

}

package nonstar.controller;

import nonstar.basic.Flow;
import nonstar.basic.Switch;

public abstract class NetworkTopo {

	protected Controller constroller;

	public NetworkTopo(Controller constroller) {
		super();
		this.constroller = constroller;
	}

	public abstract Flow getCurrCircuit(Switch srcSw, Switch dstSw);
	public abstract Flow setupCircuit(Switch srcSw, Switch dstSw);
	public abstract Flow tearCircuit(Switch srcSw, Switch dstSw);
}

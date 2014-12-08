package nonstar.controller;

import nonstar.basic.Flow;
import nonstar.basic.Switch;

public abstract class NonstarBase {
	
	protected NetworkTopo env;
	
	public NonstarBase(NetworkTopo env) {
		super();
		this.env = env;
	}
	
	public Flow getCurrCircuit(Switch src, Switch dst){
		return env.getCurrCircuit(src, dst);
	}
	
	public Flow setupCircuit(Switch src, Switch dst){
		return env.setupCircuit(src, dst);
	}
	
	public abstract void onstart();
	public abstract Flow onreq(Switch src, Switch dst);

}

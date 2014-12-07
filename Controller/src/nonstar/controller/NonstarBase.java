package nonstar.controller;

import nonstar.basic.Flow;
import nonstar.basic.Switch;

public abstract class NonstarBase {
	
	protected NetworkTopo env;
	
	public NonstarBase(NetworkTopo env) {
		super();
		this.env = env;
	}
	
	public abstract void on_start();
	public abstract Flow on_req(Switch src, Switch dst);

}

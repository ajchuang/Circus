package nonstar.controller;

import nonstar.basic.Flow;
import nonstar.basic.Switch;

public class NonstarTemplate extends NonstarBase {

	//variables
	
	//functions
	
	@Override
	public void on_start() {

		//on_start

	}

	@Override
	public Flow on_req(Switch src, Switch dst) {
		
		Flow flow = env.getCurrCircuit(src.getId(), dst.getId());
		if (flow == null)
			flow = env.setupCircuit(src.getId(), dst.getId());
		return flow;
		//return null;
	}
	
	public NonstarTemplate(NetworkTopo env) {
		super(env);	
	}

}

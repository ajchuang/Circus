package nonstar.controller;

import nonstar.basic.Flow;
import nonstar.basic.Switch;

public class NonstarTemplate extends NonstarBase {

	//variables

	//functions

	@Override
	public void onstart() {

		//on_start

	}

	@Override
	public Flow onreq(Switch src, Switch dst) {

		Flow flow = env.getCurrCircuit(src, dst);
		if (flow == null)
			flow = env.setupCircuit(src, dst);
		return flow;
		//return null;
	}

	public NonstarTemplate(NetworkTopo env) {
		super(env);
	}

}

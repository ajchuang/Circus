package nonstar.controller;

import nonstar.basic.Flow;
import nonstar.basic.Switch;

public class Nonstar extends NonstarBase {

	//variables
	
	//functions
	
	@Override
	public void on_start() {

		//on_start

	}

	@Override
	public Flow on_req(Switch src, Switch dst) {
		
		//on_req
		
		return null;
	}
	
	public Nonstar(NetworkTopo env) {
		super(env);	
	}

}

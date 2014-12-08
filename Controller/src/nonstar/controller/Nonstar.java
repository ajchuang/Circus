package nonstar.controller;

import java.util.HashMap;
import java.util.ArrayList;

import nonstar.basic.Flow;
import nonstar.basic.Switch;

public class Nonstar extends NonstarBase {

	boolean flag = true;
	public void on_start()  {
		
	}
	public Flow on_req(Switch src, Switch dst)  {
		Flow f = getCurrCircuit(src, dst);
		if(f == null) {
			f = setupCircuit(src, dst);

		}

		return f;

	}
	
	
	@Override
	public void onstart() {
		on_start();
	}

	@Override
	public Flow onreq(Switch src, Switch dst) {
		return on_req(src, dst);
	}
	
	public Nonstar(NetworkTopo env) {
		super(env);	
	}

}

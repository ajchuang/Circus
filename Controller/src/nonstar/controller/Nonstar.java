package nonstar.controller;

import java.util.HashMap;
import java.util.ArrayList;

import nonstar.basic.Flow;
import nonstar.basic.Switch;

public class Nonstar extends NonstarBase {

	HashMap<Switch,Integer> srcCount = new HashMap<Switch,Integer>();
	HashMap<Switch,Integer> dstCount = new HashMap<Switch,Integer>();
	public void on_start()  {
		int i = 0;

	}
	public int getSwitchCount(Switch sw)  {
		int res = 0;
		if(srcCount.containsKey(sw)) {
			res = res + srcCount.get(sw);

		}

		if(dstCount.containsKey(sw)) {
			res = res + dstCount.get(sw);

		}

		return res;

	}
	public void incrementSwichCount(HashMap<Switch,Integer> map, Switch sw)  {
		if(map.containsKey(sw)) {
			map.put(sw, 1);

		}

		else {
			map.put(sw, map.get(sw) + 1);

		}


	}
	public Flow on_req(Switch src, Switch dst)  {
		Flow f = getCurrCircuit(src, dst);
		if(f == null) {
			if(getSwitchCount(src) < 3 || getSwitchCount(dst) < 3) {
			f = setupCircuit(src, dst);

		}


		}

		if(f != null) {
			incrementSwichCount(srcCount, src);
		incrementSwichCount(dstCount, dst);

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

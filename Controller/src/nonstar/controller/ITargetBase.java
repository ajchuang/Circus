package nonstar.controller;

import nonstar.basic.Flow;
import nonstar.basic.Switch;

public interface ITargetBase {
	
	public void on_start();
	public Flow on_req(Switch from, Switch to);

}

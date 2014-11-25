package nonstar.interpreter;

import nonstar.basic.Flow;

public interface NetworkTopo {
	
	public Flow getCurrCircuit(int src, int dst);
	public Flow setupCircuit(int src, int dst);

}

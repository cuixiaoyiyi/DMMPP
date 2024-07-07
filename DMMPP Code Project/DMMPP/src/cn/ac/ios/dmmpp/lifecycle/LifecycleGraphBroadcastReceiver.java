package cn.ac.ios.dmmpp.lifecycle;

import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants;

public class LifecycleGraphBroadcastReceiver extends AbstractLifecycleGraph {

	
	public LifecycleGraphBroadcastReceiver() {
		super();
		initGraph();
	}

	private void initGraph() {
		Node onReceive = new Node(AndroidEntryPointConstants.BROADCAST_ONRECEIVE);
		Node bottom = new Node(null);
		head.setFirstSucc(onReceive);
		head.setFirstSucc(bottom);
		onReceive.setFirstSucc(onReceive);
		onReceive.setSecondSucc(bottom);
		nodes.add(onReceive);
		nodes.add(bottom);
	}

}

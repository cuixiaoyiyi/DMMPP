package cn.ac.ios.dmmpp.lifecycle;

import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants;

public class LifecycleGraphContentProvider extends AbstractLifecycleGraph {

	
	public LifecycleGraphContentProvider() {
		super();
		initGraph();
	}

	private void initGraph() {
		Node onCreate = new Node(AndroidEntryPointConstants.CONTENTPROVIDER_ONCREATE);
		Node bottom = new Node(null);
		head.setFirstSucc(onCreate);
		head.setFirstSucc(bottom);
		onCreate.setFirstSucc(onCreate);
		onCreate.setSecondSucc(bottom);
		nodes.add(onCreate);
		nodes.add(bottom);
	}

}

package cn.ac.ios.dmmpp.lifecycle;

import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants;

public class LifecycleGraphService extends AbstractLifecycleGraph {
	
	public LifecycleGraphService() {
		super();
		initGraph();
	}

	private void initGraph() {
		Node onCreate = new Node(AndroidEntryPointConstants.SERVICE_ONCREATE);	
		Node onStart1 = new Node(AndroidEntryPointConstants.SERVICE_ONSTART1);	
		Node onStart2 = new Node(AndroidEntryPointConstants.SERVICE_ONSTART2);
		Node onBind = new Node(AndroidEntryPointConstants.SERVICE_ONBIND);
		Node onUnBind = new Node(AndroidEntryPointConstants.SERVICE_ONUNBIND);
		Node onReBind = new Node(AndroidEntryPointConstants.SERVICE_ONREBIND);
		Node onDestroy = new Node(AndroidEntryPointConstants.SERVICE_ONDESTROY);
		Node bottom = new Node(null);
		
		head.setFirstSucc(onCreate);
		head.setSecondSucc(bottom);
		
		onCreate.setFirstSucc(onStart1);
		onCreate.setSecondSucc(onStart2);
		
		onStart1.setFirstSucc(onStart2);
		onStart1.setSecondSucc(onBind);
		
		onStart2.setFirstSucc(onBind);
		onStart2.setSecondSucc(onStart2);

		onBind.setFirstSucc(onUnBind);
		
		onUnBind.setFirstSucc(onReBind);
		onUnBind.setSecondSucc(onDestroy);
		
		onReBind.setFirstSucc(onDestroy);
		onReBind.setSecondSucc(onBind);
		
		onDestroy.setFirstSucc(bottom);

		nodes.add(onCreate);
		nodes.add(onStart1);
		nodes.add(onStart2);
		nodes.add(onBind);
		nodes.add(onUnBind);
		nodes.add(onReBind);
		nodes.add(onDestroy);
		nodes.add(bottom);
	}

}

package cn.ac.ios.dmmpp.lifecycle;

import java.util.Collection;

import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants;

public class LifecycleGraphActivity extends AbstractLifecycleGraph {
	
	public LifecycleGraphActivity(Collection<String> callbacks) {
		super();
		setCallbacks(callbacks);
		initGraph();
	}

	private void initGraph() {
		Node onCreate = new Node(AndroidEntryPointConstants.ACTIVITY_ONCREATE);	
		Node onStart = new Node(AndroidEntryPointConstants.ACTIVITY_ONSTART);	
		Node onRestoreInstanceState = new Node(AndroidEntryPointConstants.ACTIVITY_ONRESTOREINSTANCESTATE); // optional		
		Node onPostCreate = new Node(AndroidEntryPointConstants.ACTIVITY_ONPOSTCREATE);  
		Node onResume = new Node(AndroidEntryPointConstants.ACTIVITY_ONRESUME);
		Node onPostResume = new Node(AndroidEntryPointConstants.ACTIVITY_ONPOSTRESUME);
		Node onPause = new Node(AndroidEntryPointConstants.ACTIVITY_ONPAUSE);
		Node onCreateDescription = new Node(AndroidEntryPointConstants.ACTIVITY_ONCREATEDESCRIPTION);
		Node onSaveInstanceState = new Node(AndroidEntryPointConstants.ACTIVITY_ONSAVEINSTANCESTATE);
		Node onStop = new Node(AndroidEntryPointConstants.ACTIVITY_ONSTOP);
		Node onRestart = new Node(AndroidEntryPointConstants.ACTIVITY_ONRESTART);
		Node onDestroy = new Node(AndroidEntryPointConstants.ACTIVITY_ONDESTROY);
		Node bottom = new Node(null);
		
		head.setFirstSucc(onCreate);
		head.setSecondSucc(bottom);
		onCreate.setFirstSucc(onStart);
		onStart.setFirstSucc(onRestoreInstanceState);
		onStart.setSecondSucc(onPostCreate);
		onRestoreInstanceState.setFirstSucc(onPostCreate);
		onPostCreate.setFirstSucc(onResume);
		onResume.setFirstSucc(onPostResume);
		Node callBack = null;
		if(callbacks !=null && !callbacks.isEmpty()) {
			callBack = new Node(null);
			callBack.setCallbacks(callbacks);
			onPostResume.setFirstSucc(callBack);
			callBack.setFirstSucc(onPause);
			callBack.setSecondSucc(callBack);
		}else {
			onPostResume.setFirstSucc(onPause);
		}
		onPause.setFirstSucc(onCreateDescription);
		onCreateDescription.setFirstSucc(onSaveInstanceState);
		onCreateDescription.setSecondSucc(onStop);
		onStop.setFirstSucc(onRestart);
		onStop.setSecondSucc(onDestroy);
		onRestart.setFirstSucc(onStart);
		onDestroy.setFirstSucc(bottom);
		
		nodes.add(onCreate);
		nodes.add(onStart);
		nodes.add(onRestoreInstanceState);
		nodes.add(onPostCreate);
		nodes.add(onResume);
		nodes.add(onPostResume);
		if(callBack!=null) {
			nodes.add(callBack);
		}
		nodes.add(onPause);
		nodes.add(onCreateDescription);
		nodes.add(onSaveInstanceState);
		nodes.add(onStop);
		nodes.add(onRestart);
		nodes.add(onDestroy);
		nodes.add(bottom);
	}

	@Override
	public Collection<String> getCallbacks() {
		return callbacks;
	}

	@Override
	public void setCallbacks(Collection<String> callbacks) {
		this.callbacks = callbacks;
	}

}

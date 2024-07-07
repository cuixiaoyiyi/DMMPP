package cn.ac.ios.dmmpp.lifecycle;

import java.util.Collection;

import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants;

public class LifecycleGraphFragment extends AbstractLifecycleGraph {

	public LifecycleGraphFragment() {
		super();
		initGraph();
	}

	private void initGraph() {
		Node onAttach = new Node(AndroidEntryPointConstants.FRAGMENT_ONATTACH);
		Node onCreate = new Node(AndroidEntryPointConstants.FRAGMENT_ONCREATE);
		Node onCreateView = new Node(AndroidEntryPointConstants.FRAGMENT_ONCREATEVIEW);
		Node onViewCreated = new Node(AndroidEntryPointConstants.FRAGMENT_ONVIEWCREATED);
		Node onActivityCreated = new Node(AndroidEntryPointConstants.FRAGMENT_ONACTIVITYCREATED);
		Node onStart = new Node(AndroidEntryPointConstants.FRAGMENT_ONSTART);
		Node onResume = new Node(AndroidEntryPointConstants.FRAGMENT_ONRESUME);
		Node onPause = new Node(AndroidEntryPointConstants.FRAGMENT_ONPAUSE);
		Node callBack = null;
		if (callbacks != null && !callbacks.isEmpty()) {
			callBack = new Node(null);
			callBack.setCallbacks(callbacks);
			onResume.setFirstSucc(callBack);
			callBack.setFirstSucc(onPause);
			callBack.setSecondSucc(callBack);
		} else {
			onResume.setFirstSucc(onPause);
		}
		Node onSaveInstanceState = new Node(AndroidEntryPointConstants.FRAGMENT_ONSAVEINSTANCESTATE);
		Node onStop = new Node(AndroidEntryPointConstants.FRAGMENT_ONSTOP);
		Node nopAfterOnStop = new Node(null);
		Node onDestroyView = new Node(AndroidEntryPointConstants.FRAGMENT_ONDESTROYVIEW);
		Node onDestroy = new Node(AndroidEntryPointConstants.ACTIVITY_ONDESTROY);
		Node onDetach = new Node(AndroidEntryPointConstants.ACTIVITY_ONDESTROY);
		Node bottom = new Node(null);

		head.setFirstSucc(onAttach);
		head.setSecondSucc(bottom);

		onAttach.setFirstSucc(onCreate);
		onCreate.setFirstSucc(onCreateView);
		onCreateView.setFirstSucc(onViewCreated);
		onViewCreated.setFirstSucc(onActivityCreated);
		onActivityCreated.setFirstSucc(onStart);
		onResume.setFirstSucc(onResume);

		onPause.setFirstSucc(onSaveInstanceState);
		onSaveInstanceState.setFirstSucc(onStop);

		onStop.setFirstSucc(onCreateView);
		onStop.setSecondSucc(nopAfterOnStop);

		nopAfterOnStop.setFirstSucc(onStart);
		nopAfterOnStop.setSecondSucc(onDestroyView);

		onDestroyView.setFirstSucc(onDestroy);
		onDestroyView.setSecondSucc(onCreateView);

		onDestroy.setFirstSucc(onDetach);

		onDetach.setFirstSucc(bottom);
		onDetach.setSecondSucc(onAttach);

		nodes.add(onAttach);
		nodes.add(onCreate);
		nodes.add(onCreateView);
		nodes.add(onViewCreated);
		nodes.add(onActivityCreated);
		nodes.add(onStart);
		nodes.add(onResume);
		nodes.add(onPause);
		if (callBack != null) {
			nodes.add(callBack);
		}
		nodes.add(onSaveInstanceState);
		nodes.add(onStop);
		nodes.add(nopAfterOnStop);
		nodes.add(onDestroyView);
		nodes.add(onDestroy);
		nodes.add(onDetach);
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

package cn.ac.ios.dmmpp.lifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractLifecycleGraph implements LifecycleGraph {
	
	protected Node head = new Node(null);
	protected List<Node> nodes = new ArrayList<>();
	protected Collection<String> callbacks = null;
	
	protected AbstractLifecycleGraph() {
		head.setHead(true);
		nodes.add(head);
	}

	@Override
	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	public Node getHead() {
		return head;
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

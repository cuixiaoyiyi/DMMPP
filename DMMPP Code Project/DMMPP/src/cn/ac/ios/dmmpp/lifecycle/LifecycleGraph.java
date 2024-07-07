package cn.ac.ios.dmmpp.lifecycle;

import java.util.Collection;
import java.util.List;

public interface LifecycleGraph {
	
	public List<Node> getNodes();
	
	public Node getHead();
	
	public Collection<String> getCallbacks();
	
	public void setCallbacks(Collection<String> callbacks);

}

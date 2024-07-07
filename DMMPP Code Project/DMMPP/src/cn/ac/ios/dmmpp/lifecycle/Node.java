package cn.ac.ios.dmmpp.lifecycle;

import java.util.Collection;

public class Node {

	String methodSubSig = null;
	Node firstSucc = null;
	Node secondSucc = null;
	boolean isHead = false;
	Collection<String> callbacks = null;

	public Node(String methodSubSig) {
		this.methodSubSig = methodSubSig;
	}

	public boolean isReturnNode() {
		return firstSucc == null && secondSucc == null;
	}

	public boolean isBranchNode() {
		return secondSucc != null;
	}

	public boolean isHead() {
		return isHead;
	}

	public void setHead(boolean isHead) {
		this.isHead = isHead;
	}
	
	public Node getFirstSucc() {
		return firstSucc;
	}
	
	public Node getSecondSucc() {
		return secondSucc;
	}

	public void setFirstSucc(Node firstSucc) {
		this.firstSucc = firstSucc;
	}

	public void setSecondSucc(Node secondSucc) {
		this.secondSucc = secondSucc;
	}

	public String getMethodSubSig() {
		return methodSubSig;
	}

	public boolean isNopNode() {
		return methodSubSig == null || methodSubSig.isEmpty();
	}
	
	public void setCallbacks(Collection<String> callbacks) {
		this.callbacks = callbacks;
	}
	
	public boolean isCallbackNode() {
		return callbacks !=null && !callbacks.isEmpty();
	}
	
	public Collection<String> getCallbacks() {
		return callbacks;
	}

}

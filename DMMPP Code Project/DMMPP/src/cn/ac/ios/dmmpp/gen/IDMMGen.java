package cn.ac.ios.dmmpp.gen;

import soot.SootMethod;

public interface IDMMGen {

	public SootMethod createDDM();
	
	public void addNonExplicitlyInheritedLifecycleMethod();
	
	public void generateComponentLifecycle();
	
}

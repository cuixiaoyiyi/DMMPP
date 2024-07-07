package cn.ac.ios.dmmpp.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.ios.dmmpp.lifecycle.LifecycleGraph;
import cn.ac.ios.dmmpp.lifecycle.Node;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.VoidType;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.Local;
import soot.LocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;

public class DMMGen extends AbstractDMMGen {
	
	Map<Node, List<Stmt>> map = new HashMap<>();

	DMMGen(SootClass component, LifecycleGraph graph) {
		this.component =component;
		this.graph = graph;
		for (Node node : graph.getNodes()) {
			if(!node.isNopNode()) {
				mArgList.addAll(IInstrumentation.findMethod(component, node.getMethodSubSig()).getParameterTypes());			
			}else if(node.isCallbackNode()) {
				for (String sig : node.getCallbacks()) {
					SootMethod sootMethod = Scene.v().getMethod(sig);
					SootMethod constructor = getConstructor(sootMethod.getDeclaringClass());
					if(constructor != null) {
						mArgList.addAll(constructor.getParameterTypes());
						mArgList.addAll(sootMethod.getParameterTypes());
					}
					
					
				}
			}
		}
	}
	
	@Override
	public void addNonExplicitlyInheritedLifecycleMethod() {	
		for (Node node : graph.getNodes()) {
			if(!node.isNopNode()) {
				addLifeCycleMethod(node.getMethodSubSig(), component, component.getSuperclass());
			}	
		}
	}

	@Override
	public void generateComponentLifecycle() {
		generateInvokeStmtForEachNode();
		generateGotoStmt();

	}
	
	private void generateGotoStmt() {
		for (Node node : graph.getNodes()) {
			if(!node.isReturnNode() && !node.isHead()) {
				int index1 = findBlock(map.get(node));
				List<Stmt> succ = stmts.get(index1+1);
				boolean isNext = succ.equals(map.get(node.getFirstSucc()));
				if(!isNext && node.isBranchNode()) {
					isNext = succ.equals(map.get(node.getSecondSucc()));
				}
				if(!isNext) {
					map.get(node).add(Jimple.v().newGotoStmt(map.get(node.getFirstSucc()).get(0)));
				}
			}
		}
		
		for (Node node : graph.getNodes()) {
			if(node.isBranchNode()) {
				Node succ = node.getFirstSucc();
				int index1 = findBlock(map.get(node));
				int index2 = findBlock(map.get(succ));
				if(index2 == (index1 + 1) ) {
					succ = node.getSecondSucc();
				}
				List<Stmt> ifstmts = createIfStmt(map.get(succ).get(0));
				map.get(node).addAll(ifstmts);
			}
		}
		
		
	}
	
	private int findBlock(List<Stmt> list) {
		for(int i=0;i<stmts.size();i++) {
			if(list.equals(stmts.get(i))) {
				return i;
			}
		}
		return -1;
	}

	private void generateInvokeStmtForEachNode() {
		for (Node node : graph.getNodes()) {
			List<Stmt> list = new ArrayList<>();
			if(!node.isNopNode()) {
				Stmt stmt = searchAndBuildMethod(node.getMethodSubSig(), component, thisLocal);
				list.add(stmt);
				map.put(node, list);
				stmts.add(list);
			}else if(node.isReturnNode()) {
				Stmt stmt = Jimple.v().newReturnStmt(thisLocal);
				list.add(stmt);
				map.put(node, list);
				stmts.add(list);
			}else if(node.isCallbackNode()) {
				for(String callback:node.getCallbacks()) {
					SootMethod sootMethod = Scene.v().getMethod(callback);
					SootMethod constructor = getConstructor(sootMethod.getDeclaringClass());
					if(constructor != null) {
						Local caller = mLocalGenerator.generateLocal(sootMethod.getDeclaringClass().getType());
						AssignStmt newAssignStmt = Jimple.v().newAssignStmt(caller, Jimple.v().newNewExpr(sootMethod.getDeclaringClass().getType()));
						Stmt initStmt = searchAndBuildMethod(constructor.getSubSignature(), sootMethod.getDeclaringClass(), caller);
						Stmt callbackInvocation = searchAndBuildMethod(sootMethod.getSubSignature(), sootMethod.getDeclaringClass(), caller);
						if(initStmt != null && callbackInvocation!=null) {
							list.add(newAssignStmt);
							list.add(initStmt);
							list.add(callbackInvocation);
						}else {
							pIndex += constructor.getParameterCount();
							pIndex += sootMethod.getParameterCount();
						}
						
					}
				}
				map.put(node, list);
				stmts.add(list);
			}else if(node.isHead()) {
				SootMethod constructor = getConstructor(component);
				if(constructor != null) {
					list.add(Jimple.v().newAssignStmt(thisLocal, Jimple.v().newNewExpr(component.getType())));
					list.add(searchAndBuildMethod(constructor.getSubSignature(), component, thisLocal));
				}
				map.put(node, list);
				stmts.add(list);
			}
		}
	}

	private void addLifeCycleMethod(String methodName,SootClass myClass ,SootClass libClass) {
		SootMethod sootMethod = IInstrumentation.findMethod(myClass, methodName);
		if(sootMethod.getDeclaringClass().equals(myClass)) { // contains the method already
			return;
		}
		SootMethod superMethod = null;
		try {
			superMethod = libClass.getMethodUnsafe(methodName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			Log.println("we cannot find lifecycle method ", methodName, " in class ", libClass.getName(),
//					". Maybe the method wasnot existed in current Android level");
			// e.printStackTrace();
		}
		if (superMethod == null) {
			new Exception("superMethod == null").printStackTrace();
			return;
		}
		if (sootMethod == null || sootMethod.getSignature().equals(superMethod.getSignature())) {
			sootMethod = Scene.v().makeSootMethod(superMethod.getName(), superMethod.getParameterTypes(),
					superMethod.getReturnType(), superMethod.getModifiers());
			myClass.addMethod(sootMethod);
//			mainClass.addMethod(mainMethod);
			JimpleBody jimpleBody = Jimple.v().newBody(sootMethod);
			jimpleBody.insertIdentityStmts();
			sootMethod.setActiveBody(jimpleBody);
			
			LocalGenerator localGenerator = new DefaultLocalGenerator(jimpleBody); 

			final InvokeExpr invokeExpr = Jimple.v().newSpecialInvokeExpr(jimpleBody.getThisLocal(), superMethod.makeRef(),
					jimpleBody.getParameterLocals());
			if(superMethod.getReturnType() instanceof VoidType) {
				Stmt stmt = Jimple.v().newInvokeStmt(invokeExpr);
				jimpleBody.getUnits().add(stmt);
				jimpleBody.getUnits().add(Jimple.v().newReturnVoidStmt());
			}else {
				Local returnLocal = localGenerator.generateLocal(superMethod.getReturnType());
				Stmt stmt = Jimple.v().newAssignStmt(returnLocal, invokeExpr);
				jimpleBody.getUnits().add(stmt);
				jimpleBody.getUnits().add(Jimple.v().newReturnStmt(returnLocal));
			}
		}
	}

}

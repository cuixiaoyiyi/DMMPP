package cn.ac.ios.dmmpp.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import soot.Local;
import soot.LocalGenerator;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.Jimple;
import soot.jimple.NopStmt;
import soot.jimple.Stmt;

public interface IInstrumentation {
	
	List<Stmt> createIfStmt(Stmt target);
	
	Stmt searchAndBuildMethod(String subsignature, SootClass currentClass, Local classLocal);
	
	default NopStmt createInvokeNopStmt() {return Jimple.v().newNopStmt();};
	
	void insertAfter(List<Stmt> former, List<Stmt> latter);

	public static SootMethod findMethod(SootClass currentClass, String subsignature) {
		SootMethod m = currentClass.getMethodUnsafe(subsignature);
		if (m != null) {
			return m;
		}
		if (currentClass.hasSuperclass()) {
			return findMethod(currentClass.getSuperclass(), subsignature);
		}
		return null;
	}

	Stmt buildMethodCall(SootMethod methodToCall, Local classLocal, Set<SootClass> parentClasses);
	
	LocalGenerator getLocalGenerator();
	
//	Map<String,Integer> getArgTypeMap();
	
	Local getParameterLocal(int index);

	List<Value> getArgumentsFromParameters(SootMethod methodToCall);

	Local getValueForType(Type tp);
	
	Local getPredicateArray();
	
	default SootMethod getConstructor(SootClass sootClass) {
		List<SootMethod> sootMethods = new ArrayList<>(sootClass.getMethods());
		for (SootMethod sootMethod : sootMethods) {
			if(sootMethod.isConstructor()) {
				return sootMethod;
			}
		}
		return null;
	}

}

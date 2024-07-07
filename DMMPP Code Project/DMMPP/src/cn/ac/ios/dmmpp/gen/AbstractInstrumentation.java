package cn.ac.ios.dmmpp.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.BooleanType;
import soot.Local;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.ArrayRef;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants;

public abstract class  AbstractInstrumentation implements IInstrumentation {

	protected int predicateCounter = 0;

	protected List<List<Stmt>> stmts = new ArrayList<>();

	private Set<SootMethod> failedMethods = new HashSet<>();
	
	protected List<Type> mArgList = new ArrayList<>();

	@Override
	public List<Stmt> createIfStmt(Stmt target) {
		if (target == null) {
			return null;
		}
		List<Stmt> stmts = new ArrayList<>();
		final Jimple jimple = Jimple.v();
		ArrayRef predicate = jimple.newArrayRef(getPredicateArray(), IntConstant.v(predicateCounter++));
		Local local = getLocalGenerator().generateLocal(BooleanType.v());
		stmts.add(jimple.newAssignStmt(local, predicate));
		
		ValueBox cond = jimple.newConditionExprBox(jimple.newEqExpr(local, IntConstant.v(0)));
//				newEqExpr(predicate, IntConstant.v(0));
		stmts.add(jimple.newIfStmt(cond.getValue(), target));
		return stmts;
	}

	@Override
	public void insertAfter(List<Stmt> former, List<Stmt> latter) {
		int i =-1;
		for (i = 0; i < stmts.size(); i++) {
			if (former.equals(stmts.get(i))) {
				break;
			}
		}
		stmts.add(i+1, latter);
	}

	@Override
	public Stmt searchAndBuildMethod(String subsignature, SootClass currentClass, Local classLocal) {
		return searchAndBuildMethod(subsignature, currentClass, classLocal, Collections.<SootClass>emptySet());
	}
	
	private Stmt searchAndBuildMethod(String subsignature, SootClass currentClass, Local classLocal,
			Set<SootClass> parentClasses) {
		if (currentClass == null || classLocal == null) {
			return null;
		}

		SootMethod method = IInstrumentation.findMethod(currentClass, subsignature);
		if (method == null) {
			return null;
		}

		// If the method is in one of the predefined Android classes, it cannot
		// contain custom code, so we do not need to call it
		if (AndroidEntryPointConstants.isLifecycleClass(method.getDeclaringClass().getName())) {
			return null;
		}

		assert method.isStatic() || classLocal != null : "Class local was null for non-static method "
				+ method.getSignature();

		// write Method
		return buildMethodCall(method, classLocal, parentClasses);
	}
	
	@Override
	public Stmt buildMethodCall(SootMethod methodToCall, Local classLocal,
			Set<SootClass> parentClasses) {
		//Log.e("buildMethodCall++methodToCall",null);
		// If we don't have a method, we cannot call it (sad but true)
		if (methodToCall == null) {
			
			return null;
		}

		if (classLocal == null && !methodToCall.isStatic()) {
//			Log.e("Cannot call method {}, because there is no local for base object: {}", methodToCall,
//					methodToCall.getDeclaringClass());
			failedMethods .add(methodToCall);
			//Log.e("buildMethodCall++classLocal == null && !methodToCall.isStatic()");
			return null;
		}

		final InvokeExpr invokeExpr;
		List<Value> args = new LinkedList<Value>();
		if (methodToCall.getParameterCount() > 0) {
			args = getArgumentsFromParameters(methodToCall);

			if (methodToCall.isStatic())
				invokeExpr = Jimple.v().newStaticInvokeExpr(methodToCall.makeRef(), args);
			else {
				assert classLocal != null : "Class local method was null for non-static method call";
				if (methodToCall.isConstructor()) {
					invokeExpr = Jimple.v().newSpecialInvokeExpr(classLocal, methodToCall.makeRef(), args);
				} else {
					if (classLocal.getType() instanceof RefType
							&& ((RefType) classLocal.getType()).getSootClass().isInterface()) {
						invokeExpr = Jimple.v().newInterfaceInvokeExpr(classLocal, methodToCall.makeRef(), args);
					} else {
						invokeExpr = Jimple.v().newVirtualInvokeExpr(classLocal, methodToCall.makeRef(), args);
					}
				}
			}
		} else {
			if (methodToCall.isStatic()) {
				invokeExpr = Jimple.v().newStaticInvokeExpr(methodToCall.makeRef());
			} else {
				assert classLocal != null : "Class local method was null for non-static method call";
				if (methodToCall.isConstructor()) {
					invokeExpr = Jimple.v().newSpecialInvokeExpr(classLocal, methodToCall.makeRef());
				} else {
					if (classLocal.getType() instanceof RefType
							&& ((RefType) classLocal.getType()).getSootClass().isInterface()) {
						invokeExpr = Jimple.v().newInterfaceInvokeExpr(classLocal, methodToCall.makeRef(), args);
					} else {
						invokeExpr = Jimple.v().newVirtualInvokeExpr(classLocal, methodToCall.makeRef(), args);
					}
				}
			}
		}

		Stmt stmt;
		if (!(methodToCall.getReturnType() instanceof VoidType)) {
			Local returnLocal = getLocalGenerator().generateLocal(methodToCall.getReturnType());
			stmt = Jimple.v().newAssignStmt(returnLocal, invokeExpr);

		} else {
			stmt = Jimple.v().newInvokeStmt(invokeExpr);
		}
		//Log.e("buildMethodCall",stmt);

		// Clean up. If we re-use parent objects, do not destroy those. We can
		// only clean up what we have created.
//		for (Value val : args)
//			if (val instanceof Local && val.getType() instanceof RefType) {
//				if (!parentClasses.contains(((RefType) val.getType()).getSootClass())) {
////					body.getUnits().add(Jimple.v().newAssignStmt(val, NullConstant.v()));
//					localVarsForClasses.remove(((RefType) val.getType()).getSootClass());
//				}
//			}

		return stmt;
	}
	
	protected int pIndex = 0;

	@Override
	public List<Value> getArgumentsFromParameters(SootMethod methodToCall) {
		List<Value> args = new LinkedList<Value>();
		for(int i=0;i<methodToCall.getParameterCount();i++) {
//			args.add(getPredicateArray());
//			args.add(methodToCall.getParameterType(pIndex + i));
			args.add(getParameterLocal(pIndex + i));
			pIndex ++;
//			String key = getTypeKey(methodToCall,methodToCall.getParameterType(i),pIndex++);
//			Set<SootClass> constructionStack = new HashSet<SootClass>();
//			constructionStack.add(methodToCall.getDeclaringClass());
//			if(getArgTypeMap().keySet().contains(key)) {
//				args.add(getParameterLocal(getArgTypeMap().get(key)));
//			}else {
//				args.add(getValueForType(methodToCall.getParameterType(i)));
//			}
		}
		return args;
	}
	
	protected String getTypeKey(SootMethod sootMethod, Type type, int i) {
		return sootMethod.getSignature() + type + i;
	}
	
	

}

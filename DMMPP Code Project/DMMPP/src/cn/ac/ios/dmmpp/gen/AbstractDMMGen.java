package cn.ac.ios.dmmpp.gen;

import cn.ac.ios.dmmpp.lifecycle.LifecycleGraph;
import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.Local;
import soot.LocalGenerator;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;

public abstract class AbstractDMMGen extends AbstractInstrumentation implements IDMMGen {
	
	private static final String METHOD_NAME = "DMMPP";
	
	protected SootClass component = null;
	
	protected LifecycleGraph graph = null;
	
	protected SootMethod ddm = null; // dummy main method
	
	protected Body  mBody = null;
	
//	protected Map<String,Integer> mArgTypeMap = new HashMap<>();
	
	protected Local thisLocal = null;
	
	protected Local predicateArray = null;
	
	protected LocalGenerator mLocalGenerator;

	@Override
	public SootMethod createDDM() {
		if(ddm == null) {
			addNonExplicitlyInheritedLifecycleMethod();
			createEmptyDDM();
			predicateArray =  getParameterLocal(mArgList.size() - 1);
			generateComponentLifecycle();
			for(int i=0;i<stmts.size();i++) {
				mBody.getUnits().addAll(stmts.get(i));
			}
			
		}
		return ddm;
	}

	private void createEmptyDDM() {

		// Remove the existing main method if necessary. Do not clear the
		// existing one, this would take much too long.
		ddm = component.getMethodByNameUnsafe(METHOD_NAME);
		if (ddm != null) {
			component.removeMethod(ddm);
			ddm = null;
		}

		final SootClass intentClass = Scene.v().getSootClassUnsafe("android.content.Intent");
		if (intentClass == null)
			throw new RuntimeException("Could not find Android intent class");

		// Create the method
//		mArgList.add(RefType.v(intentClass));
		mArgList.add(ArrayType.v(BooleanType.v(), 1));
		
		ddm = Scene.v().makeSootMethod(METHOD_NAME, mArgList, component.getType());

		// Create the body
		mBody = Jimple.v().newBody();
		mBody.setMethod(ddm);
		ddm.setActiveBody(mBody);
		
		mLocalGenerator = new DefaultLocalGenerator(mBody);

		// Add the method to the class
		component.addMethod(ddm);
//		component.addMethod(ddm);

		// First add class to scene, then make it an application class
		// as addClass contains a call to "setLibraryClass"
		component.setApplicationClass();
		ddm.setModifiers(Modifier.PUBLIC | Modifier.STATIC);

		// Add the identity statements to the body. This must be done after the
		// method has been properly declared.
		((JimpleBody) mBody).insertIdentityStmts();
		
		// Create a new instance of the component
		
//		thisLocal = generateClassConstructor(component);
		thisLocal = mLocalGenerator.generateLocal(component.getType());
	}

	@Override
	public Local getPredicateArray() {
		return predicateArray;
	}
	
	@Override
	public LocalGenerator getLocalGenerator() {
		return mLocalGenerator;
	}

//	@Override
//	public Map<String, Integer> getArgTypeMap() {
//		return mArgTypeMap;
//	}

	@Override
	public Local getParameterLocal(int index) {
		return mBody.getParameterLocal(index);
	}

	private int parameterIndex = 0;
	
	@Override
	public Local getValueForType(Type tp) {
		return mBody.getParameterLocal(parameterIndex++%mBody.getParameterLocals().size());
	}

}

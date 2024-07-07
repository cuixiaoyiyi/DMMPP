package cn.ac.ios.dmmpp.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.ios.dmmpp.AndroidCallBacks;
import cn.ac.ios.dmmpp.lifecycle.LifecycleGraphActivity;
import cn.ac.ios.dmmpp.lifecycle.LifecycleGraphBroadcastReceiver;
import cn.ac.ios.dmmpp.lifecycle.LifecycleGraphContentProvider;
import cn.ac.ios.dmmpp.lifecycle.LifecycleGraphFragment;
import cn.ac.ios.dmmpp.lifecycle.LifecycleGraph;
import cn.ac.ios.dmmpp.lifecycle.LifecycleGraphService;
import soot.ArrayType;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants;
import soot.util.MultiMap;

public class DMMFactory {

	public static SootMethod createDDM(SootClass component) {
		
		LifecycleGraph graph = null;
		
		if(isActivity(component)) {
			Set<SootClass> invokedClasses = AndroidCallBacks.getSootClassesInvoked(component, null, null).mInvokedSootClasses;

			MultiMap<SootClass, SootMethod> callbackClasses = AndroidCallBacks.getCallBackMultiMap(invokedClasses);
			List<String> callbacks = new ArrayList<>();
			for (SootClass listenerClass : callbackClasses.keySet()) {
				for(SootMethod sootMethod: callbackClasses.get(listenerClass)) {
					callbacks.add(sootMethod.getSignature());
				}
			}
			handleLayoutXml();
			graph = new LifecycleGraphActivity(callbacks);
			
		}else if(isFragment(component)) {
			graph = new LifecycleGraphFragment();
		}else if(isService(component)) {
			graph = new LifecycleGraphService();
		}else if(isBroadcastReceiver(component)) {
			graph = new LifecycleGraphBroadcastReceiver();
		}else if(isContentProvider(component)) {
			graph = new LifecycleGraphContentProvider();
		}
		if(graph!=null) {
			return (new DMMGen(component, graph)).createDDM();
		}
		return null;
	}

	private static boolean isActivity(SootClass component) {
		return isClassSubclassOf(component, AndroidEntryPointConstants.ACTIVITYCLASS)
				|| isClassSubclassOf(component, AndroidEntryPointConstants.APPCOMPATACTIVITYCLASS_V4)
				|| isClassSubclassOf(component, AndroidEntryPointConstants.APPCOMPATACTIVITYCLASS_V7);
	}
	
	private static boolean isFragment(SootClass component) {
		return isClassSubclassOf(component, AndroidEntryPointConstants.FRAGMENTCLASS)
				|| isClassSubclassOf(component, AndroidEntryPointConstants.SUPPORTFRAGMENTCLASS)
				|| isClassSubclassOf(component, "android.support.v7.app.Fragment");
	}
	
	private static boolean isService(SootClass component) {
		return isClassSubclassOf(component, AndroidEntryPointConstants.SERVICECLASS);
	}
	
	private static boolean isBroadcastReceiver(SootClass component) {
		return isClassSubclassOf(component, AndroidEntryPointConstants.BROADCASTRECEIVERCLASS);
	}
	
	private static boolean isContentProvider(SootClass component) {
		return isClassSubclassOf(component, AndroidEntryPointConstants.CONTENTPROVIDERCLASS);
	}

	private static boolean isClassSubclassOf(SootClass child, String parent) {
		SootClass parentClass = Scene.v().getSootClass(parent);
		return isInheritedFromGivenClass(child.getType(), parentClass.getType());
	}
	
	public static boolean isInheritedFromGivenClass(Type subType, Type parentType) {
		if (subType instanceof PrimType || parentType instanceof PrimType)
			return false;
		else if ((subType instanceof ArrayType && parentType instanceof RefType) || 
			(parentType instanceof ArrayType && subType instanceof RefType))
			return false;
		else if (subType instanceof RefType && parentType instanceof RefType)
			return isInheritedFromGivenClass(((RefType) subType).getSootClass(), parentType.toString(), MatchType.equal);
		else if (subType instanceof ArrayType && parentType instanceof ArrayType) {
			int subDim = ((ArrayType) subType).numDimensions;
			int parentDim = ((ArrayType) parentType).numDimensions;
			if (subDim != parentDim)
				return false;
			Type subBaseType = ((ArrayType) subType).baseType;
			Type parentBaseType = ((ArrayType) parentType).baseType;
			return isInheritedFromGivenClass(subBaseType, parentBaseType);
		}
		else {
//			assert(false);
			return false;
		}
			
	}
	
	public enum MatchType{
		equal,regular
	}
	
	public static boolean isInheritedFromGivenClass(SootClass theClass,String classNameUnderMatch,MatchType matchType){
		if (theClass == null)
			return false;
		if (isTypeMatch(theClass,classNameUnderMatch,matchType))
			return true;
		for (SootClass interfaceClass:theClass.getInterfaces())
			if (isInheritedFromGivenClass(interfaceClass, classNameUnderMatch,matchType))
				return true;
		return isInheritedFromGivenClass(theClass.getSuperclassUnsafe(), classNameUnderMatch, matchType);
	}
	
	private static boolean isTypeMatch(SootClass currentClass,String classNameUnderMatch,MatchType matchType){
		if( matchType == MatchType.equal && currentClass.getType().toString().equals(classNameUnderMatch))
			return true;
		else if( matchType == MatchType.regular && isRegularMatch(currentClass.getType().toString(), classNameUnderMatch))
			return true;
		else
			return false;
	}
	
	
	private static boolean isRegularMatch(String targetStr,String regularStr){
		Pattern p=Pattern.compile(regularStr);
		Matcher m=p.matcher(targetStr);
		return m.matches();
	}
	
	private static void handleLayoutXml() {
		if (AndroidCallBacks.sXMLCALLBACK_METHODS == null) {
			return;
		}
//		MultiMap<String, String> xmlCALLBACK_METHODS = AndroidCallBacks.sXMLCALLBACK_METHODS;
//		for (String str : xmlCALLBACK_METHODS.keySet()) {
//			if (mActivityInfo.mInvokedLayoutXmls != null && mActivityInfo.mInvokedLayoutXmls.contains(str)) {
//				for (String callback : xmlCALLBACK_METHODS.get(str)) {
//					SootMethod sootMethod = MethodUtil.getMethod(mActivityUnderAnalysis, callback);
//					if (sootMethod != null) {
//						this.callbacks.add(sootMethod);
//					}
//				}
//			}
//		}
//		MultiMap<String, SootClass> xmlFragments = AndroidCallBacks.sXMLFRAGMENTS;
//		for (String str : xmlFragments.keySet()) {
//			if (mActivityInfo.mInvokedLayoutXmls != null && mActivityInfo.mInvokedLayoutXmls.contains(str)) {
//				for (SootClass fragment : xmlFragments.get(str)) {
//					if (fragment.getName().equals(AndroidEntryPointConstants.FRAGMENTCLASS)
//							|| fragment.getName().equals(AndroidEntryPointConstants.SUPPORTFRAGMENTCLASS)
//							|| fragment.getName().equals(AndroidEntryPointConstants.SUPPORTFRAGMENTCLASS_V7)) {
//						continue;
//					}
//					this.mFragmentClasses.add(fragment);
//				}
//			}
//		}
	}

}

package cn.ac.ios.dmmpp;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import cn.ac.ios.dmmpp.gen.DMMFactory;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

public class DDMMain {

	public static void main(String[] args) {
		DDMSootConfig.ANDROID_PLATFORM_PATH = args[0];
		DDMSootConfig.ApkPath = args[1];
		DDMSootConfig.output = args[2];
//		DDMSootConfig.ANDROID_PLATFORM_PATH = "D:\\cbq\\android-platforms";
//		DDMSootConfig.ApkPath = "D:\\cbq\\Research\\DDM\\apks\\f-Droid\\com.uberspot.a2048_25.apk";
//		DDMSootConfig.output = ".\\output";
		DDMSootConfig.sootInitialization();
		Set<SootClass> sootClasses = new HashSet<SootClass>(Scene.v().getApplicationClasses());
		for (SootClass sootClass : sootClasses) {
			 DMMFactory.createDDM(sootClass);
//			SootMethod sootMethod = DMMFactory.createDDM(sootClass);
//			if(sootMethod !=null) {
//				Log.i(sootMethod.getActiveBody());
//			}
//			if(sootMethod!=null) {
//				 writeClass(sootClass);
//			}	
		}
		String apkName = DDMSootConfig.ApkPath.substring(DDMSootConfig.ApkPath.lastIndexOf(File.separator ) + 1);
		File file = new File(DDMSootConfig.output + File.separator + apkName);
		if(file.exists()) {
			file.delete();
		}
		if (Options.v().output_format() != Options.output_format_none) {
			PackManager.v().writeOutput();
		}
				
		if (Options.v().output_format() == Options.output_format_dex) {
			file = new File(DDMSootConfig.output + File.separator + apkName);
			if(file.exists()) {
				file.renameTo(new File(DDMSootConfig.output + File.separator + "DMMPP_"+apkName));
			}
		}
	}

	static void writeClass(SootClass sootClass) {
		try {
			Method method = PackManager.class.getDeclaredMethod("writeClass", SootClass.class);
			boolean access = method.isAccessible();
			
			method.setAccessible(true);
			
			int output_format = Options.v().output_format();
			 Options.v().set_output_format(Options.output_format_c);
			method.invoke(PackManager.v(), sootClass);
			Options.v().set_output_format(output_format);
			
			method.setAccessible(access);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	

}

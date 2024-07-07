package cn.ac.ios.dmmpp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.Transform;
import soot.options.Options;

public class DDMSootConfig {
	
	static String ANDROID_PLATFORM_PATH = null;
	
	static String ApkPath = null;
	
	static String output = null;
	
	static boolean outputAPK = true;

	static public void sootInitialization() {
		Options.v().set_android_jars(ANDROID_PLATFORM_PATH);
//		Options.v().set_soot_classpath(BaseConfiguration.JAVA_HOME + File.pathSeparator + this.getAndroidJarPath());
		Options.v().set_process_dir(Collections.singletonList(ApkPath));
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_process_multiple_dex(true);
		Options.v().set_whole_program(true);
		if(outputAPK) {
			Options.v().set_output_dir(output);
			Options.v().set_output_format(Options.output_format_dex);
//			Options.v().set_output_format(Options.output_format_c);
		}else {
			Options.v().set_output_format(Options.output_format_none);
		}

		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().allow_phantom_refs();
		setExcludePackage();
		
		Pack p1 = PackManager.v().getPack("jtp");
		String phaseName = "jtp.bt";
		
		Transform t1 = new Transform(phaseName, new BodyTransformer() {
			@Override
			protected void internalTransform(Body b, String phase,
					Map<String, String> options) {
				b.getMethod().setActiveBody(b);
			}
		});
		
		p1.add(t1);
		
		soot.Main.v().autoSetOptions();
		
		Scene.v().loadNecessaryClasses();
		
		PackManager.v().runPacks();
	}

	
	private static void setExcludePackage() {
		ArrayList<String> excludeList = new ArrayList<String>();
		excludeList.add("android.*");
		excludeList.add("org.*");
		excludeList.add("soot.*");
		excludeList.add("java.*");
		excludeList.add("sun.*");
		excludeList.add("javax.*");
		excludeList.add("com.sun.*");
		excludeList.add("com.ibm.*");
		excludeList.add("org.xml.*");
		excludeList.add("org.w3c.*");
		excludeList.add("apple.awt.*");
		excludeList.add("com.apple.*");
		Options.v().set_exclude(excludeList);
	}

}

# DMMPP  
Constructing Dummy Main Methods for Android Apps  with Path-sensitive Predicates  

## Dataset  
```
./apks
```

## Open Source  
```
Maven Project  
./DMMPP Code Project          
```

## Last Executable Version   
```
./DMMPP.jar       
```


## How to Use   
### API Invocation   

```
	
	DDMSootConfig.ANDROID_PLATFORM_PATH = $androidPlatformPath$;
	DDMSootConfig.ApkPath = $apkPath$;
	DDMSootConfig.output = $outputPath$;
	
	//set output format 
	
	DDMSootConfig.sootInitialization();
	
	Set<SootClass> sootClasses = new HashSet<SootClass>(Scene.v().getApplicationClasses());
	for (SootClass sootClass : sootClasses) {
		SootMethod sootMethod = DMMFactory.createDDM(sootClass);
		// do your task
	}
	 
```

### Command Line  
```
java -cp DMMPP.jar cn.ac.ios.dmmpp.DMMMain $androidPlatformPath$ $apkPath$ $outputPath$

#output: 
$outputPath$ + "DMMPP_"+apkName
```

#e.g. 
```
java -cp DMMPP.jar cn.ac.ios.dmmpp.DMMMain ./android-platforms ./apks/f-Droid/ch.bailu.aat.apk  ./output

#output:
./output/DMMPP_ch.bailu.aat.apk
```   

## Video on YouTube

[DMMPP: Constructing Dummy Main Methods for Android Apps  with Path-sensitive Predicates](https://youtu.be/dLOwRwp1sPo)   

If you can't click to jump to YouTube, pls copy the link below   
https://youtu.be/dLOwRwp1sPo 

or download the video from this rep. 
```
./Intro of DMMPP.mp4
```

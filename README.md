# android_samples


Pro přepínání mezi jednotlivými aplikacemi je nutné změnit:
1)soubor AndroidManifest.xml ve složce "android_samples/app/src/main/" 
		
Je nutné změnit název balíčku souboru MainActivity ->  
		\<activity android:name=".lvl1basic.p00.p01buffer.MainActivity"\>


2)soubor activity_main.xml ve složce "android_samples/app/src/main/res/layout/" 

Je nutné změnit balíček GLSurfaceView prvku -> 
    	\<uhk.android_samples.lvl1basic.p02geometry.p02strip.MyGLSurfaceView
		... \> 
	\</uhk.android_samples.lvl1basic.p02geometry.p02strip.MyGLSurfaceView\>

Shadery jsou umístěny ve složce "android_samples/app/src/main/assets/shaders/"

3D objekty jsou umístěny ve složce "android_samples/app/src/main/assets/objects"
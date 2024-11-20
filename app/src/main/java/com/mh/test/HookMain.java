package com.mh.test;

import android.os.IBinder;
import android.util.Log;

import com.shark.input.InputManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {
    final String TAG = "SharkChilli";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //hook Toast
        Log.i(TAG, "handleLoadPackage: ..............................................................................................");
        if (!"com.mh.test".equals(lpparam.packageName)) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//                InputManager.getInstance().inputText("sadsakljikl");
//                InputManager.getInstance().downUpKeycode(26);
                InputManager.getInstance().touch(334,271);
            }
        }).start();

        Class<?> MainActivityClazz = lpparam.classLoader.loadClass("com.mh.test.MainActivity");

        if (MainActivityClazz != null) {
            XposedHelpers.findAndHookMethod(MainActivityClazz, "test1", String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult("=======================");
                }
            });
        }
    }

}

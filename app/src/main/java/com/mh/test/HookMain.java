package com.mh.test;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.shark.input.InputManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {
    final String TAG = "SharkChilli";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //hook Toast
        Log.i(TAG, "handleLoadPackage : ...........................*");

        XposedHelpers.findAndHookMethod(
                "android.app.Activity", // Hook Activity 类
                lpparam.classLoader,
                "onCreate", // 方法名
                android.os.Bundle.class, // 参数类型
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // 在 onCreate 方法执行前的逻辑
                        Activity activity = (Activity) param.thisObject; // 当前 Activity 对象
                        Bundle bundle = (Bundle) param.args[0]; // onCreate 的 Bundle 参数
//                        Toast.makeText(activity, activity.getClass().getName(), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Activity onCreate: Before Hooked - " + activity.getClass().getName());
                        if (bundle != null) {
                            Log.i(TAG, "Activity onCreate Bundle: " + bundle.toString());
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        // 在 onCreate 方法执行后的逻辑
                        Activity activity = (Activity) param.thisObject; // 当前 Activity 对象
                        Log.i(TAG, "Activity onCreate: After Hooked - " + activity.getClass().getName());
                    }
                }
        );

        XposedHelpers.findAndHookMethod(
                "android.app.Application", // Hook Application 类
                lpparam.classLoader,
                "onCreate", // 方法名
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // 在 onCreate 方法执行前 Hook
                        Log.i(TAG, "Application onCreate called - before");

                        // 可以获取当前 Application 实例
                        Object applicationInstance = param.thisObject;
                        Log.i(TAG, "Application instance: " + applicationInstance);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        // 在 onCreate 方法执行后 Hook
                        Log.i(TAG, "Application onCreate called - after");

                        // 可以执行额外逻辑，例如获取当前上下文
                        Application applicationInstance = (Application) param.thisObject;

                        Toast.makeText(applicationInstance, "applicationInstance init test sd", Toast.LENGTH_SHORT).show();
                    }
                }
        );

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
//                InputManager.getInstance().touch(334,271);
//                InputManager.getInstance().swipe(241, 97, 235, 541);
            }
        }).start();

//        Class<?> MainActivityClazz = lpparam.classLoader.loadClass("com.mh.test.MainActivity");
//
//        if (MainActivityClazz != null) {
//            XposedHelpers.findAndHookMethod(MainActivityClazz, "test1", String.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    param.setResult("123453");
//                }
//            });
//        }
    }

}

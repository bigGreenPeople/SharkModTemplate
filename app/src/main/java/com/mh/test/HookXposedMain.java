package com.mh.test;

import android.app.Activity;
import android.app.Application;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.ServiceManager;
import android.util.Log;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import android.content.pm.IPackageManager;

import com.github.kyuubiran.ezxhelper.EzXHelper;
import com.github.kyuubiran.ezxhelper.HookFactory;
import com.github.kyuubiran.ezxhelper.finders.MethodFinder;
import com.github.kyuubiran.ezxhelper.interfaces.IMethodHookCallback;
import com.shark.input.model.Input;

import java.lang.reflect.Method;
import java.util.List;


public class HookXposedMain implements IXposedHookLoadPackage {
    final String TAG = "SharkChilliTest";

    public void printStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder stackTraceString = new StringBuilder();

        for (StackTraceElement element : stackTrace) {
            stackTraceString.append(element.toString()).append("\n");
        }

        // 打印调用栈
        Log.i(TAG, "Stack Trace:\n" + stackTraceString.toString());
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //hook Toast
        Log.i(TAG, "handleLoadPackage android");
//        printStackTrace();


//        if (lpparam.packageName.equals("android")) {
//            EzXHelper.initHandleLoadPackage(lpparam);
//
//            //2024-11-23 12:34:12.842  1342-1342  SharkChilli             pid-1342
//            //system        1342   814 47 12:34:10 ?    00:00:17 system_server
//            // I  handleLoadPackage android: ********................................................................................
//            Log.i(TAG, "handleLoadPackage android: 111111................................................................................");
//            try {
//
//                Method addServiceMethod = MethodFinder.fromClass(ServiceManager.class)
//                        .filterByName("addService").first();
//                HookFactory.createMethodBeforeHook(addServiceMethod, methodHookParam -> {
//                    if ("package".equals(methodHookParam.args[0])) {
//                        IPackageManager pms = (IPackageManager) methodHookParam.args[1];
//                        Log.i(TAG, "pms: " + pms);
//                        HMAService.pms = pms;
//
//                        hookPms(lpparam.classLoader);
//                    }
//                });
//
//            } catch (Exception e) {
//                Log.e(TAG, "onMethodHooked: ", e);
//            }
//        }
    }

    public void hookPms(ClassLoader classLoader) {
        Class<?> PackageManagerServiceClass = null;
        try {
            PackageManagerServiceClass = classLoader.loadClass("com.android.server.pm.PackageManagerService");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hookPms: ", e);
        }
        List<Method> filterAppAccessLPr = MethodFinder.fromClass(PackageManagerServiceClass)
                .filterByName("filterAppAccessLPr")
                .filterByParamCount(5).toList();
        Log.i(TAG, "filterAppAccessLPr: " + filterAppAccessLPr);
        HookFactory.createMethodBeforeHooks(filterAppAccessLPr, param -> {
            try {
                int callingUid = (int) param.args[1];
                if (callingUid == 1000) return;
                long identity = Binder.clearCallingIdentity();
                String[] callingApps = HMAService.pms.getPackagesForUid(callingUid);
                Binder.restoreCallingIdentity(identity);

                if (callingApps == null) return;
                Object packageSettings = param.args[0];
                if (packageSettings == null) return;

                String nameField = "name";
                if (Build.VERSION.SDK_INT >= 33) {
                    nameField = "mName";
                }
                String targetApp = (String) XposedHelpers.getObjectField(packageSettings, nameField);
                // com.android.server.pm.PackageSetting
//                Log.i(TAG, "targetApp: " + targetApp);

//                for (String caller : callingApps) {
//                    Log.i(TAG, "caller: " + caller);
//
//                }

                if ("com.mh.test".equals(targetApp)) {
                    param.setResult(true);
                }

            } catch (Exception e) {
                Log.e(TAG, "onMethodHooked: ", e);
            }
        });
    }
}

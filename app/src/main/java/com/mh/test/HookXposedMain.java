package com.mh.test;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookXposedMain implements IXposedHookLoadPackage {
    final String TAG = "SharkChilliTest";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //hook Toast

        if (lpparam.packageName.equals("android") ) {
            //2024-11-23 12:34:12.842  1342-1342  SharkChilli             pid-1342
            //system        1342   814 47 12:34:10 ?    00:00:17 system_server
            // I  handleLoadPackage android: ********................................................................................
            Log.i(TAG, "handleLoadPackage android: 111111................................................................................");

        }
    }

}

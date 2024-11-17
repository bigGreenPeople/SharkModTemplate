package com.shark.input;

import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.shark.aidl.IInputService;
import com.shark.input.model.ControlMessage;

public class InputManager {
    public static final String TAG = "SharkChilli";
    public IInputService iInputService;
    public Gson mGson = new Gson();

    // 静态私有实例，确保单例
    private static InputManager instance;

    // 私有构造函数，防止外部实例化
    private InputManager(Context context) {
        // 初始化操作
        IBinder requestBinder = requestBinder(context, context.getPackageName());
        if (requestBinder == null) {
            Log.e(TAG, "requestBinder is null ");
            return;
        }

        iInputService = IInputService.Stub.asInterface(requestBinder);
    }

    public void handleEvent(ControlMessage controlMessage) {
        String msg = mGson.toJson(controlMessage);
        try {
            iInputService.handleEvent(msg);
        } catch (Exception e) {
            Log.e(TAG, "handleEvent: ", e);
        }
    }


    public static IBinder requestBinder(Context context, String niceName) {
        int BRIDGE_ACTION_GET_BINDER = 3;
        String BRIDGE_SERVICE_NAME = "activity";
        int BRIDGE_TRANSACTION_CODE = 1598837584;
        try {
            // 获取 AMS
            String bridgeServiceName = BRIDGE_SERVICE_NAME;
            IBinder bridgeService = ServiceManager.getService(bridgeServiceName);

            if (bridgeService == null) {
                Log.d(TAG, "Can't get " + BRIDGE_SERVICE_NAME);
                return null;
            }

            // Heartbeat Binder 实例
            Binder heartBeatBinder = new Binder();

            // Parcel 初始化
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();

            try {
                // 写入 descriptor
                data.writeInterfaceToken("LSPosed");
                // 写入 action code
                data.writeInt(BRIDGE_ACTION_GET_BINDER);
                // 写入 niceName
                data.writeString(niceName);
                // 写入 Heartbeat Binder
                data.writeStrongBinder(heartBeatBinder);

                // 调用 transact
                boolean result = bridgeService.transact(
                        BRIDGE_TRANSACTION_CODE,
                        data,
                        reply,
                        0
                );

                if (result) {
                    // 检查异常
                    reply.readException();
                    // 获取返回的 Binder
                    return reply.readStrongBinder();
                }
            } finally {
                // 回收 Parcel
                data.recycle();
                reply.recycle();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to request binder", e);
        }
        return null;
    }

    // 提供全局访问点，使用双重检查锁实现线程安全的单例
    public static InputManager getInstance(Context context) {
        if (instance == null) {
            synchronized (InputManager.class) {
                if (instance == null) {
                    instance = new InputManager(context);
                }
            }
        }
        return instance;
    }

    // 添加其他方法和功能
    public void someMethod() {
        // 示例方法
        System.out.println("Executing someMethod...");
    }
}

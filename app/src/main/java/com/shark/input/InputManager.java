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
import com.shark.input.model.Input;
import com.shark.input.model.Point;
import com.shark.input.model.Position;
import com.shark.input.model.Size;
import com.shark.utils.SleepUtil;

public class InputManager {
    public static final String TAG = "SharkChilli";
    public IInputService iInputService;
    public Gson mGson = new Gson();

    // 静态私有实例，确保单例
    private static InputManager instance;

    // 私有构造函数，防止外部实例化
    private InputManager() {
        // 初始化操作
        IBinder requestBinder = requestBinder(null, "noName");
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

    public void injectKeycode(int action, Integer keycode) {
        ControlMessage controlMessage = ControlMessage.createInjectKeycode(action, keycode, 0, 0);
        handleEvent(controlMessage);
    }

    public void downKeycode(Integer keycode) {
        injectKeycode(Input.AKEY_EVENT_ACTION_DOWN, keycode);
    }

    public void upKeycode(Integer keycode) {
        injectKeycode(Input.AKEY_EVENT_ACTION_UP, keycode);
    }

    public void downUpKeycode(Integer keycode) {
        downKeycode(keycode);
        SleepUtil.randomSleep(50, 150);
        upKeycode(keycode);
    }

    public void inputText(String text) {
        ControlMessage controlMessage = ControlMessage.createInjectText(text);
        handleEvent(controlMessage);
    }

    //{"action":0,"actionButton":1,"buttons":1,"copyKey":0,"hScroll":0.0,"keycode":0,"metaState":0,"paste":false,"pointerId":-1,"position":{"point":{"x":334,"y":271},"screenSize":{"height":816,"width":376}},"pressure":1.5258789E-5,"repeat":0,"sequence":0,"type":2,"vScroll":0.0}
    //{type=2, text='null', metaState=0, action=0, keycode=0, actionButton=1, buttons=1, pointerId=-1, pressure=1.5258789E-5, position=Position{point=Point{x=280, y=163}, screenSize=Size{width=376, height=816}}, hScroll=0.0, vScroll=0.0, copyKey=0, paste=false, repeat=0, sequence=0}
    //{type=2, text='null', metaState=0, action=0, keycode=0, actionButton=1, buttons=1, pointerId=-1, pressure=1.5258789E-5, position=Position{point=Point{x=280, y=163}, screenSize=Size{width=376, height=816}}, hScroll=0.0, vScroll=0.0, copyKey=0, paste=false, repeat=0, sequence=0}
    public void touch(int x, int y) {
        Point point = new Point(x, y);
        Size size = new Size(376, 816);
        Position position = new Position(point, size);
        ControlMessage controlMessage = ControlMessage.createInjectTouchEvent(Input.AKEY_EVENT_ACTION_DOWN, -1, position, (float) 1.5258789E-5, 1, 1);
        handleEvent(controlMessage);
        SleepUtil.randomSleep(50, 150);
        ControlMessage controlMessage2 = ControlMessage.createInjectTouchEvent(Input.AKEY_EVENT_ACTION_UP, -1, position, (float) 0.0f, 1, 0);
        handleEvent(controlMessage2);

    }
//{type=2, text='null', metaState=0, action=0, keycode=0, actionButton=1, buttons=1, pointerId=-1, pressure=1.5258789E-5, position=Position{point=Point{x=230, y=451}, screenSize=Size{width=376, height=816}}, hScroll=0.0, vScroll=0.0, copyKey=0, paste=false, repeat=0, sequence=0}
//{type=2, text='null', metaState=0, action=1, keycode=0, actionButton=1, buttons=0, pointerId=-1, pressure=0.0,          position=Position{point=Point{x=230, y=451}, screenSize=Size{width=376, height=816}}, hScroll=0.0, vScroll=0.0, copyKey=0, paste=false, repeat=0, sequence=0}

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
    public static InputManager getInstance() {
        if (instance == null) {
            synchronized (InputManager.class) {
                if (instance == null) {
                    instance = new InputManager();
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

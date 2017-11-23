package com.github.xgfjyw.webrtcclient;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by hongxiliang2 on 29/6/2016.
 */
public class JSCore {
    private static final String TAG = "JSCore";

    private static boolean initialized = false;
    private static Scriptable scope;
    private static android.content.Context androidContext;
    private static HandlerThread thread;
    private static Handler handler;
    private static LinkedList<Runnable> blocks;

    public static synchronized void init(final android.content.Context context) throws IllegalArgumentException {
        if (initialized)
            return;

        if (context instanceof Activity) {
            throw new IllegalArgumentException("context should be an application context");
        } else {
            androidContext = context;
        }

        thread = new HandlerThread("js/rhino_thread");
        thread.start();
        handler = new Handler(thread.getLooper());
        blocks = new LinkedList<>();

        /* initialize global APIs */
        runOnSingleThread(new Runnable() {
            @Override
            public void run() {
                Context ctx = Context.enter();
                ctx.setOptimizationLevel(-1);
                scope = ctx.initStandardObjects();
                try {
                    ScriptableObject.defineClass(scope, Console.class);
                    ctx.evaluateString(scope, "var setTimeout = console.setTimeout", TAG, 1, null);
                    ScriptableObject.defineClass(scope, ReliableSocket.class);
                    ScriptableObject.defineClass(scope, RTCPeerConnection.class);
                    ScriptableObject.defineClass(scope, RTCUserMedia.class);
                    ScriptableObject.defineClass(scope, RTCIceCandidate.class);
                    ScriptableObject.defineClass(scope, RTCSessionDescription.class);
                    ctx.evaluateString(scope, "var navigator = {}; navigator.getUserMedia = function(constraints, successCallback, errorCallback) { new _getUserMedia(constraints, successCallback, errorCallback); };", TAG, 1, null);
                    ctx.evaluateString(scope, "var addVideoStream = _getUserMedia.addVideoStream;", TAG, 1, null);

                    initialized = true;

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();

                } finally {
                    Context.exit();
                }
            }
        });
    }

    public static synchronized void deinit() {
        if (!initialized)
            return;

        thread.quit();
        thread = null;
        scope = null;

        initialized = false;
    }

    public static void runScript(final String script, final String scriptName) {
        if (blocks == null) {
            throw new Error("handler thread linked list not initialized.");
        }

        blocks.addLast(new Runnable() {
            @Override
            public void run() {
                Context ctx = Context.enter();
                ctx.setOptimizationLevel(-1);
                try {
                    ctx.evaluateString(scope, script, scriptName, 1, null);

                } catch (EcmaError ex) {
                    Log.e(TAG, ex.toString());
                } catch (UnsupportedOperationException ex) {
                    Log.e(TAG, ex.toString());

                } finally {
                    Context.exit();
                }
            }
        });

        if (blocks.size() > 1000) {
            throw new Error("too much elements in handler thread linked list.");
        }

        if (!initialized) {
            return;
        }

        Runnable block = null;
        try {
            while ((block = blocks.removeFirst()) != null) {
                runOnSingleThread(block);
            }
        } catch (NoSuchElementException e) {}
    }

    public static synchronized void runOnSingleThread(final Runnable block) {
        if (handler == null) {
            throw new Error("handler thread not initialized.");
        }
        handler.post(block);
    }

    public static android.content.Context androidContext() {
        return androidContext;
    }

    public static Scriptable scope() {
        return scope;
    }

    public static void print_debug_info(String loc) {
//        Log.d("__DEBUG__", "@" + loc + ": " + Thread.currentThread().getId());
    }


    public static class Console extends ScriptableObject {
        @Override
        public String getClassName() {
            return "console";
        }
        public Console() {}

        /* console.log() */
        @JSStaticFunction
        public static void log(Object msg) {
            print_debug_info("console.log");

            switch(msg.getClass().getName()) {
                case "java.lang.String":
                    Log.i(TAG, (String)msg);
                    break;
                default:
                    Log.i(TAG, "@console.log paramter type: " + msg.getClass().getName());
            }
        }

        /* global.setTimeout() */
        private static java.util.Timer timer = new java.util.Timer();
        @JSStaticFunction
        public static void setTimeout(final Function func, int interval) {
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Context ctx = Context.enter();
                    ctx.setOptimizationLevel(-1);
                    Scriptable scope = JSCore.scope;

                    Object[] args = new Object[]{ };
                    try {
                        func.call(ctx, scope, ctx.newObject(scope), args);

                    } catch (EcmaError ex) {
                        Log.e(TAG, ex.toString());
                    } catch (UnsupportedOperationException ex) {
                        Log.e(TAG, ex.toString());

                    } finally {
                        Context.exit();
                    }
                }
            }, interval);
        }

        @JSStaticFunction
        public static void getJavascriptObject(NativeObject obj) {
            Log.i(TAG, "aaa" + obj.getClass().getName());
        }
    }
}

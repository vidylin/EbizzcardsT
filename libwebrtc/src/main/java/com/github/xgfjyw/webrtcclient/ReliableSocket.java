package com.github.xgfjyw.webrtcclient;

/**
 * Created by hongxiliang2 on 18/8/2016.
 */

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.github.xgfjyw.libnettools.NetworkObserver;
import com.github.xgfjyw.libnettools.NetworkStatusNotificationReceiver;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;

import java.net.URI;



public class ReliableSocket extends ScriptableObject implements NetworkObserver {
    private static final String TAG = "jeromq";

    private ZmqSocket socket;

    private Function onopen;
    private Function onmessage;
    private Function onclose;
    private Function onreset;
    private Function onerror;

    @Override
    public String getClassName() {
        return "WebSocket";
    }

    public ReliableSocket() { }

    @JSConstructor
    public ReliableSocket(String url) {
        URI uri = URI.create(url);
        socket = new ZmqSocket(uri);
        NetworkStatusNotificationReceiver.regObserver(this);

        socket.connect();
    }

    @JSFunction
    public void send(String msg) {
        socket.send(msg);
    }

    @JSFunction
    public void close() {
        NetworkStatusNotificationReceiver.unregObserver(this);
        socket.close();
    }

    @JSFunction
    public void ping() { }

    @Override
    public void onNetworkChanged() {
        Log.i(TAG, "restarting socket");
        socket.restart();
    }

    private class ZmqSocket {
        private HandlerThread thread;
        private Handler handler;
        private ThreadLocal<ZMQ.Context> threadLocalCtx;
        private ThreadLocal<ZMQ.Socket> threadLocalSocket;

        private String url;

        public ZmqSocket(URI _url) {
            url = _url.toString().replace("ws://", "tcp://");

            thread = new HandlerThread("zmq_thread");
            thread.start();
            handler = new Handler(thread.getLooper());


            Runnable block = new Runnable() {
                @Override
                public void run() {
                    ZMQ.Context ctx = ZMQ.context(1);
                    ZMQ.Socket socket = ctx.socket(ZMQ.DEALER);

                    threadLocalCtx = new ThreadLocal<ZMQ.Context>();
                    threadLocalCtx.set(ctx);
                    threadLocalSocket = new ThreadLocal<ZMQ.Socket>();
                    threadLocalSocket.set(socket);
                }
            };
            runOnSingleThread(block);

            recv();
        }

        public void connect() {
            Runnable block = new Runnable() {
                @Override
                public void run() {
                    ZMQ.Socket socket = threadLocalSocket.get();
                    if (socket == null) {
                        Log.w(TAG, "socket not exist in current thread");
                        return;
                    }

                    socket.connect(url);
                    onOpen();
                }
            };
            runOnSingleThread(block);
        }

        public void send(final String msg) {
            Runnable block = new Runnable() {
                @Override
                public void run() {
                    ZMQ.Socket socket = threadLocalSocket.get();
                    if (socket == null) {
                        Log.w(TAG, "socket not exist in current thread");
                        return;
                    }

//                    Log.i(TAG, "socket -> " + msg);
                    socket.send(msg);
                }
            };

            runOnSingleThread(block);
        }

        public void recv() {
            final int interval = 50;
            final Handler handler = new Handler(thread.getLooper());
            Runnable block = new Runnable() {
                @Override
                public void run() {
                    ZMQ.Socket socket = threadLocalSocket.get();
                    if (socket == null) {
                        Log.w(TAG, "socket not exist in current thread");
                        return;
                    }

                    /*ZMQ.Context ctx = threadLocalCtx.get();
                    if (ctx == null) {
                        Log.w(TAG, "context not exist in current thread");
                        return;
                    }*/

                    ZMQ.Poller items = new Poller(1);
                    items.register(socket, ZMQ.Poller.POLLIN);

                    items.poll(0);
                    if (items.pollin(0)) {
                        String msg = socket.recvStr();
                        onMessage(msg);
                    }

                    handler.postDelayed(this, interval);
                }
            };

            handler.postDelayed(block, interval);
        }

        public void close() {
            Runnable block = new Runnable() {
                @Override
                public void run() {
                    ZMQ.Socket socket = threadLocalSocket.get();
                    if (socket != null) {
                        socket.close();
                        threadLocalSocket.remove();
                    }

                    ZMQ.Context ctx = threadLocalCtx.get();
                    if (ctx != null) {
                        ctx.close();
                        threadLocalCtx.remove();
                    }

                    thread.getLooper().quit();
                    thread = null;

                    onopen = null;
                    onmessage = null;
//                    onclose = null;
                    onreset = null;
                    onerror = null;

                    onClose();
                }
            };

            runOnSingleThread(block);
        }

        public void restart() {
            Runnable block = new Runnable() {
                @Override
                public void run() {
                    ZMQ.Socket socket = threadLocalSocket.get();
                    if (socket != null) {
                        socket.close();
                        socket = null;
                        threadLocalSocket.remove();
                    }

                    ZMQ.Context ctx = threadLocalCtx.get();
                    socket = ctx.socket(ZMQ.DEALER);
                    threadLocalSocket.set(socket);
                    socket.connect(url);

                    onReset();
                }
            };

            runOnSingleThread(block);
        }

        public void onOpen() {
            if (onopen instanceof Function) {
                JSCore.runOnSingleThread(new Runnable() {
                    @Override
                    public void run() {
                        Context ctx = Context.enter();
                        ctx.setOptimizationLevel(-1);
                        Scriptable scope = JSCore.scope();

                        Object[] args = new Object[]{};
                        try {
                            onopen.call(ctx, scope, ctx.newObject(scope), args);
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                        } finally {
                            Context.exit();
                        }
                    }
                });
            }
        }

        public void onMessage(final String msg) {
//            Log.i(TAG, "websocket <- " + msg);
            if (onmessage instanceof Function) {
                JSCore.runOnSingleThread(new Runnable() {
                    @Override
                    public void run() {
                        Context ctx = Context.enter();
                        ctx.setOptimizationLevel(-1);
                        Scriptable scope = JSCore.scope();

                        Object[] args = new Object[]{ msg };
                        try {
                            onmessage.call(ctx, scope, ctx.newObject(scope), args);
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                        } finally {
                            Context.exit();
                        }
                    }
                });
            }
        }

        public void onClose() {
            final String str = "websocket are closing";
            Log.i(TAG, str);

            if (onclose instanceof Function) {
                JSCore.runOnSingleThread(new Runnable() {
                    @Override
                    public void run() {
                        Context ctx = Context.enter();
                        ctx.setOptimizationLevel(-1);
                        Scriptable scope = JSCore.scope();

                        Object[] args = new Object[]{ str };
                        try {
                            onclose.call(ctx, scope, ctx.newObject(scope), args);
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                        } finally {
                            Context.exit();
                        }
                    }
                });
            }
        }

        public void onReset() {
            if (onreset instanceof Function) {
                JSCore.runOnSingleThread(new Runnable() {
                    @Override
                    public void run() {
                        Context ctx = Context.enter();
                        ctx.setOptimizationLevel(-1);
                        Scriptable scope = JSCore.scope();

                        Object[] args = new Object[]{};
                        try {
                            onreset.call(ctx, scope, ctx.newObject(scope), args);
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                        } finally {
                            Context.exit();
                        }
                    }
                });
            }
        }

        public void onError(Exception ex) {
            if (onerror instanceof Function) {
                final String err = ex.toString();
                JSCore.runOnSingleThread(new Runnable() {
                    @Override
                    public void run() {
                        Context ctx = Context.enter();
                        ctx.setOptimizationLevel(-1);
                        Scriptable scope = JSCore.scope();

                        Object[] args = new Object[]{ err };
                        try {
                            onerror.call(ctx, scope, ctx.newObject(scope), args);
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                        } finally {
                            Context.exit();
                        }
                    }
                });
            }
        }

        private synchronized void runOnSingleThread(Runnable block) {
            if (thread != null) {
                handler.post(block);
            }
        }
    }


    @JSSetter
    public void setOnopen(Function func) {
        onopen = func;
    }
    @JSGetter
    public Function getOnopen() {
        return onopen;
    }

    @JSSetter
    public void setOnmessage(Function func) {
        onmessage = func;
    }
    @JSGetter
    public Function getOnmessage() {
        return onmessage;
    }

    @JSSetter
    public void setOnerror(Function func) {
        onerror = func;
    }
    @JSGetter
    public Function getOnerror() {
        return onerror;
    }

    @JSSetter
    public void setOnclose(Function func) {
        onclose = func;
    }
    @JSGetter
    public Function getOnclose() {
        return onclose;
    }

    @JSSetter
    public void setOnreset(Function func) {
        onreset = func;
    }
    @JSGetter
    public Function getOnreset() {
        return onreset;
    }
}

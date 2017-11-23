package com.gzligo.ebizzcardstranslator.base.mvp;


import android.os.Build;
import android.os.Bundle;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by xfast on 2017/6/1.
 * a copy of android.os.Message, replace Handler to IView
 */
public final class Message implements Parcelable {
    public int what;

    public int arg1;

    public int arg2;

    public String str;

    /**
     * 记录presenter的类名, 当一个页面需要持有多个Presenter时使用
     */
    public String presenter;

    public Object obj;

    public Object[] objs;

    public Messenger replyTo;

    public int sendingUid = -1;

    static final int FLAG_IN_USE = 1 << 0;

    static final int FLAG_ASYNCHRONOUS = 1 << 1;

    static final int FLAGS_TO_CLEAR_ON_COPY_FROM = FLAG_IN_USE;

    int flags;

    Bundle data;

    IView target;

    Message next;

    private static final Object sPoolSync = new Object();
    private static Message sPool;
    private static int sPoolSize = 0;

    private static final int MAX_POOL_SIZE = 50;

    private static boolean gCheckRecycle = true;

    private static Message obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new Message();
    }

    public static Message obtain(Message orig) {
        Message m = obtain();
        m.what = orig.what;
        m.str = orig.str;
        m.presenter = orig.presenter;
        m.arg1 = orig.arg1;
        m.arg2 = orig.arg2;
        m.obj = orig.obj;
        m.objs = orig.objs;
        m.replyTo = orig.replyTo;
        m.sendingUid = orig.sendingUid;
        if (orig.data != null) {
            m.data = new Bundle(orig.data);
        }
        m.target = orig.target;

        return m;
    }

    public static Message obtain(IView v) {
        Message m = obtain();
        m.target = v;
        return m;
    }

    public static Message obtain(IView v, Object obj) {
        Message m = obtain();
        m.target = v;
        m.obj = obj;
        return m;
    }

    public static Message obtain(IView v, Object[] objs) {
        Message m = obtain();
        m.target = v;
        m.objs = objs;
        return m;
    }

    public static Message obtain(IView v, Class presenter) {
        Message m = obtain();
        m.target = v;
        m.presenter = presenter.getSimpleName();
        return m;
    }

    public static Message obtain(IView v, Object obj, Class presenter) {
        Message m = obtain();
        m.target = v;
        m.obj = obj;
        m.presenter = presenter.getSimpleName();
        return m;
    }

    public static Message obtain(IView v, Object[] objs, Class presenter) {
        Message m = obtain();
        m.target = v;
        m.objs = objs;
        m.presenter = presenter.getSimpleName();
        return m;
    }

    public static Message obtain(IView v, int what) {
        Message m = obtain();
        m.target = v;
        m.what = what;
        return m;
    }

    public static Message obtain(IView v, int what, Object obj) {
        Message m = obtain();
        m.target = v;
        m.what = what;
        m.obj = obj;
        return m;
    }

    public static Message obtain(IView v, int what, int arg1, int arg2) {
        Message m = obtain();
        m.target = v;
        m.what = what;
        m.arg1 = arg1;
        m.arg2 = arg2;
        return m;
    }

    public static Message obtain(IView v, int what, int arg1, int arg2, Object obj) {
        Message m = obtain();
        m.target = v;
        m.what = what;
        m.arg1 = arg1;
        m.arg2 = arg2;
        m.obj = obj;
        return m;
    }

    public boolean isFromPresenter(Class presenter) {
        return this.presenter.equals(presenter.getSimpleName());
    }

    public static void updateCheckRecycle(int targetSdkVersion) {
        if (targetSdkVersion < Build.VERSION_CODES.LOLLIPOP) {
            gCheckRecycle = false;
        }
    }

    public void recycle() {
        if (isInUse()) {
            if (gCheckRecycle) {
                throw new IllegalStateException("This message cannot be recycled because it "
                        + "is still in use.");
            }
            return;
        }
        recycleUnchecked();
    }

    void recycleUnchecked() {
        flags = FLAG_IN_USE;
        what = 0;
        arg1 = 0;
        arg2 = 0;
        obj = null;
        objs = null;
        str = null;
        presenter = null;
        replyTo = null;
        sendingUid = -1;
        target = null;
        data = null;
        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    public void copyFrom(Message o) {
        this.flags = o.flags & ~FLAGS_TO_CLEAR_ON_COPY_FROM;
        this.what = o.what;
        this.str = o.str;
        this.presenter = o.presenter;
        this.arg1 = o.arg1;
        this.arg2 = o.arg2;
        this.obj = o.obj;
        this.objs = o.objs;
        this.replyTo = o.replyTo;
        this.sendingUid = o.sendingUid;
        if (o.data != null) {
            this.data = (Bundle) o.data.clone();
        } else {
            this.data = null;
        }
    }

    public void setTarget(IView target) {
        this.target = target;
    }

    public IView getTarget() {
        return target;
    }

    public Bundle getData() {
        if (data == null) {
            data = new Bundle();
        }

        return data;
    }

    public Bundle peekData() {
        return data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    /**
     * 分发并回收消息<br/>
     * 回调{@link IView#handlePresenterCallback(Message)}
     */
    public void dispatchToIView() {
        if (target == null) throw new IllegalArgumentException("target is null");
        target.handlePresenterCallback(this);
        this.recycleUnchecked();
    }

    /**
     * 分发消息, 需在最后手动回收消息{@link Message#recycle()}<br/>
     * 回调{@link IView#handlePresenterCallback(Message)}
     */
    public void dispatchToIViewNoRecycle() {
        if (target == null) throw new IllegalArgumentException("target is null");
        target.handlePresenterCallback(this);
    }

    public boolean isAsynchronous() {
        return (flags & FLAG_ASYNCHRONOUS) != 0;
    }

    public void setAsynchronous(boolean async) {
        if (async) {
            flags |= FLAG_ASYNCHRONOUS;
        } else {
            flags &= ~FLAG_ASYNCHRONOUS;
        }
    }

    boolean isInUse() {
        return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
    }

    void markInUse() {
        flags |= FLAG_IN_USE;
    }

    private Message() {
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("{");

        if (target != null) {

            b.append(" what=");
            b.append(what);

            if (!TextUtils.isEmpty(presenter)) {
                b.append(" presenter=");
                b.append(presenter);
            }

            if (!TextUtils.isEmpty(str)) {
                b.append(" str=");
                b.append(str);
            }


            if (arg1 != 0) {
                b.append(" arg1=");
                b.append(arg1);
            }

            if (arg2 != 0) {
                b.append(" arg2=");
                b.append(arg2);
            }

            if (obj != null) {
                b.append(" obj=");
                b.append(obj);
            }

            b.append(" target=");
            b.append(target.getClass().getName());
        } else {
            b.append(" barrier=");
            b.append(arg1);
        }

        b.append(" }");
        return b.toString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        public Message createFromParcel(Parcel source) {
            Message msg = Message.obtain();
            msg.readFromParcel(source);
            return msg;
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(what);
        dest.writeInt(arg1);
        dest.writeInt(arg2);
        dest.writeString(str);
        dest.writeString(presenter);
        if (obj != null) {
            try {
                Parcelable p = (Parcelable) obj;
                dest.writeInt(1);
                dest.writeParcelable(p, flags);
            } catch (ClassCastException e) {
                throw new RuntimeException(
                        "Can't marshal non-Parcelable objects across processes.");
            }
        } else {
            dest.writeInt(0);
        }

        if (objs != null) {
            try {
                Parcelable[] p = (Parcelable[]) objs;
                dest.writeInt(1);
                dest.writeParcelableArray(p, flags);
            } catch (ClassCastException e) {
                throw new RuntimeException(
                        "Can't marshal non-Parcelable objects across processes.");
            }
        } else {
            dest.writeInt(0);
        }
        dest.writeBundle(data);
        Messenger.writeMessengerOrNullToParcel(replyTo, dest);
        dest.writeInt(sendingUid);
    }

    private void readFromParcel(Parcel source) {
        what = source.readInt();
        arg1 = source.readInt();
        arg2 = source.readInt();
        str = source.readString();
        presenter = source.readString();
        if (source.readInt() != 0) {
            obj = source.readParcelable(getClass().getClassLoader());
        }
        if (source.readInt() != 0) {
            objs = source.readParcelableArray(getClass().getClassLoader());
        }
        data = source.readBundle();
        replyTo = Messenger.readMessengerOrNullFromParcel(source);
        sendingUid = source.readInt();
    }
}



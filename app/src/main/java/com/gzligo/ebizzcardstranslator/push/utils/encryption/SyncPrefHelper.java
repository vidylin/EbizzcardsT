package com.gzligo.ebizzcardstranslator.push.utils.encryption;

import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * {@link SharedPreferences} 辅助类，你可以通过{@link #setThreadSafe(boolean)}
 * 来开闭线程安全的开关，默认情况下打开开关。
 */
public class SyncPrefHelper extends PrefHelper {

    private volatile boolean threadSafe = true;

    public final boolean isThreadSafe() {
        return threadSafe;
    }

    public final SyncPrefHelper setThreadSafe(boolean threadSafe) {
        this.threadSafe = threadSafe;
        return this;
    }

    public SyncPrefHelper(SharedPreferences pref) {
        super(pref);
    }

    private final byte[] lock = new byte[0];

    @Override
    public final SyncPrefHelper edit(EditorAction action) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.edit(action);
            }
        } else {
            return (SyncPrefHelper) super.edit(action);
        }
    }

    @Override
    public final SyncPrefHelper clear() {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.clear();
            }
        } else {
            return (SyncPrefHelper) super.clear();
        }
    }

    @Override
    public final Map<String, ?> getAll() {
        if (threadSafe) {
            synchronized (lock) {
                return super.getAll();
            }
        } else {
            return super.getAll();
        }
    }

    @Override
    public final String getString(String key, String defValue) {
        if (threadSafe) {
            synchronized (lock) {
                return super.getString(key, defValue);
            }
        } else {
            return super.getString(key, defValue);
        }
    }

    @Override
    public final Set<String> getStringSet(String key, Set<String> defValues) {
        if (threadSafe) {
            synchronized (lock) {
                return super.getStringSet(key, defValues);
            }
        } else {
            return super.getStringSet(key, defValues);
        }
    }

    @Override
    public final int getInt(String key, int defValue) {
        if (threadSafe) {
            synchronized (lock) {
                return super.getInt(key, defValue);
            }
        } else {
            return super.getInt(key, defValue);
        }
    }

    @Override
    public final long getLong(String key, long defValue) {
        if (threadSafe) {
            synchronized (lock) {
                return super.getLong(key, defValue);
            }
        } else {
            return super.getLong(key, defValue);
        }
    }

    @Override
    public final float getFloat(String key, float defValue) {
        if (threadSafe) {
            synchronized (lock) {
                return super.getFloat(key, defValue);
            }
        } else {
            return super.getFloat(key, defValue);
        }
    }

    @Override
    public final boolean getBoolean(String key, boolean defValue) {
        if (threadSafe) {
            synchronized (lock) {
                return super.getBoolean(key, defValue);
            }
        } else {
            return super.getBoolean(key, defValue);
        }
    }

    @Override
    public final boolean contains(String key) {
        if (threadSafe) {
            synchronized (lock) {
                return super.contains(key);
            }
        } else {
            return super.contains(key);
        }
    }

    @Override
    public final SyncPrefHelper putString(String key, String value) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.putString(key, value);
            }
        } else {
            return (SyncPrefHelper) super.putString(key, value);
        }
    }

    @Override
    public final SyncPrefHelper putStringSet(String key, Set<String> values) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.putStringSet(key, values);
            }
        } else {
            return (SyncPrefHelper) super.putStringSet(key, values);
        }
    }

    @Override
    public final SyncPrefHelper putBoolean(String key, boolean value) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.putBoolean(key, value);
            }
        } else {
            return (SyncPrefHelper) super.putBoolean(key, value);
        }
    }

    @Override
    public final SyncPrefHelper putInt(String key, int value) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.putInt(key, value);
            }
        } else {
            return (SyncPrefHelper) super.putInt(key, value);
        }
    }

    @Override
    public final SyncPrefHelper putLong(String key, long value) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.putLong(key, value);
            }
        } else {
            return (SyncPrefHelper) super.putLong(key, value);
        }
    }

    @Override
    public final SyncPrefHelper putFloat(String key, float value) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.putFloat(key, value);
            }
        } else {
            return (SyncPrefHelper) super.putFloat(key, value);
        }
    }

    @Override
    public final SyncPrefHelper remove(String key) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.remove(key);
            }
        } else {
            return (SyncPrefHelper) super.remove(key);
        }
    }

    @Override
    public final SyncPrefHelper putStringArray(String baseKey, String[] array) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.putStringArray(baseKey, array);
            }
        } else {
            return (SyncPrefHelper) super.putStringArray(baseKey, array);
        }
    }

    @Override
    public final String[] getStringArray(String baseKey, String[] defVal) {
        if (threadSafe) {
            synchronized (lock) {
                return super.getStringArray(baseKey, defVal);
            }
        } else {
            return super.getStringArray(baseKey, defVal);
        }
    }

    @Override
    public final SyncPrefHelper putBooleanArray(String baseKey, boolean[] array) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.putBooleanArray(baseKey, array);
            }
        } else {
            return (SyncPrefHelper) super.putBooleanArray(baseKey, array);
        }
    }

    @Override
    public final boolean[] getBooleanArray(String baseKey, boolean[] defVal) {
        if (threadSafe) {
            synchronized (lock) {
                return super.getBooleanArray(baseKey, defVal);
            }
        } else {
            return super.getBooleanArray(baseKey, defVal);
        }
    }

    @Override
    public final SyncPrefHelper putIntArray(String baseKey, int[] array) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.putIntArray(baseKey, array);
            }
        } else {
            return (SyncPrefHelper) super.putIntArray(baseKey, array);
        }
    }

    @Override
    public final int[] getIntArray(String baseKey, int[] defVal) {
        if (threadSafe) {
            synchronized (lock) {
                return super.getIntArray(baseKey, defVal);
            }
        } else {
            return super.getIntArray(baseKey, defVal);
        }
    }

    @Override
    public final SyncPrefHelper putLongArray(String baseKey, long[] array) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.putLongArray(baseKey, array);
            }
        } else {
            return (SyncPrefHelper) super.putLongArray(baseKey, array);
        }
    }

    @Override
    public final long[] getLongArray(String baseKey, long[] defVal) {
        if (threadSafe) {
            synchronized (lock) {
                return super.getLongArray(baseKey, defVal);
            }
        } else {
            return super.getLongArray(baseKey, defVal);
        }
    }

    @Override
    public final SyncPrefHelper putFloatArray(String baseKey, float[] array) {
        if (threadSafe) {
            synchronized (lock) {
                return (SyncPrefHelper) super.putFloatArray(baseKey, array);
            }
        } else {
            return (SyncPrefHelper) super.putFloatArray(baseKey, array);
        }
    }

    @Override
    public final float[] getFloatArray(String baseKey, float[] defVal) {
        if (threadSafe) {
            synchronized (lock) {
                return super.getFloatArray(baseKey, defVal);
            }
        } else {
            return super.getFloatArray(baseKey, defVal);
        }
    }

}

package com.gzligo.ebizzcardstranslator.push.utils.encryption;

import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

public class PrefHelper {

    private final SharedPreferences pref;
    private volatile boolean applyOrCommit = true;

    public final PrefHelper setApplyOrCommit(boolean applyOrCommit) {
        this.applyOrCommit = applyOrCommit;
        return this;
    }

    public PrefHelper(SharedPreferences pref) {
        this.pref = pref;
    }

    /*Get & Set*/

    public interface EditorAction{

        void onEditor(SharedPreferences.Editor editor);

    }

    public PrefHelper edit(EditorAction action){
        SharedPreferences.Editor editor = pref.edit();
        action.onEditor(editor);
        submitEdit(editor);
        return this;
    }

    public PrefHelper clear() {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        submitEdit(editor);
        return this;
    }

    public Map<String, ?> getAll() {
        return pref.getAll();
    }

    public String getString(String key, String defValue) {
        return pref.getString(key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        return pref.getStringSet(key, defValues);
    }

    public int getInt(String key, int defValue) {
        return pref.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return pref.getLong(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return pref.getFloat(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return pref.getBoolean(key, defValue);
    }

    public boolean contains(String key) {
        return pref.contains(key);
    }

    public PrefHelper putString(String key, String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        submitEdit(editor);
        return this;
    }

    public PrefHelper putStringSet(String key, Set<String> values) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(key, values);
        submitEdit(editor);
        return this;
    }

    public PrefHelper putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        submitEdit(editor);
        return this;
    }

    public PrefHelper putInt(String key, int value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        submitEdit(editor);
        return this;
    }

    public PrefHelper putLong(String key, long value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        submitEdit(editor);
        return this;
    }

    public PrefHelper putFloat(String key, float value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, value);
        submitEdit(editor);
        return this;
    }

    public PrefHelper remove(String key) {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        submitEdit(editor);
        return this;
    }

    private void submitEdit(SharedPreferences.Editor editor) {
        if (applyOrCommit) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    /* Ext */

    private static final String TAG_COUNT = "COUNT";

    private String buildItemKey(String baseKey, int index) {
        return baseKey + index;
    }

    private String buildCountKey(String baseKey) {
        return baseKey + TAG_COUNT;
    }

    public PrefHelper putStringArray(String baseKey, String[] array) {
        SharedPreferences.Editor editor = pref.edit();

        int len = array.length;
        for (int i = 0; i < len; i++) {
            editor.putString(buildItemKey(baseKey, i), array[i]);
        }

        editor.putInt(buildCountKey(baseKey), len);

        submitEdit(editor);
        return this;
    }

    public String[] getStringArray(String baseKey, String[] defVal) {
        int count = pref.getInt(buildCountKey(baseKey), -1);

        String[] val = null;
        if (count >= 0) {
            val = new String[count];
            for (int i = 0; i < count; i++) {
                val[i] = pref.getString(buildItemKey(baseKey, i), null);
            }
        }

        return val != null ? val : defVal;
    }

    public PrefHelper putBooleanArray(String baseKey, boolean[] array) {
        SharedPreferences.Editor editor = pref.edit();

        int len = array.length;
        for (int i = 0; i < len; i++) {
            editor.putBoolean(buildItemKey(baseKey, i), array[i]);
        }

        editor.putInt(buildCountKey(baseKey), len);

        submitEdit(editor);
        return this;
    }

    public boolean[] getBooleanArray(String baseKey, boolean[] defVal) {
        int count = pref.getInt(buildCountKey(baseKey), -1);

        boolean[] val = null;
        if (count >= 0) {
            val = new boolean[count];
            for (int i = 0; i < count; i++) {
                val[i] = pref.getBoolean(buildItemKey(baseKey, i), false);
            }
        }

        return val != null ? val : defVal;
    }

    public PrefHelper putIntArray(String baseKey, int[] array) {
        SharedPreferences.Editor editor = pref.edit();

        int len = array.length;
        for (int i = 0; i < len; i++) {
            editor.putInt(buildItemKey(baseKey, i), array[i]);
        }

        editor.putInt(buildCountKey(baseKey), len);

        submitEdit(editor);
        return this;
    }

    public int[] getIntArray(String baseKey, int[] defVal) {
        int count = pref.getInt(buildCountKey(baseKey), -1);

        int[] val = null;
        if (count >= 0) {
            val = new int[count];
            for (int i = 0; i < count; i++) {
                val[i] = pref.getInt(buildItemKey(baseKey, i), 0);
            }
        }

        return val != null ? val : defVal;
    }

    public PrefHelper putLongArray(String baseKey, long[] array) {
        SharedPreferences.Editor editor = pref.edit();

        int len = array.length;
        for (int i = 0; i < len; i++) {
            editor.putLong(buildItemKey(baseKey, i), array[i]);
        }

        editor.putInt(buildCountKey(baseKey), len);

        submitEdit(editor);
        return this;
    }

    public long[] getLongArray(String baseKey, long[] defVal) {
        int count = pref.getInt(buildCountKey(baseKey), -1);

        long[] val = null;
        if (count >= 0) {
            val = new long[count];
            for (int i = 0; i < count; i++) {
                val[i] = pref.getLong(buildItemKey(baseKey, i), 0);
            }
        }

        return val != null ? val : defVal;
    }

    public PrefHelper putFloatArray(String baseKey, float[] array) {
        SharedPreferences.Editor editor = pref.edit();

        int len = array.length;
        for (int i = 0; i < len; i++) {
            editor.putFloat(buildItemKey(baseKey, i), array[i]);
        }

        editor.putInt(buildCountKey(baseKey), len);

        submitEdit(editor);
        return this;
    }

    public float[] getFloatArray(String baseKey, float[] defVal) {
        int count = pref.getInt(buildCountKey(baseKey), -1);

        float[] val = null;
        if (count >= 0) {
            val = new float[count];
            for (int i = 0; i < count; i++) {
                val[i] = pref.getFloat(buildItemKey(baseKey, i), 0);
            }
        }

        return val != null ? val : defVal;
    }
}

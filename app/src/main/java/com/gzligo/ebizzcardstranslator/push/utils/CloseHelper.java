package com.gzligo.ebizzcardstranslator.push.utils;

import android.database.Cursor;
import android.util.Log;

import java.io.Closeable;

public class CloseHelper {

    public static void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }


    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            Log.e("CloseHelper", "closeQuietly Exception: " + e.getMessage());
        }
    }
}
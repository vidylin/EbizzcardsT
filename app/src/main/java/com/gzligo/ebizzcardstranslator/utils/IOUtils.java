package com.gzligo.ebizzcardstranslator.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by Lwd on 2017/6/13.
 */

public class IOUtils {
    public static byte[] readFully(InputStream in) throws IOException {
        int var1 = -1;
        boolean var2 = true;
        byte[] var3 = new byte[0];
        if (var1 == -1) {
            var1 = Integer.MAX_VALUE;
        }

        int var6;
        for (int var4 = 0; var4 < var1; var4 += var6) {
            int var5;
            if (var4 >= var3.length) {
                var5 = Math.min(var1 - var4, var3.length + 1024);
                if (var3.length < var4 + var5) {
                    var3 = Arrays.copyOf(var3, var4 + var5);
                }
            } else {
                var5 = var3.length - var4;
            }

            var6 = in.read(var3, var4, var5);
            if (var6 < 0) {
                if (var2 && var1 != Integer.MAX_VALUE) {
                    throw new EOFException("Detect premature EOF");
                }

                if (var3.length != var4) {
                    var3 = Arrays.copyOf(var3, var4);
                }
                break;
            }
        }

        return var3;
    }
}

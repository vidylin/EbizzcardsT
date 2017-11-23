package com.gzligo.ebizzcardstranslator.push.utils.encryption;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gzligo.ebizzcardstranslator.push.utils.FileUtils;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by Loren.Li on 2017/5/24.
 */
public abstract class AbsEncryptor {

    private final Charset charset;

    public Charset getCharset() {
        return charset;
    }

    public AbsEncryptor(Charset charset) {
        this.charset = charset != null ? charset : Charset.defaultCharset();
    }

    /*Impl*/

    public final byte[] encrypt(byte[] src) {
        try {
            return doEncrypt(src);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public final byte[] decrypt(byte[] cipher) {
        try {
            return doDecrypt(cipher);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    protected abstract byte[] doEncrypt(byte[] src) throws Exception;

    protected abstract byte[] doDecrypt(byte[] cipher) throws Exception;

	/*Ext*/

    public final String encryptHex(String src) {
        byte[] encrypted = encrypt(src.getBytes(charset));
        return EncodeUtil.encodeHex(encrypted);
    }

    public final String decryptHex(String hexStr) {
        byte[] cipher = EncodeUtil.decodeHex(hexStr);
        byte[] decrypted = decrypt(cipher);
        return new String(decrypted, charset);
    }

    public final String encryptBase64(String src) {
        byte[] encrypted = encrypt(src.getBytes(charset));
        return EncodeUtil.encodeBase64(encrypted);
    }

    public final String decryptBase64(String base64) {
        byte[] cipher = EncodeUtil.decodeBase64(base64);
        byte[] decrypted = decrypt(cipher);
        return new String(decrypted, charset);
    }

    /*Salt&Key*/

    public static final String DEPREACATED_SECURE_ALGORITHM_SHA1PRNG = "SHA1PRNG";
    public static final String DEPREACATED_SECURE_PROVIDER_CRYPTO = "Crypto";

    public static final String SECURE_ALGORITHM_PBKDF2WITHHMACSHA1 = "PBKDF2WithHmacSHA1";

    public static final int KEY_BITS_LENGTH_128 = 128;
    public static final int KEY_BITS_LENGTH_192 = 192;
    public static final int KEY_BITS_LENGTH_256 = 256;

    public static final int ITERATION_COUNT = 1000;

    /**
     * 创建或获取已缓存的盐值。
     *
     * @param saltLen
     * @param pref    用于保存配置的{@link SharedPreferences}，如果为 null 则默认使用{@link }来存储数据。
     * @param key
     * @return
     */
    public static byte[] getOrCreateSalt(int saltLen, @Nullable SharedPreferences pref, @NonNull String key) {
        String saltHex = pref.getString(key, null);
        byte[] salt = !TextUtils.isEmpty(saltHex) ? EncodeUtil.decodeHex(saltHex) : null;
        if (salt == null) {
            salt = newSecureSalt(saltLen);
            saltHex = EncodeUtil.encodeHex(salt);
            if (pref != null) {
                pref.edit().putString(key, saltHex).apply();
            }
        }
        return salt;
    }

    public static byte[] newSecureSalt(int saltLen) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltLen];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 从 password 中获取 key。
     * <p>
     * 该方法会将初次创建的盐值保存在{@link SharedPreferences}中，
     * 使得每次创建的 key 都是一致的。
     *
     * @param password
     * @param keyBitLen
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @see #getOrDeriveKey(String, int, Integer, SharedPreferences, String)
     */
    public static byte[] getOrDeriveKey(String password, int keyBitLen)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        return getOrDeriveKey(password, keyBitLen, null, null, null);
    }

    /**
     * 从 password 中获取 key。
     * <p>
     * 该方法会将初次创建的盐值保存在{@link SharedPreferences}中，
     * 使得每次创建的 key 都是一致的。
     *
     * @param password
     * @param keyBitLen
     * @param pref
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @see #getOrDeriveKey(String, int, Integer, SharedPreferences, String)
     */
    public static byte[] getOrDeriveKey(String password, int keyBitLen, @Nullable SharedPreferences pref)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        return getOrDeriveKey(password, keyBitLen, null, pref, null);
    }

    /**
     * 从 password 中获取 key。
     * <p>
     * 该方法会将初次创建的盐值保存在{@link SharedPreferences}中，
     * 使得每次创建的 key 都是一致的。
     *
     * @param password
     * @param keyBitLen
     * @param iterationCount 用于创建{@link PBEKeySpec}，为 null 时默认值是{@link #ITERATION_COUNT}
     * @param pref           用于保存配置的{@link SharedPreferences}，如果为 null 则默认使用{@link }来存储数据。
     * @param saltName       用于保存盐的 hex 字符串的 key，
     *                       为 null 时将使用 AES+password+keyBitLen 拼接成的字符串的 MD5值。
     * @return
     */
    public static byte[] getOrDeriveKey(String password, int keyBitLen,
                                        @Nullable Integer iterationCount, @Nullable SharedPreferences pref,
                                        @Nullable String saltName)
            throws InvalidKeySpecException, NoSuchAlgorithmException {

        if (TextUtils.isEmpty(saltName)) {
            saltName = FileUtils.digestMD5(password + keyBitLen);
        }
        int saltLen = keyBitLen / 8;
        byte[] salt = getOrCreateSalt(saltLen, pref, saltName);
        return deriveKey(password, keyBitLen, salt, iterationCount);
    }

    /**
     * * 从 password 中获取 key。
     * <p>
     * 注意，由于7.0移除了部分加密相关的类因而每次使用同一个
     * password 调用该方法也可能输出不同的 key 值。
     * <p>
     * 建议优先使用{@link #getOrDeriveKey(String, int, Integer, SharedPreferences, String)}
     *
     * @param password
     * @param keyBitLen
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static byte[] deriveKey(String password, int keyBitLen) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return deriveKey(password, keyBitLen, null, null);
    }

    /**
     * 从 password 中获取 key。
     * <p>
     * 注意，由于7.0移除了部分加密相关的类因而每次使用同一个
     * password 调用该方法也可能输出不同的 key 值。
     * <p>
     * 建议优先使用{@link #getOrDeriveKey(String, int, Integer, SharedPreferences, String)}
     *
     * @param password
     * @param keyBitLen      可选值如下：{@link #KEY_BITS_LENGTH_128}、{@link #KEY_BITS_LENGTH_192}、{@link #KEY_BITS_LENGTH_256}(为 null 时的默认值)
     * @param iterationCount 用于创建{@link PBEKeySpec}，为 null 时默认值是{@link #ITERATION_COUNT}
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static byte[] deriveKey(String password, int keyBitLen, @Nullable byte[] secureSalt, @Nullable Integer iterationCount) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int saltLen = keyBitLen / 8;
        iterationCount = iterationCount != null ? iterationCount : ITERATION_COUNT;

        secureSalt = secureSalt != null ? secureSalt : newSecureSalt(saltLen);
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), secureSalt, iterationCount, keyBitLen);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SECURE_ALGORITHM_PBKDF2WITHHMACSHA1);
        return keyFactory.generateSecret(keySpec).getEncoded();
    }

    @Deprecated
    public static byte[] deriveKeyInsecurely(String password, int keyBitLen) {
        byte[] passwordBytes = password.getBytes(Charset.forName("US_ASCII"));
        return InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(passwordBytes, keyBitLen);
    }

    @Deprecated
    public static byte[] deriveKeyDeprecated(String password, String algorithm, int keyBitLen) throws NoSuchProviderException, NoSuchAlgorithmException {
        return deriveKeyDeprecated(password, algorithm, keyBitLen, null);
    }

    @Deprecated
    public static byte[] deriveKeyDeprecated(String password, String algorithm, int keyBitLen, @Nullable Charset charset) throws NoSuchProviderException, NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance(DEPREACATED_SECURE_ALGORITHM_SHA1PRNG, DEPREACATED_SECURE_PROVIDER_CRYPTO);
        secureRandom.setSeed(password.getBytes(charset != null ? charset : Charset.defaultCharset()));

        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(keyBitLen, secureRandom); // 192 and 256 bits m-ay not be available
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * Detail：https://android-developers.googleblog.com/2016/06/security-crypto-provider-deprecated-in.html
     * <p>
     * Stripped-down version of the SHA1PRNG provided by the Crypto provider.
     * <p>
     * The Crypto provider that offers this functionality was deprecated on Android.
     * <p>
     * Use this class only to retrieve encrypted data that couldn't be retrieved otherwise.
     */
    private static class InsecureSHA1PRNGKeyDerivator {

        /**
         * Only public method. Derive a key from the given seed.
         * <p>
         * Use this method only to retrieve encrypted data that couldn't be retrieved otherwise.
         *
         * @param seed      seed used for the random generator, usually coming from a password
         * @param kenBitLen length of bits
         */
        public static byte[] deriveInsecureKey(byte[] seed, int kenBitLen) {
            int byteLen = kenBitLen / 8;

            InsecureSHA1PRNGKeyDerivator derivator = new InsecureSHA1PRNGKeyDerivator();
            derivator.setSeed(seed);
            byte[] key = new byte[byteLen];
            derivator.nextBytes(key);
            return key;
        }

        // constants to use in expressions operating on bytes in int and long variables:
        // END_FLAGS - final bytes in words to append to message;
        //             see "ch.5.1 Padding the Message, FIPS 180-2"
        // RIGHT1    - shifts to right for left half of long
        // RIGHT2    - shifts to right for right half of long
        // LEFT      - shifts to left for bytes
        // MASK      - mask to select counter's bytes after shift to right

        private static final int[] END_FLAGS = {0x80000000, 0x800000, 0x8000, 0x80};

        private static final int[] RIGHT1 = {0, 40, 48, 56};

        private static final int[] RIGHT2 = {0, 8, 16, 24};

        private static final int[] LEFT = {0, 24, 16, 8};

        private static final int[] MASK = {0xFFFFFFFF, 0x00FFFFFF, 0x0000FFFF,
                0x000000FF};

        // HASHBYTES_TO_USE defines # of bytes returned by "computeHash(byte[])"
        // to use to form byte array returning by the "nextBytes(byte[])" method
        // Note, that this implementation uses more bytes than it is defined
        // in the above specification.
        private static final int HASHBYTES_TO_USE = 20;

        // value of 16 defined in the "SECURE HASH STANDARD", FIPS PUB 180-2
        private static final int FRAME_LENGTH = 16;

        // miscellaneous constants defined in this implementation:
        // COUNTER_BASE - initial value to set to "counter" before computing "nextBytes(..)";
        //                note, that the exact value is not defined in STANDARD
        // HASHCOPY_OFFSET   - offset for copy of current hash in "copies" array
        // EXTRAFRAME_OFFSET - offset for extra frame in "copies" array;
        //                     as the extra frame follows the current hash frame,
        //                     EXTRAFRAME_OFFSET is equal to length of current hash frame
        // FRAME_OFFSET      - offset for frame in "copies" array
        // MAX_BYTES - maximum # of seed bytes processing which doesn't require extra frame
        //             see (1) comments on usage of "seed" array below and
        //             (2) comments in "engineNextBytes(byte[])" method
        //
        // UNDEFINED  - three states of engine; initially its state is "UNDEFINED"
        // SET_SEED     call to "engineSetSeed"  sets up "SET_SEED" state,
        // NEXT_BYTES   call to "engineNextByte" sets up "NEXT_BYTES" state

        private static final int COUNTER_BASE = 0;

        private static final int HASHCOPY_OFFSET = 0;

        private static final int EXTRAFRAME_OFFSET = 5;

        private static final int FRAME_OFFSET = 21;

        private static final int MAX_BYTES = 48;

        private static final int UNDEFINED = 0;

        private static final int SET_SEED = 1;

        private static final int NEXT_BYTES = 2;

        // Structure of "seed" array:
        // -  0-79 - words for computing hash
        // - 80    - unused
        // - 81    - # of seed bytes in current seed frame
        // - 82-86 - 5 words, current seed hash
        private transient int[] seed;

        // total length of seed bytes, including all processed
        private transient long seedLength;

        // Structure of "copies" array
        // -  0-4  - 5 words, copy of current seed hash
        // -  5-20 - extra 16 words frame;
        //           is used if final padding exceeds 512-bit length
        // - 21-36 - 16 word frame to store a copy of remaining bytes
        private transient int[] copies;

        // ready "next" bytes; needed because words are returned
        private transient byte[] nextBytes;

        // index of used bytes in "nextBytes" array
        private transient int nextBIndex;

        // variable required according to "SECURE HASH STANDARD"
        private transient long counter;

        // contains int value corresponding to engine's current state
        private transient int state;

        /**
         * constant defined in "SECURE HASH STANDARD"
         */
        private static final int H0 = 0x67452301;


        /**
         * constant defined in "SECURE HASH STANDARD"
         */
        private static final int H1 = 0xEFCDAB89;


        /**
         * constant defined in "SECURE HASH STANDARD"
         */
        private static final int H2 = 0x98BADCFE;


        /**
         * constant defined in "SECURE HASH STANDARD"
         */
        private static final int H3 = 0x10325476;


        /**
         * constant defined in "SECURE HASH STANDARD"
         */
        private static final int H4 = 0xC3D2E1F0;


        /**
         * offset in buffer to store number of bytes in 0-15 word frame
         */
        private static final int BYTES_OFFSET = 81;


        /**
         * offset in buffer to store current hash value
         */
        private static final int HASH_OFFSET = 82;


        /**
         * # of bytes in H0-H4 words; <BR>
         * in this implementation # is set to 20 (in general # varies from 1 to 20)
         */
        private static final int DIGEST_LENGTH = 20;

        // The "seed" array is used to compute both "current seed hash" and "next bytes".
        //
        // As the "SHA1" algorithm computes a hash of entire seed by splitting it into
        // a number of the 512-bit length frames (512 bits = 64 bytes = 16 words),
        // "current seed hash" is a hash (5 words, 20 bytes) for all previous full frames;
        // remaining bytes are stored in the 0-15 word frame of the "seed" array.
        //
        // As for calculating "next bytes",
        // both remaining bytes and "current seed hash" are used,
        // to preserve the latter for following "setSeed(..)" commands,
        // the following technique is used:
        // - upon getting "nextBytes(byte[])" invoked, single or first in row,
        //   which requires computing new hash, that is,
        //   there is no more bytes remaining from previous "next bytes" computation,
        //   remaining bytes are copied into the 21-36 word frame of the "copies" array;
        // - upon getting "setSeed(byte[])" invoked, single or first in row,
        //   remaining bytes are copied back.

        private InsecureSHA1PRNGKeyDerivator() {
            seed = new int[HASH_OFFSET + EXTRAFRAME_OFFSET];
            seed[HASH_OFFSET] = H0;
            seed[HASH_OFFSET + 1] = H1;
            seed[HASH_OFFSET + 2] = H2;
            seed[HASH_OFFSET + 3] = H3;
            seed[HASH_OFFSET + 4] = H4;

            seedLength = 0;
            copies = new int[2 * FRAME_LENGTH + EXTRAFRAME_OFFSET];
            nextBytes = new byte[DIGEST_LENGTH];
            nextBIndex = HASHBYTES_TO_USE;
            counter = COUNTER_BASE;
            state = UNDEFINED;
        }

        /*
         * The method invokes the SHA1Impl's "updateHash(..)" method
         * to update current seed frame and
         * to compute new intermediate hash value if the frame is full.
         *
         * After that it computes a length of whole seed.
         */
        private void updateSeed(byte[] bytes) {

            // on call:   "seed" contains current bytes and current hash;
            // on return: "seed" contains new current bytes and possibly new current hash
            //            if after adding, seed bytes overfill its buffer
            updateHash(seed, bytes, 0, bytes.length - 1);

            seedLength += bytes.length;
        }

        /**
         * Changes current seed by supplementing a seed argument to the current seed,
         * if this already set;
         * the argument is used as first seed otherwise. <BR>
         * <p>
         * The method overrides "engineSetSeed(byte[])" in class SecureRandomSpi.
         *
         * @param seed - byte array
         * @throws NullPointerException - if null is passed to the "seed" argument
         */
        private void setSeed(byte[] seed) {
            if (seed == null) {
                throw new NullPointerException("seed == null");
            }

            if (state == NEXT_BYTES) { // first setSeed after NextBytes; restoring hash
                System.arraycopy(copies, HASHCOPY_OFFSET, this.seed, HASH_OFFSET,
                        EXTRAFRAME_OFFSET);
            }
            state = SET_SEED;

            if (seed.length != 0) {
                updateSeed(seed);
            }
        }

        /**
         * Writes random bytes into an array supplied.
         * Bits in a byte are from left to right. <BR>
         * <p>
         * To generate random bytes, the "expansion of source bits" method is used,
         * that is,
         * the current seed with a 64-bit counter appended is used to compute new bits.
         * The counter is incremented by 1 for each 20-byte output. <BR>
         * <p>
         * The method overrides engineNextBytes in class SecureRandomSpi.
         *
         * @param bytes - byte array to be filled in with bytes
         * @throws NullPointerException - if null is passed to the "bytes" argument
         */
        protected synchronized void nextBytes(byte[] bytes) {

            int i, n;

            long bits; // number of bits required by Secure Hash Standard
            int nextByteToReturn; // index of ready bytes in "bytes" array
            int lastWord; // index of last word in frame containing bytes

            // This is a bug since words are 4 bytes. Android used to keep it this way for backward
            // compatibility.
            final int extrabytes = 7;// # of bytes to add in order to computer # of 8 byte words

            if (bytes == null) {
                throw new NullPointerException("bytes == null");
            }

            // This is a bug since extraBytes == 7 instead of 3. Android used to keep it this way for
            // backward compatibility.
            lastWord = seed[BYTES_OFFSET] == 0 ? 0
                    : (seed[BYTES_OFFSET] + extrabytes) >> 3 - 1;

            if (state == UNDEFINED) {

                throw new IllegalStateException("No seed supplied!");

            } else if (state == SET_SEED) {

                System.arraycopy(seed, HASH_OFFSET, copies, HASHCOPY_OFFSET,
                        EXTRAFRAME_OFFSET);

                // possible cases for 64-byte frame:
                //
                // seed bytes < 48      - remaining bytes are enough for all, 8 counter bytes,
                //                        0x80, and 8 seedLength bytes; no extra frame required
                // 48 < seed bytes < 56 - remaining 9 bytes are for 0x80 and 8 counter bytes
                //                        extra frame contains only seedLength value at the end
                // seed bytes > 55      - extra frame contains both counter's bytes
                //                        at the beginning and seedLength value at the end;
                //                        note, that beginning extra bytes are not more than 8,
                //                        that is, only 2 extra words may be used

                // no need to set to "0" 3 words after "lastWord" and
                // more than two words behind frame
                for (i = lastWord + 3; i < FRAME_LENGTH + 2; i++) {
                    seed[i] = 0;
                }

                bits = (seedLength << 3) + 64; // transforming # of bytes into # of bits

                // putting # of bits into two last words (14,15) of 16 word frame in
                // seed or copies array depending on total length after padding
                if (seed[BYTES_OFFSET] < MAX_BYTES) {
                    seed[14] = (int) (bits >>> 32);
                    seed[15] = (int) (bits & 0xFFFFFFFF);
                } else {
                    copies[EXTRAFRAME_OFFSET + 14] = (int) (bits >>> 32);
                    copies[EXTRAFRAME_OFFSET + 15] = (int) (bits & 0xFFFFFFFF);
                }

                nextBIndex = HASHBYTES_TO_USE; // skipping remaining random bits
            }
            state = NEXT_BYTES;

            if (bytes.length == 0) {
                return;
            }

            nextByteToReturn = 0;

            // possibly not all of HASHBYTES_TO_USE bytes were used previous time
            n = (HASHBYTES_TO_USE - nextBIndex) < (bytes.length - nextByteToReturn) ? HASHBYTES_TO_USE
                    - nextBIndex
                    : bytes.length - nextByteToReturn;
            if (n > 0) {
                System.arraycopy(nextBytes, nextBIndex, bytes, nextByteToReturn, n);
                nextBIndex += n;
                nextByteToReturn += n;
            }

            if (nextByteToReturn >= bytes.length) {
                return; // return because "bytes[]" are filled in
            }

            n = seed[BYTES_OFFSET] & 0x03;
            for (; ; ) {
                if (n == 0) {

                    seed[lastWord] = (int) (counter >>> 32);
                    seed[lastWord + 1] = (int) (counter & 0xFFFFFFFF);
                    seed[lastWord + 2] = END_FLAGS[0];

                } else {

                    seed[lastWord] |= (int) ((counter >>> RIGHT1[n]) & MASK[n]);
                    seed[lastWord + 1] = (int) ((counter >>> RIGHT2[n]) & 0xFFFFFFFF);
                    seed[lastWord + 2] = (int) ((counter << LEFT[n]) | END_FLAGS[n]);
                }
                if (seed[BYTES_OFFSET] > MAX_BYTES) {
                    copies[EXTRAFRAME_OFFSET] = seed[FRAME_LENGTH];
                    copies[EXTRAFRAME_OFFSET + 1] = seed[FRAME_LENGTH + 1];
                }

                computeHash(seed);

                if (seed[BYTES_OFFSET] > MAX_BYTES) {

                    System.arraycopy(seed, 0, copies, FRAME_OFFSET, FRAME_LENGTH);
                    System.arraycopy(copies, EXTRAFRAME_OFFSET, seed, 0,
                            FRAME_LENGTH);

                    computeHash(seed);
                    System.arraycopy(copies, FRAME_OFFSET, seed, 0, FRAME_LENGTH);
                }
                counter++;

                int j = 0;
                for (i = 0; i < EXTRAFRAME_OFFSET; i++) {
                    int k = seed[HASH_OFFSET + i];
                    nextBytes[j] = (byte) (k >>> 24); // getting first  byte from left
                    nextBytes[j + 1] = (byte) (k >>> 16); // getting second byte from left
                    nextBytes[j + 2] = (byte) (k >>> 8); // getting third  byte from left
                    nextBytes[j + 3] = (byte) (k); // getting fourth byte from left
                    j += 4;
                }

                nextBIndex = 0;
                j = HASHBYTES_TO_USE < (bytes.length - nextByteToReturn) ? HASHBYTES_TO_USE
                        : bytes.length - nextByteToReturn;

                if (j > 0) {
                    System.arraycopy(nextBytes, 0, bytes, nextByteToReturn, j);
                    nextByteToReturn += j;
                    nextBIndex += j;
                }

                if (nextByteToReturn >= bytes.length) {
                    break;
                }
            }
        }

        /**
         * The method generates a 160 bit hash value using
         * a 512 bit message stored in first 16 words of int[] array argument and
         * current hash value stored in five words, beginning OFFSET+1, of the array argument.
         * Computation is done according to SHA-1 algorithm.
         * <p>
         * The resulting hash value replaces the previous hash value in the array;
         * original bits of the message are not preserved.
         * <p>
         * No checks on argument supplied, that is,
         * a calling method is responsible for such checks.
         * In case of incorrect array passed to the method
         * either NPE or IndexOutOfBoundException gets thrown by JVM.
         *
         * @params arrW - integer array; arrW.length >= (BYTES_OFFSET+6); <BR>
         * only first (BYTES_OFFSET+6) words are used
         */
        private static void computeHash(int[] arrW) {

            int a = arrW[HASH_OFFSET];
            int b = arrW[HASH_OFFSET + 1];
            int c = arrW[HASH_OFFSET + 2];
            int d = arrW[HASH_OFFSET + 3];
            int e = arrW[HASH_OFFSET + 4];

            int temp;

            // In this implementation the "d. For t = 0 to 79 do" loop
            // is split into four loops. The following constants:
            //     K = 5A827999   0 <= t <= 19
            //     K = 6ED9EBA1  20 <= t <= 39
            //     K = 8F1BBCDC  40 <= t <= 59
            //     K = CA62C1D6  60 <= t <= 79
            // are hex literals in the loops.

            for (int t = 16; t < 80; t++) {

                temp = arrW[t - 3] ^ arrW[t - 8] ^ arrW[t - 14] ^ arrW[t - 16];
                arrW[t] = (temp << 1) | (temp >>> 31);
            }

            for (int t = 0; t < 20; t++) {

                temp = ((a << 5) | (a >>> 27)) +
                        ((b & c) | ((~b) & d)) +
                        (e + arrW[t] + 0x5A827999);
                e = d;
                d = c;
                c = (b << 30) | (b >>> 2);
                b = a;
                a = temp;
            }
            for (int t = 20; t < 40; t++) {

                temp = (((a << 5) | (a >>> 27))) + (b ^ c ^ d) + (e + arrW[t] + 0x6ED9EBA1);
                e = d;
                d = c;
                c = (b << 30) | (b >>> 2);
                b = a;
                a = temp;
            }
            for (int t = 40; t < 60; t++) {

                temp = ((a << 5) | (a >>> 27)) + ((b & c) | (b & d) | (c & d)) +
                        (e + arrW[t] + 0x8F1BBCDC);
                e = d;
                d = c;
                c = (b << 30) | (b >>> 2);
                b = a;
                a = temp;
            }
            for (int t = 60; t < 80; t++) {

                temp = (((a << 5) | (a >>> 27))) + (b ^ c ^ d) + (e + arrW[t] + 0xCA62C1D6);
                e = d;
                d = c;
                c = (b << 30) | (b >>> 2);
                b = a;
                a = temp;
            }

            arrW[HASH_OFFSET] += a;
            arrW[HASH_OFFSET + 1] += b;
            arrW[HASH_OFFSET + 2] += c;
            arrW[HASH_OFFSET + 3] += d;
            arrW[HASH_OFFSET + 4] += e;
        }

        /**
         * The method appends new bytes to existing ones
         * within limit of a frame of 64 bytes (16 words).
         * <p>
         * Once a length of accumulated bytes reaches the limit
         * the "computeHash(int[])" method is invoked on the array to compute updated hash,
         * and the number of bytes in the frame is set to 0.
         * Thus, after appending all bytes, the array contain only those bytes
         * that were not used in computing final hash value yet.
         * <p>
         * No checks on arguments passed to the method, that is,
         * a calling method is responsible for such checks.
         *
         * @params intArray  - int array containing bytes to which to append;
         * intArray.length >= (BYTES_OFFSET+6)
         * @params byteInput - array of bytes to use for the update
         * @params from      - the offset to start in the "byteInput" array
         * @params to        - a number of the last byte in the input array to use,
         * that is, for first byte "to"==0, for last byte "to"==input.length-1
         */
        private static void updateHash(int[] intArray, byte[] byteInput, int fromByte, int toByte) {

            // As intArray contains a packed bytes
            // the buffer's index is in the intArray[BYTES_OFFSET] element

            int index = intArray[BYTES_OFFSET];
            int i = fromByte;
            int maxWord;
            int nBytes;

            int wordIndex = index >> 2;
            int byteIndex = index & 0x03;

            intArray[BYTES_OFFSET] = (index + toByte - fromByte + 1) & 077;

            // In general case there are 3 stages :
            // - appending bytes to non-full word,
            // - writing 4 bytes into empty words,
            // - writing less than 4 bytes in last word

            if (byteIndex != 0) {       // appending bytes in non-full word (as if)

                for (; (i <= toByte) && (byteIndex < 4); i++) {
                    intArray[wordIndex] |= (byteInput[i] & 0xFF) << ((3 - byteIndex) << 3);
                    byteIndex++;
                }
                if (byteIndex == 4) {
                    wordIndex++;
                    if (wordIndex == 16) {          // intArray is full, computing hash

                        computeHash(intArray);
                        wordIndex = 0;
                    }
                }
                if (i > toByte) {                 // all input bytes appended
                    return;
                }
            }

            // writing full words

            maxWord = (toByte - i + 1) >> 2;           // # of remaining full words, may be "0"
            for (int k = 0; k < maxWord; k++) {

                intArray[wordIndex] = (((int) byteInput[i] & 0xFF) << 24) |
                        (((int) byteInput[i + 1] & 0xFF) << 16) |
                        (((int) byteInput[i + 2] & 0xFF) << 8) |
                        (((int) byteInput[i + 3] & 0xFF));
                i += 4;
                wordIndex++;

                if (wordIndex < 16) {     // buffer is not full yet
                    continue;
                }
                computeHash(intArray);      // buffer is full, computing hash
                wordIndex = 0;
            }

            // writing last incomplete word
            // after writing free byte positions are set to "0"s

            nBytes = toByte - i + 1;
            if (nBytes != 0) {

                int w = ((int) byteInput[i] & 0xFF) << 24;

                if (nBytes != 1) {
                    w |= ((int) byteInput[i + 1] & 0xFF) << 16;
                    if (nBytes != 2) {
                        w |= ((int) byteInput[i + 2] & 0xFF) << 8;
                    }
                }
                intArray[wordIndex] = w;
            }

            return;
        }
    }
}

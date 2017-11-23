package com.gzligo.ebizzcardstranslator.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.gzligo.ebizzcardstranslator.constants.FileConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * Created by Lwd on 2017/6/13.
 */

public class FileUtils {
    public static final String TAG = "FileUtils";

    private static File thumbnailDir;

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
                file.delete();
            }
        }
    }


    public static File createFile(Context activity, File dir, String fileName) {
        File file = new File(dir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "create file exception.");
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void saveObject(String dirName, Object obj, String fileName) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        File sdFile = new File(dirName, fileName);
        try {
            if (!sdFile.exists()) {
                sdFile.createNewFile();
            }
            fos = new FileOutputStream(sdFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null)
                    oos.close();
            } catch (Exception e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
            }
        }
    }


    public static boolean copyFile(String oldPath, String newPath) {
        try {
            int byteSum = 0;
            int byteRead = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteRead = inStream.read(buffer)) != -1) {
                    byteSum += byteRead;
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
            Log.i(TAG, "copy File success: " + oldPath + "-->" + newPath);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "copy File error " );
        }
        return false;
    }

    public static File saveBitmap2File(String desc, Bitmap bitmap, String fileName) {
        FileOutputStream fOut = null;
        File flie = null;
        try {
            File checkFile = new File(desc);
            if (!checkFile.exists()) {
                Log.e(TAG, desc + "is not exists.");
                checkFile.mkdirs();
            }

            flie = new File(desc, fileName);
            if (!flie.exists()) {
                flie.createNewFile();
            }
            fOut = new FileOutputStream(flie);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flie;
    }

    public static File saveBitmap2File(String desc, String fileName, Bitmap bitmap, int quality) {
        FileOutputStream fOut = null;
        File flie = null;
        try {
            File checkFile = new File(desc);
            if (!checkFile.exists()) {
                checkFile.mkdirs();
            }
            flie = new File(desc, fileName);
            if (!flie.exists()) {
                flie.createNewFile();
            }
            fOut = new FileOutputStream(flie);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flie;
    }

    public static File streamToFile(String path, InputStream ins) {
        OutputStream os = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
                if (ins != null)
                    ins.close();
            } catch (Exception e) {

            }
        }
        return null;
    }

    public static String convertStreamToString(InputStream is, String charSet) {
        try {
            if (TextUtils.isEmpty(charSet)) {
                charSet = "gbk";
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, charSet));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static Object readObject(String dirName, String fileName) {
        if (dirName == null) {
            Log.e(TAG, dirName + " is null.");
            return null;
        }
        FileInputStream freader = null;
        ObjectInputStream objectInputStream = null;
        Object object = null;
        try {
            File file = new File(dirName, fileName);
            if (!file.exists()) {
                Log.e(TAG, "the file isn't exists .");
                return null;
            }
            freader = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(freader);
            object = objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (freader != null) {
                try {
                    freader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    public static File saveFile(String newPath, byte[] bytes) {

        File file = new File(newPath);
        BufferedOutputStream stream = null;
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(file);
            stream = new BufferedOutputStream(fs);
            if (!file.exists()) {
                file.createNewFile();
            }
            stream.write(bytes);

            Log.i(TAG, "saveFile success -> " + newPath);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null)
                    stream.close();

                if (fs != null) {
                    fs.close();
                }
            } catch (Exception e) {

            }
        }
        return null;
    }

    public static Bitmap compressImageFromFile(float width, float hight, String descPath) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(descPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = hight;//
        float ww = width;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;

        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;

        bitmap = BitmapFactory.decodeFile(descPath, newOpts);
        return bitmap;
    }

    public static File getSmallBitmap(Activity context, String filePath, float widthScale, float heightScale, int quality, String type) {

        File thumbnailDir = new File(context.getFilesDir().getAbsoluteFile(), "ThumbnailPhotos");

        if (!thumbnailDir.exists())
            thumbnailDir.mkdirs();

        String thumbnailName = TimeUtils.stringToMD5(filePath) + type + "_thumbnail.jpg";
        File thumbnailFile = new File(thumbnailDir.getAbsolutePath(), thumbnailName);
        if (thumbnailFile.exists()) {
            Log.i(TAG, "compressImageFromFile from exists thumbnail file.");
            return thumbnailFile;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, (int) (ScreenUtils.getScreenWidth(context) * widthScale), (int) (ScreenUtils.getScreenHeight(context) * heightScale));
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPreferredConfig = null;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inTargetDensity = context.getResources().getDisplayMetrics().densityDpi;
        options.inScaled = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        return saveBitmap2File(thumbnailDir.getAbsolutePath(), thumbnailName, bitmap, quality);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap thumbnailPhoto(Context context, String filePath) {
        long start = System.currentTimeMillis();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        float realWidth = options.outWidth;
        float realHeight = options.outHeight;

        double ratio = realWidth / realHeight;

        System.out.println("真实图片高度：" + realHeight + "宽度:" + realWidth + ", scale =" + ratio);
        int width = 0;
        int height = 0;
        if (realWidth >= realHeight) {
            //width = screen / 2
            width = ScreenUtils.getScreenWidth(context) / 2;
            height = (int) (width / ratio);
        } else if (realWidth < realHeight) {
            width = ScreenUtils.getScreenWidth(context) / 4;
            height = (int) (width / ratio);
        }
        System.out.println("目标图片大小：高度:" + height + "宽度:" + width + " ratio:" + ratio);

        // 计算缩放比
//        int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / 100);
//        if (scale <= 0) {
//            scale = 1;
//        }
//        options.inSampleSize = scale;
//        options.outHeight = 200;
//        options.outWidth = 200;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(filePath, options);
//        int w = bitmap.getWidth();
//        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) width) / realWidth;
        float scaleHeight = ((float) height) / realHeight;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        int w = resultBitmap.getWidth();
        int h = resultBitmap.getHeight();

        long end = System.currentTimeMillis();
        System.out.println("缩略图高度：" + h + "宽度:" + w + ",time =" + (end - start) + ",ratio =" + ratio);
        return resultBitmap;
    }

    public static byte[] streamToByte(InputStream is) {
        ByteArrayOutputStream byteStream = null;
        byte[] imgdata = null;
        try {
            byteStream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                byteStream.write(ch);
            }
            imgdata = byteStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteStream != null) {
                    byteStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return imgdata;
    }


    public static Bitmap pictureRotate(Bitmap bitmap, String descPath) {
        int degree = readPicureRotate(descPath);
        if (degree != 0) {
            Matrix m = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            // rotate angle
            m.setRotate(degree);
            //new bitmap
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);
        }
        return bitmap;
    }

    public static int readPicureRotate(String picturePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(picturePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * file -> byte
     */
    public static byte[] getFileBytes(File file) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            int bytes = (int) file.length();
            byte[] buffer = new byte[bytes];
            int readBytes = bis.read(buffer);
            if (readBytes != buffer.length) {
                throw new IOException("Entire file not read");
            }
            return buffer;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static long getFileSize(File file) {
        if (file == null) {
            Log.w(TAG, "getFileSize file is null??? ");
            return 0;
        }
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.w(TAG, "getFileSize file not exists??? " + file.getAbsolutePath());
        }
        return size;
    }

    public static String encodeBase64(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new String(Base64.encode(buffer, Base64.DEFAULT));
    }

    public static String decodeBase64(String fileString) throws Exception {
        byte[] buffer = Base64.decode(fileString, Base64.DEFAULT);
        return new String(buffer);
    }

    /**
     * get file md5.
     */
    public static String getFileMD5(File file) {

        if (!file.isFile()) {
            Log.e(TAG, file + "isn't file.");
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }



    public static BitmapFactory.Options getImageOptions(byte[] bs) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bs, 0, bs.length, newOpts);
        newOpts.inJustDecodeBounds = false;
        return newOpts;
    }

    public static byte[] hex2byte(String str) {
        if (str == null) {
            return null;
        }

        str = str.trim();
        int len = str.length();

        if (len == 0 || len % 2 == 1) {
            return null;
        }

        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<HashMap<String, String>> getAllPictures(Context context) {
        ArrayList<HashMap<String, String>> picturemaps = new ArrayList<>();
        HashMap<String, String> picturemap;
        ContentResolver cr = context.getContentResolver();
        //先得到缩略图的URL和对应的图片id
        Cursor cursor = cr.query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Images.Thumbnails.IMAGE_ID,
                        MediaStore.Images.Thumbnails.DATA
                },
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            do {
                picturemap = new HashMap<>();
                picturemap.put("image_id_path", cursor.getInt(0) + "");
                picturemap.put("thumbnail_path", cursor.getString(1));
                picturemaps.add(picturemap);
            } while (cursor.moveToNext());
            cursor.close();
        }
        //再得到正常图片的path
        for (int i = 0; i < picturemaps.size(); i++) {
            picturemap = picturemaps.get(i);
            String media_id = picturemap.get("image_id_path");
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.Media.DATA
                    },
                    MediaStore.Audio.Media._ID + "=" + media_id,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                do {
                    picturemap.put("image_id", cursor.getString(0));
                    picturemaps.set(i, picturemap);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return picturemaps;
    }

    public static void copyAssets(Context context, String assetsFilename, String desPath) throws IOException {
        AssetManager manager = context.getAssets();
        final InputStream is = manager.open(assetsFilename);
        copyFile(new File(desPath), is, "");
    }

    public static void copyAssets(Context context, String assetsFilename, File file, String mode) throws IOException {
        AssetManager manager = context.getAssets();
        final InputStream is = manager.open(assetsFilename);
        copyFile(file, is, mode);
    }

    public static void copyFile(File file, InputStream is, String mode) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        final String abspath = file.getAbsolutePath();
        final FileOutputStream out = new FileOutputStream(file);
        byte buf[] = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        is.close();
//        Runtime.getRuntime().exec("chmod " + mode + " " + abspath).waitFor();
    }

    public static void copyFile(String oldPath, String newPathDir, String newFileName,boolean rewrite) {
        File fromFile = new File(oldPath);
        File toFile = new File(newPathDir,newFileName);
        Log.e(TAG,"toFile -->>" + toFile.getAbsolutePath());
        copyFile(fromFile.getAbsolutePath(),toFile.getAbsolutePath(),rewrite);
    }

    public static void copyFile(String oldPath, String newPath, boolean rewrite) {
        File fromFile = new File(oldPath);
        File toFile = new File(newPath);

        Log.i(TAG, "copyFile: " + fromFile.exists() + "," + fromFile.isFile() + "," + fromFile.canRead());

        if (!fromFile.exists()) {
            return;
        }
        if (!fromFile.isFile()) {
            return;
        }
        if (!fromFile.canRead()) {
            return;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            byte[] bt = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            Log.i(TAG, "copyFile success, path: " + newPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getExtension(String filePath) {
        return getExtension(new File(filePath));
    }

    public static String getExtension(File file) {
        String suffix = "";
        String name = file.getName();
        final int idx = name.lastIndexOf(".");
        if (idx > 0) {
            suffix = name.substring(idx + 1);
        }
        return suffix;
    }

    public static String getFileName(String filePath) {
        if (filePath == null) {
            return "";
        }
        return new File(filePath).getName();
    }

    public static long getFileLength(String filePath) {
        if (filePath == null) {
            return 0;
        }

        return new File(filePath).length();
    }

    /**
     * 压缩单个文件
     */
    public static void ZipFile(File file, String zippath) {
        try {
            File zipFile = new File(zippath);
            InputStream input = new FileInputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOut.putNextEntry(new ZipEntry(file.getName()));
            int temp = 0;
            while ((temp = input.read()) != -1) {
                zipOut.write(temp);
            }
            zipOut.flush();
            input.close();
            zipOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取输入流的摘要。摘要默认为小写。
     *
     * @param algorithm
     * @param in
     * @return
     * @throws IOException
     */
    public static String digest(String algorithm, InputStream in) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.reset();
            byte[] buff = new byte[1024];
            int len;
            while ((len = (in.read(buff))) != -1) {
                digest.update(buff, 0, len);
            }
            return EncodeUtil.encodeHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("无法初始化" + algorithm + "算法");
        } finally {
            in.close();
        }
    }

    public static final String ALGORITHM_MD5 = "MD5";

    public static String digestMD5(final String str) {
        try {
            return digest(ALGORITHM_MD5, str, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String digest(String algorithm, String str, @Nullable Charset charset) {
        return digest(algorithm, str.getBytes(charset != null ? charset : Charset.defaultCharset()));
    }

    public static String digest(String algorithm, byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.reset();
            return EncodeUtil.encodeHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("无法初始化" + algorithm + "算法");
        }
    }

    public static Bitmap getVideoFrameAtTime(String videoPath){
        if (TextUtils.isEmpty(videoPath)) {
            return null;
        }
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        File checkFile = new File(videoPath);
        if (FileUtils.getFileSize(checkFile) > 0) {
            media.setDataSource(videoPath);
            Bitmap frameAtBitmap = media.getFrameAtTime();
            return frameAtBitmap;
        }
        return null;
    }

    public static String saveBitmap(String destFileDir,String destFileName,Bitmap bt) {
        File dir = new File(destFileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, destFileName.replace(FileConstants.FILE_SUFFIX_JPG,""));
        try {
            FileOutputStream out = new FileOutputStream(file);
            bt.compress(Bitmap.CompressFormat.JPEG, 30, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file.getPath();
    }
}


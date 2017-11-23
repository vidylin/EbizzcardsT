package com.gzligo.ebizzcardstranslator.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.ProductVideoThumbnailBean;
import com.waynell.videolist.widget.TextureVideoView;

import java.util.Hashtable;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.STOP_VOICE;

/**
 * Created by Lwd on 2017/6/8.
 */

public class CommonUtils {
    public static String getDeviceID() {
        String szImei = "";
        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) AppManager.get().getApplication().getSystemService(TELEPHONY_SERVICE);
            szImei = TelephonyMgr.getDeviceId();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return szImei;
    }

    public static String formatMediaUrl(String portrait) {
        if (TextUtils.isEmpty(portrait))
            return "";
        if (portrait.contains(HttpUtils.MEDIA_HOST)) {
            return portrait;
        }
        return formatMediaUrl(portrait, null);
    }

    public static String formatMediaUrl(String portrait, String type) {
        if (TextUtils.isEmpty(portrait))
            return "";
        if (TextUtils.isEmpty(type))
            return HttpUtils.MEDIA_HOST + portrait;
        String newUrl = HttpUtils.MEDIA_HOST + portrait + type;
        return newUrl;
    }

    //获取缩略图
    public static Observable<Bitmap> getVideoThumbnail(final String filePath) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>(){
            @Override
            public void subscribe(@NonNull ObservableEmitter<Bitmap> emitter) throws Exception {
                emitter.onNext(getBitmap(filePath));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static ProductVideoThumbnailBean createVideoThumbnail(String filePath,String destFileName) {
        Bitmap bitmap = getBitmap(filePath);
        ProductVideoThumbnailBean productVideoThumbnailBean = new ProductVideoThumbnailBean();
        if(null!=bitmap){
            String fileDir = FileManager.getFileManager(AppManager.get().getApplication()).getChatDirImage().getAbsolutePath();
            String fPath = FileUtils.saveBitmap(fileDir,destFileName,bitmap);
            productVideoThumbnailBean.setThumbnailPath(fPath);
            productVideoThumbnailBean.setThumbnailSize(bitmap.getWidth()+"*"+bitmap.getHeight());
        }
        return productVideoThumbnailBean;
    }

    public static Bitmap getBitmap(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (filePath.startsWith("http://")
                    || filePath.startsWith("https://")
                    || filePath.startsWith("widevine://")) {
                retriever.setDataSource(filePath,new Hashtable<String, String>());
            }else {
                retriever.setDataSource(filePath);
            }
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses.size() > 0) {
            ActivityManager.RunningAppProcessInfo appProcess = appProcesses.get(0);
            if (appProcess.processName.equals(context.getPackageName())&&
                    appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static void loadVideoImage(String url, final ImageView imageView, String imgSize, final TextureVideoView videoPlayer) {
        if(TextUtils.isEmpty(imgSize)){
            Glide.with(AppManager.get().getApplication())
                    .load(url)
                    .thumbnail(0.1f).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                    params.height = params.width * height / width;
                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(params.width, params.height));
                    videoPlayer.setLayoutParams(new RelativeLayout.LayoutParams(params.width, params.height));
                    imageView.setImageBitmap(bitmap);
                }
            });
        }else{
            String[] imageSizes = imgSize.split("\\*");
            if(null!=imageSizes&&imageSizes.length==2){
                int width = Integer.parseInt(imageSizes[0]);
                int height = Integer.parseInt(imageSizes[1]);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                params.height = params.width * height / width;
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(params.width, params.height));
                videoPlayer.setLayoutParams(new RelativeLayout.LayoutParams(params.width, params.height));
                if(!TextUtils.isEmpty(url)){
                    imageView.setVisibility(View.VISIBLE);
                    ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                            .builder()
                            .url(url)
                            .imgHeigth(height)
                            .imgWidth(width)
                            .imageView(imageView)
                            .errorPic(R.mipmap.default_head_portrait)
                            .isClearMemory(false)
                            .transformation(new CustomShapeTransformation(AppManager.get().getApplication(),
                                    ChatConstants.CHAT_IMG_SHAPE_TRANS_FORMATION))
                            .build());
                }
            }
        }
    }

    public static void loadVideoSize(final ImageView imageView, String imgSize,TextureVideoView videoPlayer) {
        if(TextUtils.isEmpty(imgSize)){
            return;
        }
        String[] imageSizes = imgSize.split("\\*");
        if(null!=imageSizes&&imageSizes.length==2){
            int width = Integer.parseInt(imageSizes[0]);
            int height = Integer.parseInt(imageSizes[1]);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            params.height = params.width * height / width;
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(params.width, params.height));
            videoPlayer.setLayoutParams(new RelativeLayout.LayoutParams(params.width, params.height));
        }
    }

    public static void playVoice(int position, String filePath, final ImageView imageView, IView iView) {
        imageView.setBackgroundResource(R.drawable.play_voice_left_green);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
        drawable.start();
        boolean isPlaying = MediaManager.getInstance().isPlaying();
        if (isPlaying) {
            int pos = MediaManager.getInstance().getPosition();
            if (pos == position) {
                imageView.setBackgroundResource(R.mipmap.voice_play_green_three);
                MediaManager.getInstance().reset();
                MediaManager.getInstance().setPosition(-1);
                return;
            } else {
                Message message = Message.obtain(iView);
                message.what = STOP_VOICE;
                message.arg1 = pos;
                message.dispatchToIView();
            }
        }
        MediaManager.getInstance().setPosition(position);
        MediaManager.getInstance().playSound(filePath, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                imageView.setBackgroundResource(R.mipmap.voice_play_green_three);
                MediaManager.getInstance().reset();
                MediaManager.getInstance().setPosition(-1);
            }
        }, new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

            }
        });
    }
}

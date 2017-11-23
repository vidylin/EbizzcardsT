package com.gzligo.ebizzcardstranslator.business.chat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.common.PreImageViewPager;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.persistence.ChatMsgProperty;
import com.gzligo.ebizzcardstranslator.utils.MediaPlayerCallbackUtils;
import com.gzligo.ebizzcardstranslator.utils.ScreenUtils;
import com.waynell.videolist.widget.TextureVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;

public class PreImageActivity extends BaseActivity<PreImagePresenter> {
    @BindView(R.id.pre_img_view_pager) PreImageViewPager preViewPager;
    @BindView(R.id.preview_actionbar) ToolActionBar previewActionbar;
    private int totalNum = -1;
    private int pos;
    private List<ChatMsgProperty> chatMsgProperties;

    @Override
    public PreImagePresenter createPresenter() {
        return new PreImagePresenter(new ChatRepository(), this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_pre_image;
    }

    @Override
    public void initData() {
        if(null==chatMsgProperties){
            chatMsgProperties = new ArrayList<>();
        }
        ArrayList<ChatMsgProperty> chatMsgList = (ArrayList<ChatMsgProperty>) getIntent().getSerializableExtra("CHAT_MSG_LIST");
        pos = getIntent().getIntExtra("POSITION",0);
        chatMsgProperties.addAll(chatMsgList);
        totalNum = chatMsgList.size();
    }

    @Override
    public void initViews() {
        previewActionbar.setCenterTitle(pos + 1 + "" + "/" + totalNum);
        preViewPager.setAdapter(new SamplePagerAdapter(chatMsgProperties, this));
        preViewPager.setCurrentItem(pos);
    }

    @Override
    public void initEvents() {
        preViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (totalNum != -1) {
                    previewActionbar.setCenterTitle(position + 1 + "" + "/" + totalNum);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick({R.id.tv_close})
    void click() {
        finish();
    }

    class SamplePagerAdapter extends PagerAdapter {
        private List<ChatMsgProperty> imagePaths;
        private Activity context;
        private Map<Integer, Bitmap> videoCoverMap;

        public SamplePagerAdapter(List<ChatMsgProperty> imagePaths, Activity context) {
            this.imagePaths = imagePaths;
            this.context = context;
            if (null == videoCoverMap) {
                videoCoverMap = new HashMap<>();
            }
        }

        @Override
        public int getCount() {
            return imagePaths.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            ChatMsgProperty chatMessageBean = imagePaths.get(position);
            int type = chatMessageBean.getType();
            View view = null;
            String filePath = chatMessageBean.getFilePath();
            String fileUrl = chatMessageBean.getFileUrl();
            String videoPath = chatMessageBean.getVideoThumbnailPath();
            switch (type) {
                case 2:
                    view = LayoutInflater.from(container.getContext()).inflate(R.layout.activity_video, null, false);
                    final TextureVideoView textureVideoView = (TextureVideoView) view.findViewById(R.id.video_player_vv);
                    final ImageView videoCover = (ImageView) view.findViewById(R.id.video_cover);
                    final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.video_down_progress);
                    final ImageView playVideoImg = (ImageView) view.findViewById(R.id.play_video_img);
                    container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    if(null!=filePath){
                        textureVideoView.setVideoPath(filePath);
                    }else if(null!=fileUrl){
                        textureVideoView.setVideoPath(fileUrl);
                    }
                    textureVideoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!textureVideoView.isPlaying()) {
                                textureVideoView.start();
                                progressBar.setVisibility(View.VISIBLE);
                                playVideoImg.setVisibility(View.GONE);
                            } else {
                                textureVideoView.stop();
                                context.finish();
                            }
                        }
                    });
                    textureVideoView.setMediaPlayerCallback(new MediaPlayerCallbackUtils() {

                        @Override
                        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                            videoCover.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            textureVideoView.stop();
                            return false;
                        }
                    });

                    if (null != videoPath) {
                        serverVideoImage(videoPath,videoCover,textureVideoView);
                    }else if(null!=filePath){
                        serverVideoImage(filePath,videoCover,textureVideoView);
                    }else if(null!=fileUrl){
                        serverVideoImage(fileUrl,videoCover,textureVideoView);
                    }
                    break;
                case 1:
                    PhotoView photoView = new PhotoView(container.getContext());
                    if(filePath!=null){
                        photoView.setImageURI(Uri.parse(filePath));
                    }else if(null!=fileUrl){
                        loadVideoImage(fileUrl,photoView);
                    }
                    container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    photoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.finish();
                        }
                    });
                    view = photoView;
                    break;
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private static void loadVideoImage(String url, final ImageView imageView) {
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(url)
                .imageView(imageView)
                .errorPic(R.mipmap.default_head_portrait)
                .isClearMemory(false)
                .build());
    }

    private void serverVideoImage(String url, final ImageView imageView, final TextureVideoView textureVideoView){
        Glide.with(AppManager.get().getApplication()).load(url).thumbnail(0.1f).into(new SimpleTarget<Drawable>() {

            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                int width = resource.getIntrinsicWidth();
                int height = resource.getIntrinsicHeight();
                int screenWidth = ScreenUtils.getScreenWidth(AppManager.get().getApplication());
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = screenWidth;
                params.height = screenWidth*height/width;
                imageView.setLayoutParams(params);
                textureVideoView.setLayoutParams(params);
                imageView.setImageDrawable(resource);
            }
        });
    }

}

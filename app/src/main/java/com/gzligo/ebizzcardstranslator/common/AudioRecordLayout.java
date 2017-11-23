package com.gzligo.ebizzcardstranslator.common;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.utils.KeyBoardUtils;
import com.gzligo.ebizzcardstranslator.utils.MediaManager;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;
import com.zhy.autolayout.AutoLinearLayout;

import java.io.File;
import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.NEW_CHAT_PRIVATE_MSG;

/**
 * Created by Lwd on 2017/6/16.
 */

public class AudioRecordLayout extends AutoLinearLayout implements AudioManager.AudioStageListener {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_PLAY = 4;
    private static final int STATE_WANT_TO_STOP = 3;
    private static final int NORMAL_CHAT = 0x30;
    private static final int PRIVATE_CHAT = 0x31;
    private static final int RE_TRANS_CHAT = 0x45;
    private static final int RECORD_VOICE_TIME = 59;
    private int actionType = NORMAL_CHAT;
    private int mCurrentState = STATE_NORMAL;
    private int mTime = 0;
    private AudioManager mAudioManager;
    private AudioFinishRecorderListener mListener;
    private Unbinder unbinder;
    private int clickNumber = 0;
    private String recordVoiceFilePath;
    private int timeCount;
    private int voiceTime;
    private ChatMessageBean privateChatMessage;
    private String beforeValue="";

    @BindView(R.id.voice_img) ImageView voiceImg;
    @BindView(R.id.content_input_et) EditText contentInputEt;
    @BindView(R.id.send_rl) RelativeLayout sendRl;
    @BindView(R.id.chat_bottom_ll) LinearLayout chatBottomLl;
    @BindView(R.id.line_view) View lineView;
    @BindView(R.id.mark_voice_img) ImageView markVoiceImg;
    @BindView(R.id.record_voice_tv) TextView recordVoiceTv;
    @BindView(R.id.record_voice_time) Chronometer recordVoiceTime;
    @BindView(R.id.come_back_kb) ImageView comeBackKb;
    @BindView(R.id.record_voice_rl) RelativeLayout recordVoiceRl;
    @BindView(R.id.record_voice_img) ImageView recordVoiceImg;
    @BindView(R.id.cancel_voice_tv) TextView cancelVoiceTv;
    @BindView(R.id.send_voice_tv) TextView sendVoiceTv;
    @BindView(R.id.record_voice_ing) LinearLayout recordVoiceIng;
    @BindView(R.id.chat_voice_bottom) RelativeLayout chatVoiceBottom;
    @BindView(R.id.chat_bottom_input_rl) RelativeLayout chatBottomInputRl;
    @BindView(R.id.circle_view_one) CircleViewOne circleViewOne;

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public AudioRecordLayout(Context context) {
        super(context);
    }

    public AudioRecordLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
        initEven();
    }

    private void initViews(Context context) {
        View view = View.inflate(context, R.layout.chat_bottom_input, this);
        unbinder = ButterKnife.bind(view);
    }

    public void initAudioManager(){
        mAudioManager = AudioManager.getInstance();
        mAudioManager.setOnAudioStageListener(this);
        mAudioManager.setHandle(handler);
    }

    private void initEven() {
        changeState(STATE_NORMAL);
        recordVoiceTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (RECORD_VOICE_TIME < timeCount) {
                    recordVoiceTime.setText(TimeUtils.formatTime(timeCount));
                    mAudioManager.stopRecord();
                    return;
                }
                timeCount++;
                recordVoiceTime.setText(TimeUtils.formatTime(timeCount));
            }
        });
        contentInputEt.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        &&PRIVATE_CHAT==actionType) {
                    String beforeContent = contentInputEt.getText().toString().trim();
                    if(beforeValue.trim().equals(beforeContent)){
                        contentInputEt.setText("");
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
        contentInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(TextUtils.isEmpty(charSequence.toString().trim())){
                    sendRl.setBackgroundResource(R.drawable.chat_input_send_bg);
                }else{
                    sendRl.setBackgroundResource(R.drawable.chat_input_send_selector);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String afterContent = editable.toString();
                switch (actionType){
                    case PRIVATE_CHAT:
                        if (!TextUtils.isEmpty(afterContent)) {
                            if(!afterContent.startsWith(beforeValue)){
                                mListener.isShowKeyBoard();
                                resetViewStatus();
                                beforeValue="";
                                actionType = NORMAL_CHAT;
                            }
                        }else{
                            mListener.isShowKeyBoard();
                            resetViewStatus();
                            beforeValue="";
                            actionType = NORMAL_CHAT;
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @OnClick({R.id.mark_voice_img, R.id.record_voice_img, R.id.send_rl,
            R.id.send_voice_tv, R.id.cancel_voice_tv, R.id.come_back_kb})
    void clickView(View view) {
        switch (view.getId()) {
            case R.id.come_back_kb:
                stopPlay();
                KeyBoardUtils.showKeyBoard(AppManager.get().getApplication(), contentInputEt);
                chatBottomLl.setVisibility(View.VISIBLE);
                chatVoiceBottom.setVisibility(View.GONE);
                break;
            case R.id.cancel_voice_tv:
                stopPlay();
                chatBottomLl.setVisibility(View.VISIBLE);
                chatVoiceBottom.setVisibility(View.GONE);
                mAudioManager.cancelRecord();
                resetData();
                resetViewStatus();
                break;
            case R.id.send_voice_tv:
                if (voiceTime == 0) {
                    voiceTime = timeCount;
                }
                stopPlay();
                mListener.sendVoice(voiceTime, recordVoiceFilePath, privateChatMessage,actionType);
                resetData();
                resetViewStatus();
                contentInputEt.setText("");
                break;
            case R.id.send_rl:
                String msg = contentInputEt.getText().toString();
                if(TextUtils.isEmpty(msg)){
                    break;
                }
                clickNumber = 0;
                String result;
                switch (actionType) {
                    case PRIVATE_CHAT:
                        result = msg.replace(beforeValue, "").trim();
                        if (TextUtils.isEmpty(result)) {
                            break;
                        }
                        mListener.getSendTxt(result, privateChatMessage,actionType);
                        break;
                    case RE_TRANS_CHAT:
                        result = msg.trim();
                        if (TextUtils.isEmpty(result)) {
                            break;
                        }
                        mListener.getSendTxt(result, null,actionType);
                        break;
                    default:
                        result = msg.trim();
                        if (TextUtils.isEmpty(result)) {
                            break;
                        }
                        mListener.getSendTxt(result, null,actionType);
                        break;
                }
                resetViewStatus();
                timeCount = 0;
                privateChatMessage = null;
                beforeValue = "";
                contentInputEt.setText("");
                break;
            case R.id.record_voice_img:
                switch (clickNumber) {
                    case 0:
                        mAudioManager.prepareAudio();
                        break;
                    case 1:
                        mAudioManager.stopRecord();
                        break;
                    case 2:
                        changeState(STATE_RECORDING);
                        clickNumber = 0;
                        break;
                    case 3://播放
                        MediaManager.getInstance().playSound(recordVoiceFilePath, new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                changeState(STATE_WANT_TO_STOP);
                                MediaManager.getInstance().reset();
                                MediaManager.getInstance().setPosition(-1);
                                MediaManager.getInstance().setTransVoice(false);
                                clickNumber = 3;
                                recordVoiceTime.stop();
                                circleViewOne.stop();
                                circleViewOne.setVisibility(GONE);
                                timeCount = 0;
                            }
                        }, new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                if (voiceTime == 0) {
                                    voiceTime = timeCount;
                                }
                                timeCount = 0;
                                changeState(STATE_WANT_TO_PLAY);
                                circleViewOne.initSpeed(voiceTime);
                                circleViewOne.setVisibility(VISIBLE);
                                circleViewOne.start();
                                recordVoiceTime.start();
                                clickNumber = 4;
                            }
                        });
                        break;
                    case 4://停止播放
                        boolean isPlaying = MediaManager.getInstance().isPlaying();
                        if (isPlaying) {
                            recordVoiceTime.stop();
                            circleViewOne.stop();
                            circleViewOne.setVisibility(GONE);
                            timeCount = 0;
                            recordVoiceTime.setText(TimeUtils.formatTime(voiceTime));
                            MediaManager.getInstance().stop();
                            MediaManager.getInstance().setPosition(-1);
                            MediaManager.getInstance().setTransVoice(false);
                        }
                        changeState(STATE_WANT_TO_STOP);
                        clickNumber = 3;
                        break;
                }
                break;
        }
    }

    private void startView() {
        recordVoiceTv.setVisibility(View.GONE);
        recordVoiceTime.setVisibility(View.VISIBLE);
        markVoiceImg.setVisibility(View.INVISIBLE);
        comeBackKb.setVisibility(View.INVISIBLE);
        changeState(STATE_RECORDING);
        clickNumber = 1;
        recordVoiceTime.start();
        circleViewOne.setVisibility(VISIBLE);
        circleViewOne.start();
    }

    private void resetViewStatus() {
        recordVoiceImg.setBackgroundResource(R.mipmap.chat_prepare_record_voice);
        recordVoiceIng.setVisibility(GONE);
        recordVoiceTime.setText("00:00");
        recordVoiceTime.setVisibility(GONE);
        recordVoiceTv.setVisibility(View.VISIBLE);
        markVoiceImg.setVisibility(View.VISIBLE);
        comeBackKb.setVisibility(View.VISIBLE);
        chatBottomLl.setVisibility(View.VISIBLE);
        chatVoiceBottom.setVisibility(View.GONE);
    }

    @Override
    public void wellPrepared() {
        startView();
    }

    @Override
    public void onStop(String filePath) {
        recordVoiceTime.stop();
        circleViewOne.stop();
        circleViewOne.setVisibility(GONE);
        if (mListener != null) {//回调，保存录音
            BigDecimal b = new BigDecimal(mTime);
            int f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
            File file = new File(filePath);
            if (file != null) {
                recordVoiceFilePath = filePath;
                mTime = f1;
            } else {
                handler.sendEmptyMessage(AudioManager.MSG_ERROR_AUDIO_RECORD);
            }
        } else if (mCurrentState == STATE_WANT_TO_STOP) {
            mAudioManager.cancelRecord();
        }
        reset();
        changeState(STATE_WANT_TO_STOP);
        clickNumber = 3;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case AudioManager.MSG_ERROR_AUDIO_RECORD:
                    Toast.makeText(getContext(), getResources().getString(R.string.record_voice_error), Toast.LENGTH_SHORT).show();
                    mAudioManager.cancelRecord();
                    reset();
                    break;
                default:
                    break;
            }
        }
    };

    private void reset() {
        // TODO Auto-generated method stub
        changeState(STATE_NORMAL);
        mTime = 0;
        clickNumber = 0;
    }

    private void changeState(int state) {
        // TODO Auto-generated method stub
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (mCurrentState) {
                case STATE_NORMAL://还没开始
                    recordVoiceImg.setBackgroundResource(R.mipmap.chat_prepare_record_voice);
                    break;
                case STATE_RECORDING://录制中
                    recordVoiceImg.setBackgroundResource(R.mipmap.chat_recording_voice);
                    break;
                case STATE_WANT_TO_STOP://停止
                    recordVoiceImg.setBackgroundResource(R.mipmap.chat_send_voice);
                    recordVoiceIng.setVisibility(VISIBLE);
                    break;
                case STATE_WANT_TO_PLAY://播放
                    recordVoiceImg.setBackgroundResource(R.mipmap.chat_recording_voice);
                    break;
            }
        }
    }

    public void releaseButterKnife() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface AudioFinishRecorderListener {
        void requestPermissionListener();

        void getSendTxt(String msg, ChatMessageBean data,int actionType);

        void sendVoice(int seconds, String filePath, ChatMessageBean chatMsg,int actionType);

        void isShowKeyBoard();
    }

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        mListener = listener;
    }

    public void hideKeyBoard() {
        if (null != voiceImg && null != contentInputEt) {
            sendRl.setFocusable(false);
            sendRl.setClickable(false);
            voiceImg.setOnClickListener(null);
            voiceImg.setBackgroundResource(R.mipmap.chat_voice_normal);
            contentInputEt.setInputType(InputType.TYPE_NULL);
            sendRl.setBackgroundResource(R.drawable.chat_input_send_bg);
            KeyBoardUtils.hideKeyBoard(AppManager.get().getApplication(), contentInputEt);
        }
    }

    public void showKeyBoard() {
        setOnclickImageView();
        if (null != contentInputEt) {
            voiceImg.setBackgroundResource(R.mipmap.chat_voice_pressed);
            contentInputEt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            contentInputEt.setHorizontallyScrolling(false);
            contentInputEt.setVerticalScrollBarEnabled(true);
            contentInputEt.setMaxLines(4);
        }
    }

    public void hideKB() {
        KeyBoardUtils.hideKeyBoard(AppManager.get().getApplication(), contentInputEt);
    }

    public void getContentInputEt(ChatMessageBean data) {
        this.actionType = NEW_CHAT_PRIVATE_MSG;
        privateChatMessage = data;
        beforeValue = "@" + data.getFromName() + AppManager.get().getApplication().getResources().getString(R.string.space);
        contentInputEt.setText(beforeValue);
        contentInputEt.setSelection(beforeValue.length());
        KeyBoardUtils.showKeyBoard(AppManager.get().getApplication(), contentInputEt);
    }

    private void setOnclickImageView() {
        if (null != voiceImg) {
            voiceImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.requestPermissionListener();
                }
            });
        }
        if(null!=sendRl){
            sendRl.setFocusable(true);
            sendRl.setClickable(true);
        }
    }

    public void allowablePermission() {
        KeyBoardUtils.hideKeyBoard(AppManager.get().getApplication(), contentInputEt);
        chatBottomLl.setVisibility(View.GONE);
        chatVoiceBottom.setVisibility(View.VISIBLE);
    }

    public void cancelRecordVoice() {
        stopPlay();
        recordVoiceTime.stop();
        circleViewOne.stop();
        circleViewOne.setVisibility(GONE);
        chatBottomLl.setVisibility(View.VISIBLE);
        chatVoiceBottom.setVisibility(View.GONE);
        mAudioManager.cancelRecord();
        resetViewStatus();
        mCurrentState = STATE_NORMAL;
        hideKB();
        resetData();
    }

    public void setReTranslateMsgContent(ChatMessageBean chatMsg){
        if(null!=chatMsg){
            if(chatMsg.getSendMsgType()==0){
                String content = chatMsg.getTranslateContent();
                if(!TextUtils.isEmpty(content)){
                    contentInputEt.setText(content);
                    contentInputEt.setSelection(content.length());
                }
            }
        }
        KeyBoardUtils.showKeyBoard(AppManager.get().getApplication(), contentInputEt);
    }

    private void stopPlay(){
        boolean isPlaying = MediaManager.getInstance().isPlaying();
        if (isPlaying) {
            recordVoiceTime.stop();
            circleViewOne.stop();
            circleViewOne.setVisibility(GONE);
            MediaManager.getInstance().stop();
            MediaManager.getInstance().setPosition(-1);
            MediaManager.getInstance().setTransVoice(false);
        }
    }

    public boolean isVisibility(){
        int visibility = chatVoiceBottom.getVisibility();
        if(visibility==View.VISIBLE){
            return true;
        }else{
            return false;
        }
    }

    private void resetData(){
        timeCount = 0;
        voiceTime = 0;
        clickNumber = 0;
    }
}

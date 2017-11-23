package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.me.PersonalBaseMessageActivity;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.persistence.LoginBean;
import com.gzligo.ebizzcardstranslator.persistence.RegisterBean;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/26.
 */

public class RegisterSuccessActivity extends BaseActivity<LoginPresenter> implements IView{

    @BindView(R.id.register_success_avatar_iv)
    ImageView mAvatarIv;
    @BindView(R.id.register_success_nickname)
    TextView mNicknameTv;
    @BindView(R.id.register_success_login)
    Button mLoginBtn;
    @BindView(R.id.register_success_continue)
    Button mContinueBtn;
    private RegisterBean registerBean;
    private String mPhoneWithCcode;
    private String mPassword;
    private String mNickname;
    private String mAvatarPath;
    private String mAccessToken;
    public static final int REGISTER_SUCCESS_LOGIN = 0x01;

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter(new LoginRepository(),this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_register_success;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initData() {
        String json = getIntent().getStringExtra(TranslatorConstants.Common.JSON);
        Gson gson = new Gson();
        registerBean = gson.fromJson(json, RegisterBean.class);
        mPhoneWithCcode = registerBean.getPhone();
        mPassword = registerBean.getPassword();
        mNickname = registerBean.getNickname();
        mNicknameTv.setText(mNickname);
        if (registerBean.getPortrait()!=null) {
            mAvatarPath = registerBean.getPortrait();
            ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                    .builder()
                    .url(mAvatarPath)
                    .imageView(mAvatarIv)
                    .errorPic(R.mipmap.default_head_portrait)
                    .isClearMemory(false)
                    .transformation(new CustomShapeTransformation(AppManager.get().getApplication(), 50))
                    .build());
        }
    }

    @Override
    public void initEvents() {

    }

    @OnClick({R.id.register_success_login,R.id.register_success_continue})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.register_success_login:
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;
            case R.id.register_success_continue:
                if (mAccessToken!=null){
                    Intent intent = new Intent(this, PersonalBaseMessageActivity.class);
                    intent.putExtra(TranslatorConstants.Common.DATA,mAccessToken);
                    startActivity(intent);
                }else {
                    getPresenter().login(Message.obtain(this, new String[]{mPhoneWithCcode, mPassword}),true,registerBean.getCc_code());
                }
                break;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case REGISTER_SUCCESS_LOGIN:
                if (((LoginBean)message.obj).getAccess_token()!=null){
                    Intent intent = new Intent(this, PersonalBaseMessageActivity.class);
                    intent.putExtra(TranslatorConstants.Common.DATA,((LoginBean)message.obj).getAccess_token());
                    startActivity(intent);
                }
                break;
        }
    }
}

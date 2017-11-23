package com.gzligo.ebizzcardstranslator.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;

import java.util.TreeMap;

/**
 * Created by ZuoJian on 2017/5/26.
 */

public class DialogUtils {

    private static final double DIALOG_WIDTH = 0.75;
    private static final float DIALOG_ALPHA = 0.95f;

    private static Dialog createDialog(Activity activity, int layoutId) {
        return createDialog(activity, layoutId, true, DIALOG_WIDTH, DIALOG_ALPHA);
    }

    private static Dialog createDialog(Activity activity, int layoutId,double width,float alpha) {
        return createDialog(activity, layoutId, true, width, alpha);
    }

    private static Dialog createDialog(Activity activity, int layoutId, boolean reSetWidth,double width,float alpha) {
        Dialog dialog = new Dialog(activity, R.style.dialog);
        View view = LayoutInflater.from(activity).inflate(layoutId, null);
        dialog.setContentView(view);
        if (reSetWidth) {
            try {
                Window dialogWindow = dialog.getWindow();
                WindowManager m = activity.getWindowManager();
                Display d = m.getDefaultDisplay();
                WindowManager.LayoutParams p = dialogWindow.getAttributes();
                p.width = (int) (d.getWidth() * width);
                p.alpha = alpha;
                p.dimAmount = 0.5f;
                dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialogWindow.setAttributes(p);
            } catch (Exception e) {
                Log.w("lw", "createDialog set width failed." + e.toString());
            }
        }
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    public static Dialog showTitleContentDialog(Activity activity, String title, String content, String cancel, String confirm, final TranslatorCallBack.OnDialogClickListener listener) {
        final Dialog dialog = createDialog(activity, R.layout.dialog_send_message);
        TextView tvTile = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tvContent = (TextView) dialog.findViewById(R.id.tv_content);
        tvTile.setText(title);
        tvContent.setText(content);

        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        tvCancel.setText(cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView tvConfirm = (TextView) dialog.findViewById(R.id.tv_confirm);
        tvConfirm.setText(confirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });
        return dialog;
    }

    public static Dialog showLoadingDialog(Activity activity, String content) {
        Dialog dialog = createDialog(activity, R.layout.dialog_loading);
        TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_loading_txt);
        if(!TextUtils.isEmpty(content)){
            dialogTitle.setVisibility(View.VISIBLE);
            dialogTitle.setText(content);
        }else{
            dialogTitle.setVisibility(View.GONE);
        }
        return dialog;
    }

    public static Dialog showEditDialog(Activity activity, String title ,String hint,String name, final TranslatorCallBack.OnEditNameClickListener listener) {
        final Dialog dialog = createDialog(activity, R.layout.dialog_edit_name);
        TextView titleName = (TextView) dialog.findViewById(R.id.dialog_send_msg_title_txt);
        TextView cancel = (TextView) dialog.findViewById(R.id.tv_edit_name_cancel);
        TextView confirm = (TextView) dialog.findViewById(R.id.tv_edit_name_confirm);
        final EditText edtName = (EditText) dialog.findViewById(R.id.edt_edit_name);
        edtName.setHint(hint);
        edtName.setText(name);
        titleName.setText(title);
        edtName.setSelection(name.length());
        edtName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfirm(edtName.getText().toString().trim());
                }
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static Dialog myWalletMoreDialog(Activity activity, final TranslatorCallBack.onMyWalletMoreClickListener listener){
        final Dialog dialog = createDialog(activity, R.layout.my_wallet_more_dialog);
        RelativeLayout addBankCardRl = (RelativeLayout) dialog.findViewById(R.id.my_wallet_more_add_bank_card);
        RelativeLayout recordRl = (RelativeLayout) dialog.findViewById(R.id.my_wallet_more_record);
        RelativeLayout passwordRl = (RelativeLayout) dialog.findViewById(R.id.my_wallet_more_password);
        addBankCardRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener !=null){
                    listener.onAddBankCard();
                }
                dialog.dismiss();
            }
        });
        recordRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener !=null){
                    listener.onRecord();
                }
                dialog.dismiss();
            }
        });
        passwordRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener !=null){
                    listener.onPassword();
                }
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static Dialog selectPictureDialog(Activity activity,final TranslatorCallBack.onSelectPictureClickLister listener){
        final Dialog dialog = new Dialog(activity,R.style.SelectPhotoDialog);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.select_picture_dialog, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        Window w = dialog.getWindow();
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.width = (int) d.getWidth();
        lp.gravity = Gravity.BOTTOM;
        dialog.onWindowAttributesChanged(lp);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(layout);
        dialog.show();
        RelativeLayout takePhotoRl = (RelativeLayout) dialog.findViewById(R.id.select_picture_take_photo);
        RelativeLayout fromAlbumRl = (RelativeLayout) dialog.findViewById(R.id.select_picture_from_album);
        RelativeLayout cancelRl = (RelativeLayout) dialog.findViewById(R.id.select_picture_cancel);
        takePhotoRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener !=null){
                    listener.onTakePhoto();
                }
                dialog.dismiss();
            }
        });
        fromAlbumRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener !=null){
                    listener.onFromAlbum();
                }
                dialog.dismiss();
            }
        });
        cancelRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static Dialog bottomContentConfirmDialog(Activity activity,String content,String confirm,final TranslatorCallBack.onConfirmDialogListener listener){
        final Dialog dialog = new Dialog(activity,R.style.BottomConfirmDialog);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.bot_content_confirm_dialog, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        Window w = dialog.getWindow();
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.width = (int) d.getWidth();
        lp.gravity = Gravity.BOTTOM;
        dialog.onWindowAttributesChanged(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.onCancel();
                dialog.dismiss();
            }
        });

        dialog.setContentView(layout);
        dialog.show();
        TextView contentTv = (TextView) dialog.findViewById(R.id.bot_dialog_content);
        TextView confirmTv = (TextView) dialog.findViewById(R.id.bot_dialog_confirm_txt);
        RelativeLayout confirmRl = (RelativeLayout) dialog.findViewById(R.id.bot_dialog_confirm_rl);
        RelativeLayout cancelRl = (RelativeLayout) dialog.findViewById(R.id.bot_dialog_caccel_rl);
        contentTv.setText(content);
        confirmTv.setText(confirm);
        confirmRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onConfirm();
                dialog.dismiss();
            }
        });
        cancelRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel();
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static Dialog inputTakeCashPwdDialog(Activity activity, String actionbar, String title, String content,
                                                final TranslatorCallBack.onInputPwdCompleteListener listener){
        final Dialog dialog = createDialog(activity,R.layout.input_take_cash_pwd_dialog,0.95,1f);
        ImageView cancelIv = (ImageView) dialog.findViewById(R.id.input_pwd_dialog_close_iv);
        TextView actionbarTv = (TextView) dialog.findViewById(R.id.input_pwd_dialog_actionbar_tv);
        TextView titleTv = (TextView) dialog.findViewById(R.id.input_pwd_dialog_title_tv);
        TextView contentTv = (TextView) dialog.findViewById(R.id.input_pwd_dialog_content);
        final PayPwdEditView pwdEditView = (PayPwdEditView) dialog.findViewById(R.id.input_pwd_dialog_pwd_ppedv);
        actionbarTv.setText(actionbar);
        titleTv.setText(title);
        contentTv.setText(content);
        pwdEditView.setFocusable(true);
        pwdEditView.setFocusableInTouchMode(true);
        pwdEditView.requestFocus();
        cancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        pwdEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)){
                    if (!TextUtils.isEmpty(pwdEditView.getText())&&
                            (pwdEditView.getText().toString().length() == 6)){
                        if (listener!=null){
                            listener.onComplete(pwdEditView.getText().toString());
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return dialog;
    }

    public static Dialog showConFirmDialog(Activity activity, String title, String content, String confirm,int layout,
                                           final TranslatorCallBack.OnDialogClickListener listener) {
        final Dialog dialog = createDialog(activity, layout);
        TextView tvTile = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tvContent = (TextView) dialog.findViewById(R.id.tv_content);
        tvTile.setText(title);
        tvContent.setText(content);
        dialog.setCancelable(false);
        TextView tvConfirm = (TextView) dialog.findViewById(R.id.tv_confirm);
        tvConfirm.setText(confirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });
        return dialog;
    }

    public static Dialog showExpiredOrderDialog(Activity activity,NewTransOrderBean newTransOrderBean,int layout,
                                                final TranslatorCallBack.OnDialogClickListener listener) {
        final Dialog dialog = createDialog(activity, layout);
        dialog.setCancelable(false);
        ImageView fromPeopleImg = (ImageView) dialog.findViewById(R.id.from_people_img);
        ImageView toPeopleImg = (ImageView) dialog.findViewById(R.id.to_people_img);
        TextView fromLanguageTv = (TextView) dialog.findViewById(R.id.from_language_tv);
        TextView toLanguageTv = (TextView) dialog.findViewById(R.id.to_language_tv);
        TextView remarksTv = (TextView) dialog.findViewById(R.id.remarks_tv);
        TextView tvConfirm = (TextView) dialog.findViewById(R.id.confirm_tv);
        RelativeLayout remarksRl = (RelativeLayout) dialog.findViewById(R.id.remarks_rl);
        tvConfirm.setText(activity.getResources().getString(R.string.confirm));
        initImageView(newTransOrderBean.getFromPortraitId(),fromPeopleImg);
        initImageView(newTransOrderBean.getToPortraitId(),toPeopleImg);
        String descStr = newTransOrderBean.getDesc();
        if(null==descStr||descStr.length()==0){
            remarksRl.setVisibility(View.GONE);
        }else{
            remarksRl.setVisibility(View.VISIBLE);
            remarksTv.setText(descStr);
        }
        TreeMap<Integer, LanguagesBean> treeMap = CommonBeanManager.getInstance().getTreeMap();
        if (LanguageUtils.getLanguage(activity).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
            fromLanguageTv.setText(treeMap.get(newTransOrderBean.getFromLangId()).getZh_name());
            toLanguageTv.setText(treeMap.get(newTransOrderBean.getToLangId()).getZh_name());
        }else if (LanguageUtils.getLanguage(activity).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)) {
            fromLanguageTv.setText(treeMap.get(newTransOrderBean.getFromLangId()).getEn_name());
            toLanguageTv.setText(treeMap.get(newTransOrderBean.getToLangId()).getEn_name());
        }
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });
        return dialog;
    }

    private static void initImageView(String url,ImageView img){
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(HttpUtils.MEDIA_HOST+url)
                .imageView(img)
                .errorPic(R.mipmap.default_head_portrait)
                .isClearMemory(false)
                .transformation(new CustomShapeTransformation(AppManager.get().getApplication(),39))
                .build());
    }

    public static Dialog showFailedOrderDialog(Activity activity,int layout) {
        final Dialog dialog = createDialog(activity, layout);
        dialog.setCancelable(false);
        TextView tvConfirm = (TextView) dialog.findViewById(R.id.know_btn);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
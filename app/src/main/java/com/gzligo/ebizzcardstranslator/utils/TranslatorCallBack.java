package com.gzligo.ebizzcardstranslator.utils;

/**
 * Created by ZuoJian on 2017/5/26.
 */

public class TranslatorCallBack {
    public interface OnDialogClickListener {
        void onConfirm();
    }

    public interface OnEditNameClickListener {
        void onConfirm(String name);
    }

    public interface onMyWalletMoreClickListener{
        void onAddBankCard();

        void onRecord();

        void onPassword();
    }

    public interface onSelectPictureClickLister{
        void onTakePhoto();

        void onFromAlbum();
    }

    public interface onInputPwdCompleteListener{
        void onComplete(String password);
    }

    public interface onConfirmDialogListener{
        void onConfirm();

        void onCancel();
    }
}

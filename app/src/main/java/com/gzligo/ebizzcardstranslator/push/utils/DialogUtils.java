package com.gzligo.ebizzcardstranslator.push.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;


/**
 * Created by YJZ on 2016/7/8 0008.
 */
public class DialogUtils {

    public static Dialog showTextMsgDialog(Context context, int cancel, int confirm, final DialogCallback.OnDialogClickListener listener, String... contents) {
        if (contents.length <= 0) {
            throw new RuntimeException("contents is :" + contents.length);
        }
        final Dialog dialog = createDialog(context, R.layout.dialog_send_msg);
        TextView tvContent = (TextView) dialog.findViewById(R.id.tv_content);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < contents.length; i++) {
            if (i == contents.length - 1) {
                sb.append(contents[i]);
            } else {
                sb.append(contents[i]).append("\n");
            }
        }
        tvContent.setText(sb);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        View line = dialog.findViewById(R.id.line);
        if (cancel < 0) {
            tvCancel.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        } else {
            tvCancel.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            tvCancel.setText(cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
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

    private static Dialog createDialog(Context context, int layoutId) {
        Dialog dialog = new Dialog(context, R.style.dialog);
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        dialog.setContentView(view);
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    public static class DialogCallback {
        public interface OnDialogClickListener {
            void onConfirm();
        }
    }
}


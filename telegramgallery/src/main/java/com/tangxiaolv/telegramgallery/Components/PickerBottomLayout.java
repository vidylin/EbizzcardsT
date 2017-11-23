
package com.tangxiaolv.telegramgallery.Components;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.tangxiaolv.telegramgallery.PhotoAlbumPickerActivity;
import com.tangxiaolv.telegramgallery.R;
import com.tangxiaolv.telegramgallery.Theme;
import com.tangxiaolv.telegramgallery.Utils.AndroidUtilities;
import com.tangxiaolv.telegramgallery.Utils.LayoutHelper;

public class PickerBottomLayout extends FrameLayout {

    public LinearLayout doneButton;
    public TextView cancelButton;
    public TextView doneButtonTextView;
    public TextView doneButtonBadgeTextView;
    public com.tangxiaolv.telegramgallery.Components.CheckBox originalCheckBox;
    public TextView originalTextView;

    private boolean isDarkTheme;

    public PickerBottomLayout(Context context) {
        this(context, true);
    }

    public PickerBottomLayout(Context context, boolean darkTheme) {
        super(context);
        isDarkTheme = darkTheme;

        setBackgroundColor(isDarkTheme ? 0xff1a1a1a : 0xffffffff);


        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundDrawable(
                Theme.createBarSelectorDrawable(isDarkTheme ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR
                        : Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, false));
        linearLayout.setPadding(AndroidUtilities.dp(29), 0, 0, 0);
        addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT,
                LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        originalCheckBox = new com.tangxiaolv.telegramgallery.Components.CheckBox(context);
        originalCheckBox.setSize(20);
        originalCheckBox.setCheckOffset(AndroidUtilities.dp(1));
        originalCheckBox.setDrawBackground(true);
        originalCheckBox.setColor(0xff007aff);
        originalCheckBox.setChecked(false, false);
        linearLayout.addView(originalCheckBox, LayoutHelper.createLinear(20, 20, Gravity.CENTER_VERTICAL, 0, 0, 10, 0));
        originalCheckBox.setVisibility(GONE);

        originalTextView = new TextView(context);
        originalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        originalTextView.setTextColor(isDarkTheme ? 0xffffffff : 0xff007aff);
        originalTextView.setGravity(Gravity.CENTER);
        originalTextView.setBackgroundDrawable(
                Theme.createBarSelectorDrawable(isDarkTheme ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR
                        : Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, false));
        originalTextView.setText(R.string.Original);
        originalTextView.setVisibility(GONE);
        linearLayout.addView(originalTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 20, Gravity.CENTER_VERTICAL, 0, 0, 10, 0));

        cancelButton = new TextView(context);
        cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        cancelButton.setTextColor(isDarkTheme ? 0xffffffff : 0xff007aff);
        cancelButton.setGravity(Gravity.CENTER);
        cancelButton.setBackgroundDrawable(
                Theme.createBarSelectorDrawable(isDarkTheme ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR
                        : Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, false));
        cancelButton.setText(R.string.Preview);
        linearLayout.addView(cancelButton, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,
                LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
//        cancelButton = new TextView(context);
//        cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//        cancelButton.setTextColor(isDarkTheme ? 0xffffffff : 0xff007aff);
//        cancelButton.setGravity(Gravity.CENTER);
//        cancelButton.setBackgroundDrawable(
//                Theme.createBarSelectorDrawable(isDarkTheme ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR
//                        : Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, false));
//        cancelButton.setPadding(AndroidUtilities.dp(29), 0, AndroidUtilities.dp(29), 0);
//        cancelButton.setText(R.string.Preview);
//        // cancelButton.getPaint().setFakeBoldText(true);
//        addView(cancelButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT,
//                LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        doneButton = new LinearLayout(context);
        doneButton.setOrientation(LinearLayout.HORIZONTAL);
        doneButton.setBackgroundDrawable(
                Theme.createBarSelectorDrawable(isDarkTheme ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR
                        : Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, false));
        doneButton.setPadding(AndroidUtilities.dp(29), 0, AndroidUtilities.dp(29), 0);
        addView(doneButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT,
                LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.RIGHT));

        doneButtonBadgeTextView = new TextView(context);
        // doneButtonBadgeTextView.getPaint().setFakeBoldText(true);
        doneButtonBadgeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        doneButtonBadgeTextView.setTextColor(0xffffffff);
        doneButtonBadgeTextView.setGravity(Gravity.CENTER);
        doneButtonBadgeTextView.setBackgroundResource(
                isDarkTheme ? R.drawable.photobadge_new : R.drawable.photobadge_new);
        doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8),
                AndroidUtilities.dp(1));
        doneButton.addView(doneButtonBadgeTextView,
                LayoutHelper.createLinear(26, 26, Gravity.CENTER_VERTICAL, 0, 0, 10, 0));

        doneButtonTextView = new TextView(context);
        doneButtonTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        doneButtonTextView.setTextColor(isDarkTheme ? 0xffffffff : 0xff007aff);
        doneButtonTextView.setGravity(Gravity.CENTER);
        doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8));
        doneButtonTextView.setText(R.string.Send);
        // doneButtonTextView.getPaint().setFakeBoldText(true);
        doneButton.addView(doneButtonTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,
                LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
    }

    public void updateSelectedCount(int count, boolean disable) {
        if (count == 0) {
            doneButtonBadgeTextView.setVisibility(View.GONE);

            if (disable) {
                doneButtonTextView.setTextColor(0xff999999);
                cancelButton.setTextColor(0xff999999);
                doneButton.setEnabled(false);
                cancelButton.setEnabled(false);
            } else {
                // doneButtonTextView.setTextColor(isDarkTheme ? 0xffffffff : 0xff19a7e8);
                doneButtonTextView.setTextColor(
                        PhotoAlbumPickerActivity.limitPickPhoto == 1 ? 0xffffffff : 0xff999999);
            }
        } else {
            doneButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            doneButtonBadgeTextView.setVisibility(View.VISIBLE);
            doneButtonBadgeTextView.setText(String.format("%d", count));

            doneButtonTextView.setTextColor(isDarkTheme ? 0xffffffff : 0xff007aff);
            cancelButton.setTextColor(isDarkTheme ? 0xffffffff : 0xff007aff);
            if (disable) {
                doneButton.setEnabled(true);
                cancelButton.setEnabled(true);
            }
        }
    }
}

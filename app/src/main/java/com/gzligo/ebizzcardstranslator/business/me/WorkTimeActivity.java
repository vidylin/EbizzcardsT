package com.gzligo.ebizzcardstranslator.business.me;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.WorkTimeBean;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/2.
 */

public class WorkTimeActivity extends BaseActivity<PersonalPresenter> implements IView{

    @BindView(R.id.work_time_on_am_txt)
    TextView workOnAmTxt;
    @BindView(R.id.work_time_off_am_txt)
    TextView workOffAmTxt;
    @BindView(R.id.work_time_on_pm_txt)
    TextView workOnPmTxt;
    @BindView(R.id.work_time_off_pm_txt)
    TextView workOffPmTxt;

    public static final int UPLOAD_WORK_TIME = 0x00;
    private String mAccessToken;
    private Dialog mLoadingDialog;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_work_time;
    }

    @Override
    public void initData() {
        SharedPreferences sharedPreferences = AppManager.get().getApplication().getSharedPreferences("Access_token", Context.MODE_PRIVATE);
        mAccessToken = sharedPreferences.getString("accessToken","");
    }

    @Override
    public void initViews() {
        String work = getIntent().getStringExtra(TranslatorConstants.Common.DATA);
        if (!TextUtils.isEmpty(work)&&!work.equals("-")) {
            String workTimeAmOn = work.split("-")[0];
            String workTimeAmOff = work.split("-")[1];
            String workTimePmOn = work.split("-")[2];
            String workTimePmOff = work.split("-")[3];
            workOnAmTxt.setText(workTimeAmOn);
            workOffAmTxt.setText(workTimeAmOff);
            workOnPmTxt.setText(workTimePmOn);
            workOffPmTxt.setText(workTimePmOff);
        }
    }

    @Override
    public void initEvents() {

    }

    @OnClick({R.id.tv_close,R.id.work_time_on_am_rl,R.id.work_time_off_am_rl,R.id.work_time_on_pm_rl,R.id.work_time_off_pm_rl,R.id.tv_right1})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.work_time_on_am_rl:
                handleTime(workOnAmTxt);
                break;
            case R.id.work_time_off_am_rl:
                handleTime(workOffAmTxt);
                break;
            case R.id.work_time_on_pm_rl:
                handleTime(workOnPmTxt);
                break;
            case R.id.work_time_off_pm_rl:
                handleTime(workOffPmTxt);
                break;
            case R.id.tv_right1:
                uploadWorkTime();
                break;
        }
    }

    private void handleTime(final TextView mWorkText) {
        String defaultTime = mWorkText.getText().toString();
        Date date;
        Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(defaultTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            try {
                date = sdf.parse(defaultTime);
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        TimePickerView pvBirthday = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mWorkText.setText(TimeUtils.getHM(date.getTime()));
            }
        }).setCancelText(getResources().getString(R.string.cancel))
                .setSubmitText(getResources().getString(R.string.confirm))
                .setLabel("","","","","","")
                .setDate(calendar)
                .setType(new boolean[]{false,false,false,true,true,false})//设置为年月日显示
                .build();
        pvBirthday.show();
    }

    private void uploadWorkTime() {
        List<WorkTimeBean> workTimeList = new ArrayList<>();
        Gson gson = new Gson();
        if (!TextUtils.isEmpty(workOnAmTxt.getText().toString())&&!TextUtils.isEmpty(workOffAmTxt.getText().toString())&&
                !TextUtils.isEmpty(workOnPmTxt.getText().toString())&&!TextUtils.isEmpty(workOffPmTxt.getText().toString())){
            mLoadingDialog = DialogUtils.showLoadingDialog(this,getResources().getString(R.string.submitting));
            WorkTimeBean workTimeAm = new WorkTimeBean();
            WorkTimeBean workTimePm = new WorkTimeBean();
            workTimeAm.setOn(workOnAmTxt.getText().toString());
            workTimeAm.setOff(workOffAmTxt.getText().toString());
            workTimePm.setOn(workOnPmTxt.getText().toString());
            workTimePm.setOff(workOffPmTxt.getText().toString());
            workTimeList.add(workTimeAm);
            workTimeList.add(workTimePm);
            String jsonWorkTime = gson.toJson(workTimeList);
            getPresenter().uploadWorkTime(Message.obtain(WorkTimeActivity.this,new String[]{jsonWorkTime}),true);
        }else {
            Toast.makeText(this,getResources().getString(R.string.work_time_toast_complete_choose),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        if (mLoadingDialog!=null){
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        switch (message.what){
            case UPLOAD_WORK_TIME:
                if (((ErrorMessageBean)message.obj).getError()==0) {
                    Toast.makeText(this, getResources().getString(R.string.submitting_success), Toast.LENGTH_SHORT).show();
                }
                setResultToMeFragment();
                break;
        }
    }

    private void setResultToMeFragment() {
        String time = workOnAmTxt.getText().toString()+"-"+workOffAmTxt.getText()+"-"+workOnPmTxt.getText().toString()+"-"+workOffPmTxt.getText().toString();
        Intent intent = getIntent();
        intent.putExtra(TranslatorConstants.Common.DATA,time);
        setResult(RESULT_OK,intent);
        finish();
    }
}

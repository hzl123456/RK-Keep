package rkkeep.keep.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rey.material.app.TimePickerDialog;
import com.rey.material.widget.Switch;

import cn.xmrk.rkandroid.activity.BackableBaseActivity;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.SharedPreferencesUtil;
import rkkeep.keep.R;
import rkkeep.keep.util.SettingContact;

/**
 * Created by Au61 on 2016/4/26.
 */
public class SettingActivity extends BackableBaseActivity implements View.OnClickListener {

    //设置页面所保存的信息
    private SharedPreferencesUtil mSharedPreferencesUtil;

    private RelativeLayout layoutAddBottom;
    private Switch switchAddBottom;
    private RelativeLayout layoutChooseItemBottom;
    private Switch switchChooseItemBottom;
    private RelativeLayout layoutShangwu;
    private TextView tvTimeShangwu;
    private RelativeLayout layoutXiawu;
    private TextView tvTimeXiawu;
    private RelativeLayout layoutWanshang;
    private TextView tvTimeWanshang;
    private RelativeLayout layoutShare;
    private Switch switchOpenShare;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitleInfo();
        findViews();
        init();
    }

    private void init() {
        mSharedPreferencesUtil = new SharedPreferencesUtil(this, "SETTING");
        tvTimeShangwu.setText(mSharedPreferencesUtil.getString(SettingContact.CHOOSE_TIME_FOR_SHANGWU, "08:00"));
        tvTimeXiawu.setText(mSharedPreferencesUtil.getString(SettingContact.CHOOSE_TIME_FOR_XIAWU, "13:00"));
        tvTimeWanshang.setText(mSharedPreferencesUtil.getString(SettingContact.CHOOSE_TIME_FOR_WANGSHANG, "18:00"));
        switchAddBottom.setChecked(mSharedPreferencesUtil.getBoolean(SettingContact.ADD_TO_BOTTOM, true));
        switchChooseItemBottom.setChecked(mSharedPreferencesUtil.getBoolean(SettingContact.ITEM_MOVE_TO_BOTTOM, true));
        switchOpenShare.setChecked(mSharedPreferencesUtil.getBoolean(SettingContact.INFO_TO_SHARE, true));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSharedPreferencesUtil.putBoolean(SettingContact.ADD_TO_BOTTOM, switchAddBottom.isChecked());
        mSharedPreferencesUtil.putBoolean(SettingContact.ITEM_MOVE_TO_BOTTOM, switchChooseItemBottom.isChecked());
        mSharedPreferencesUtil.putBoolean(SettingContact.INFO_TO_SHARE, switchOpenShare.isChecked());

    }

    private void setTitleInfo() {
        getTitlebar().setTitleTextAppearance(this, R.style.TitleTextStyle);
        getTitlebar().setBackgroundResource(R.color.title_yellow);
        getSupportActionBar().setTitle(R.string.nav_setting);
    }

    private void findViews() {
        layoutAddBottom = (RelativeLayout) findViewById(R.id.layout_add_bottom);
        switchAddBottom = (Switch) findViewById(R.id.switch_add_bottom);
        layoutChooseItemBottom = (RelativeLayout) findViewById(R.id.layout_choose_item_bottom);
        switchChooseItemBottom = (Switch) findViewById(R.id.switch_choose_item_bottom);
        layoutShangwu = (RelativeLayout) findViewById(R.id.layout_shangwu);
        tvTimeShangwu = (TextView) findViewById(R.id.tv_time_shangwu);
        layoutXiawu = (RelativeLayout) findViewById(R.id.layout_xiawu);
        tvTimeXiawu = (TextView) findViewById(R.id.tv_time_xiawu);
        layoutWanshang = (RelativeLayout) findViewById(R.id.layout_wanshang);
        tvTimeWanshang = (TextView) findViewById(R.id.tv_time_wanshang);
        layoutShare = (RelativeLayout) findViewById(R.id.layout_share);
        switchOpenShare = (Switch) findViewById(R.id.switch_open_share);

        layoutAddBottom.setOnClickListener(this);
        layoutChooseItemBottom.setOnClickListener(this);
        layoutShangwu.setOnClickListener(this);
        layoutXiawu.setOnClickListener(this);
        layoutWanshang.setOnClickListener(this);
        layoutShare.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == layoutAddBottom) {//在底部添加新内容
            switchAddBottom.setChecked(!switchAddBottom.isChecked());
        } else if (v == layoutChooseItemBottom) {//将选中的项移至底部
            switchChooseItemBottom.setChecked(!switchChooseItemBottom.isChecked());
        } else if (v == layoutShangwu) {//上午
            setTimePicker(SettingContact.CHOOSE_TIME_FOR_SHANGWU);
        } else if (v == layoutXiawu) {//下午
            setTimePicker(SettingContact.CHOOSE_TIME_FOR_XIAWU);
        } else if (v == layoutWanshang) {//晚上
            setTimePicker(SettingContact.CHOOSE_TIME_FOR_WANGSHANG);
        } else if (v == layoutShare) {//启用共享功能
            switchOpenShare.setChecked(!switchOpenShare.isChecked());
        }
    }

    private void setTimePicker(final String type) {
        String time = mSharedPreferencesUtil.getString(type, "08:00");
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);
        final TimePickerDialog dialog = new TimePickerDialog(this, R.style.Material_App_Dialog_TimePicker_Light);
        dialog.hour(hour);
        dialog.minute(minute);
        dialog.positiveAction(cn.xmrk.rkandroid.R.string.ok)
                .negativeAction(cn.xmrk.rkandroid.R.string.cancel)
                .cancelable(true)
                .show();
        //表示选择
        dialog.positiveActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = dialog.getHour();
                int minute = dialog.getMinute();

                switch (type) {
                    case SettingContact.CHOOSE_TIME_FOR_SHANGWU://上午
                        if (0 <= hour && hour < 12) {
                            tvTimeShangwu.setText(CommonUtil.changeOne2Two(hour) + ":" + CommonUtil.changeOne2Two(minute));
                            mSharedPreferencesUtil.putString(type, tvTimeShangwu.getText().toString());
                        } else {
                            CommonUtil.showSnackToast(getString(R.string.setting_shangwu_limit), tvTimeShangwu);
                        }
                        break;
                    case SettingContact.CHOOSE_TIME_FOR_XIAWU://下午
                        if (12 <= hour && hour < 18) {
                            tvTimeXiawu.setText(CommonUtil.changeOne2Two(hour) + ":" + CommonUtil.changeOne2Two(minute));
                            mSharedPreferencesUtil.putString(type, tvTimeXiawu.getText().toString());
                        } else {
                            CommonUtil.showSnackToast(getString(R.string.setting_xaiwu_limit), tvTimeXiawu);
                        }
                        break;
                    case SettingContact.CHOOSE_TIME_FOR_WANGSHANG://晚上
                        if (18 <= hour && hour < 24) {
                            tvTimeWanshang.setText(CommonUtil.changeOne2Two(hour) + ":" + CommonUtil.changeOne2Two(minute));
                            mSharedPreferencesUtil.putString(type, tvTimeWanshang.getText().toString());
                        } else {
                            CommonUtil.showSnackToast(getString(R.string.setting_wanshang_limit), tvTimeWanshang);
                        }
                        break;
                }


                dialog.dismiss();
            }
        });
        //表示取消
        dialog.negativeActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}

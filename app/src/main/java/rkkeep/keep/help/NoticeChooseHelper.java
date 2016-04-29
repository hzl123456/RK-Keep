package rkkeep.keep.help;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.TimePickerDialog;

import java.util.Date;

import rkkeep.keep.R;
import rkkeep.keep.activity.ChooseAddressActivity;
import rkkeep.keep.pojo.AddressInfo;

/**
 * Created by Au61 on 2016/4/28.
 */
public class NoticeChooseHelper implements IMessageOperate, View.OnClickListener {

    private final int CHOOSE_ADDRESS = 99;


    private Activity activity;

    private Dialog mDialog;

    private View view;

    /**
     * 选择的时间
     **/
    private Date chooseDate;


    public NoticeChooseHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 表示的选择时间或者地址的弹窗
     **/
    public void showDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(activity);
        }
        view = activity.getLayoutInflater().inflate(R.layout.layout_choose_time_address, null);
        LinearLayout layoutChooseTime = (LinearLayout) view.findViewById(R.id.layout_choose_time);
        LinearLayout layoutChooseAddress = (LinearLayout) view.findViewById(R.id.layout_choose_address);
        layoutChooseTime.setOnClickListener(this);
        layoutChooseAddress.setOnClickListener(this);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    /**
     * 表示的选择时间或者删除
     **/
    public void showTimeDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(activity);
        }
        view = activity.getLayoutInflater().inflate(R.layout.layout_edit_notice_time, null);
        LinearLayout layoutChooseTime = (LinearLayout) view.findViewById(R.id.layout_choose_time);
        LinearLayout layoutChooseAddress = (LinearLayout) view.findViewById(R.id.layout_delete_time);
        layoutChooseTime.setOnClickListener(this);
        layoutChooseAddress.setOnClickListener(this);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();

    }

    /**
     * 表示的选择地址或者删除
     **/
    public void showAddressDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(activity);
        }
        view = activity.getLayoutInflater().inflate(R.layout.layout_edit_notice_address, null);
        LinearLayout layoutChooseTime = (LinearLayout) view.findViewById(R.id.layout_choose_address);
        LinearLayout layoutChooseAddress = (LinearLayout) view.findViewById(R.id.layout_delete_address);
        layoutChooseTime.setOnClickListener(this);
        layoutChooseAddress.setOnClickListener(this);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();

    }


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED && requestCode == CHOOSE_ADDRESS) {//选择地址回来
            if (mOnNoticeChooseListener != null) {
                mOnNoticeChooseListener.OnAddress((AddressInfo) data.getExtras().get("data"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (v == view.findViewById(R.id.layout_choose_time)) {//选择日期和时间
            chooseTime();
        } else if (v == view.findViewById(R.id.layout_choose_address)) {//选择地址
            activity.startActivityForResult(new Intent(activity, ChooseAddressActivity.class), CHOOSE_ADDRESS);
        } else if (v == view.findViewById(R.id.layout_delete_time)) {//删除时间
            //删除时间，那么就返回null的
            if (mOnNoticeChooseListener != null) {
                mOnNoticeChooseListener.OnTime(null);
            }
        } else if (v == view.findViewById(R.id.layout_delete_address)) {//删除地址
            //删除地址，那么就返回null的
            if (mOnNoticeChooseListener != null) {
                mOnNoticeChooseListener.OnAddress(null);
            }
        }
    }

    private void chooseTime() {

        final DatePickerDialog datePickerDialog = new DatePickerDialog(activity, R.style.Material_App_Dialog_DatePicker_Light);
        Date nowDate = new Date(System.currentTimeMillis());
        Date maxDate = new Date(nowDate.getYear(), 11, 31);
        datePickerDialog.date(nowDate.getTime()).dateRange(nowDate.getTime(), maxDate.getTime()).positiveAction(cn.xmrk.rkandroid.R.string.ok)
                .negativeAction(cn.xmrk.rkandroid.R.string.cancel)
                .cancelable(true)
                .show();
        datePickerDialog.positiveActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.dismiss();
                chooseDate = new Date(datePickerDialog.getDate());
                final TimePickerDialog timePickerDialog = new TimePickerDialog(activity, R.style.Material_App_Dialog_TimePicker_Light);
                timePickerDialog.positiveAction(cn.xmrk.rkandroid.R.string.ok)
                        .negativeAction(cn.xmrk.rkandroid.R.string.cancel)
                        .cancelable(true)
                        .show();
                timePickerDialog.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timePickerDialog.dismiss();
                        chooseDate.setHours(timePickerDialog.getHour());
                        chooseDate.setMinutes(timePickerDialog.getMinute());
                        if (mOnNoticeChooseListener != null) {
                            mOnNoticeChooseListener.OnTime(chooseDate);
                        }
                    }
                });
                timePickerDialog.negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timePickerDialog.dismiss();
                    }
                });
            }
        });
        datePickerDialog.negativeActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.dismiss();
            }
        });
    }


    private OnNoticeChooseListener mOnNoticeChooseListener;

    public void setOnNoticeChooseListener(OnNoticeChooseListener mOnNoticeChooseListener) {
        this.mOnNoticeChooseListener = mOnNoticeChooseListener;
    }

    public interface OnNoticeChooseListener {

        void OnTime(Date date);

        void OnAddress(AddressInfo addressInfo);
    }


}

package rkkeep.keep.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.widget.imageView.RoundImageView;
import rkkeep.keep.R;
import rkkeep.keep.widget.HandWritingView;

/**
 * Created by Au61 on 2016/5/13.
 */
public class HandWritingActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibBack;
    private ImageButton ibFront;
    private ImageButton ibShare;

    private HandWritingView mHandWritingView;

    private RadioGroup rgGroup;
    private RoundImageView ibChooseColor;
    private ImageButton ibChooseWidth;
    private ImageButton ibClear;

    private ColorPickerDialog mColorDialog;

    /**
     * 当前选中的颜色，默认初始为红色
     **/
    private int checkColor = Color.RED;

    private RelativeLayout layoutPaintSize;
    private SeekBar seekBarPaintSize;
    private TextView ivPaintSize;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handwriting);
        initTitle();
        findViews();
        initView();
    }

    private void findViews() {
        mHandWritingView = (HandWritingView) findViewById(R.id.view);
        rgGroup = (RadioGroup) findViewById(R.id.rg_group);
        ibChooseColor = (RoundImageView) findViewById(R.id.ib_choose_color);
        ibChooseWidth = (ImageButton) findViewById(R.id.ib_choose_width);
        ibClear = (ImageButton) findViewById(R.id.ib_clear);
        layoutPaintSize = (RelativeLayout) findViewById(R.id.layout_paint_size);
        ivPaintSize = (TextView) findViewById(R.id.iv_paint_size);
        seekBarPaintSize = (SeekBar) findViewById(R.id.seek_paint_size);


        ibChooseColor.setImageDrawable(new ColorDrawable(checkColor));

        ibChooseColor.setOnClickListener(this);
        ibChooseWidth.setOnClickListener(this);
        ibClear.setOnClickListener(this);

        CommonUtil.setLongClick(ibChooseColor, "选择画笔的颜色");
        CommonUtil.setLongClick(ibChooseWidth, "选择画笔的大小");
        CommonUtil.setLongClick(ibClear, "清空当前画布");

        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_rubish) {
                    mHandWritingView.setIsClear(true);
                } else {
                    mHandWritingView.setIsClear(false);
                }
            }
        });

        seekBarPaintSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = getStokeWidth() * 2;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size, size);
                layoutParams.setMargins((CommonUtil.dip2px(56)-size)/2,(CommonUtil.dip2px(56)-size)/2,0,0);
                ivPaintSize.setLayoutParams(layoutParams);
                mHandWritingView.setNowStokeWidth(getStokeWidth());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public int getStokeWidth() {
        return CommonUtil.dip2px(seekBarPaintSize.getProgress()+1);
    }


    private void setColorContentVisibility() {
        if (layoutPaintSize.getVisibility() == View.VISIBLE) {
            layoutPaintSize.setVisibility(View.GONE);
        } else {
            layoutPaintSize.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        //设置图片和画笔才可以使用
        mHandWritingView.setPaint(checkColor, getStokeWidth());
        mHandWritingView.setBitmap(null);
        mHandWritingView.setonDrawingListener(new HandWritingView.onDrawingListener() {
            @Override
            public void canBack(boolean canBack) {
                ibBack.setEnabled(canBack);
            }

            @Override
            public void canFront(boolean canFront) {
                ibFront.setEnabled(canFront);
            }
        });
    }

    private void initTitle() {
        View titleView = View.inflate(this, R.layout.title_handwrite, null);
        ibBack = (ImageButton) titleView.findViewById(R.id.ib_back);
        ibFront = (ImageButton) titleView.findViewById(R.id.ib_front);
        ibShare = (ImageButton) titleView.findViewById(R.id.ib_share);


        ibBack.setOnClickListener(this);
        ibFront.setOnClickListener(this);
        ibShare.setOnClickListener(this);

        CommonUtil.setLongClick(ibShare, getString(R.string.share_my_pic));

        ibBack.setEnabled(false);
        ibFront.setEnabled(false);

        getTitlebar().addView(titleView);
        getTitlebar().setBackgroundResource(R.color.bg_white);
        getTitlebar().setNavigationIcon(R.drawable.ic_material_arrow_left_dark);
        getTitlebar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == ibBack) {
            mHandWritingView.drawBack();
        } else if (v == ibFront) {
            mHandWritingView.drawFront();
        } else if (v == ibShare) {

        } else if (v == ibChooseColor) {
            showChooseColor();
        } else if (v == ibChooseWidth) {
            setColorContentVisibility();
        } else if (v == ibClear) {
            showDialogMessage("确定清空画布", null, getString(R.string.ok), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mHandWritingView.clearDraw();
                }
            }, null);
        }
    }

    private void showChooseColor() {
        mColorDialog = new ColorPickerDialog(this, checkColor);
        mColorDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                checkColor = color;
                ibChooseColor.setImageDrawable(new ColorDrawable(checkColor));
                mHandWritingView.setPaint(checkColor, getStokeWidth());
                mColorDialog.dismiss();
            }
        });
        mColorDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

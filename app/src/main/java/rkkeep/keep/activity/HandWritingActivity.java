package rkkeep.keep.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import java.io.File;

import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.FileUtil;
import cn.xmrk.rkandroid.widget.imageView.RoundImageView;
import rkkeep.keep.R;
import rkkeep.keep.util.VoiceUtil;
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

    /**
     * 加载的背景图
     **/
    private Bitmap drawBitmap;

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            if (msg.what == 0) {
                getPDM().dismiss();
                findViews();
                initView();
            } else if (msg.what == 1) {
                getPDM().dismiss();
                Intent intent = new Intent();
                intent.putExtra("data", (String) msg.obj);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handwriting);
        initTitle();
        initBitmap();
    }

    private void initBitmap() {
        getPDM().showProgress("正在加载绘图");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String path = getIntent().getExtras().getString("data");
                    drawBitmap = BitmapFactory.decodeFile(path);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mHandler.sendMessage(mHandler.obtainMessage(0));
                }
            }
        }).start();
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
                layoutParams.setMargins((CommonUtil.dip2px(56) - size) / 2, (CommonUtil.dip2px(56) - size) / 2, 0, 0);
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
        return CommonUtil.dip2px(seekBarPaintSize.getProgress() + 1);
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
        mHandWritingView.setBitmap(drawBitmap);
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
            shareBitmap();
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

    //分享绘图
    public void shareBitmap() {
        if (mHandWritingView.hasDrawBitmap()) {
            String path = VoiceUtil.getImagePath() + System.currentTimeMillis() + ".png";
            FileUtil.saveBmpToFilePng(mHandWritingView.getDrawBitmap(), new File(path));
            Uri uri = Uri.parse("file://" + path);
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_STREAM, uri);
            it.setType("image/*");
            startActivityForResult(Intent.createChooser(it,
                    getString(R.string.share_my_pic)), 10);
        } else {
            CommonUtil.showSnackToast("绘图未发生改变", getTitlebar());
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutPaintSize.getVisibility() == View.VISIBLE) {
            layoutPaintSize.setVisibility(View.GONE);
        } else if (mHandWritingView.hasDrawBitmap()) {
            //保存图片，返回路径
            getPDM().showProgress("正在生成绘图");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String path = VoiceUtil.getImagePath() + System.currentTimeMillis() + ".png";
                    FileUtil.saveBmpToFilePng(mHandWritingView.getDrawBitmap(), new File(path));
                    mHandler.sendMessage(mHandler.obtainMessage(1, path));
                }
            }).start();
        } else {
            super.onBackPressed();
        }
    }
}

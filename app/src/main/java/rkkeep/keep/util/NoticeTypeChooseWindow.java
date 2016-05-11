package rkkeep.keep.util;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import rkkeep.keep.R;
import rkkeep.keep.adapter.NoticeTypeAdapter;

/**
 * Created by Au61 on 2016/4/27.
 */
public class NoticeTypeChooseWindow extends PopupWindow implements View.OnClickListener {

    public static final int CHOOSE_PICTURE = 1;
    public static final int CHOOSE_DRAW = 2;
    public static final int CHOOSE_VOICE = 3;

    private LinearLayout layoutPicture;
    private LinearLayout layoutDraw;
    private LinearLayout tvVoice;
    private RecyclerView rvContent;
    private LinearLayout layoutContent;
    private View view;

    private Context mContent;
    private LinearLayoutManager manager;
    private NoticeTypeAdapter adapter;
    private String checkColor;
    private View headerView;
    private View viewDismiss;

    public NoticeTypeChooseWindow(String checkColor, Context context) {
        this.checkColor = checkColor;
        mContent = context;
        findViews();
        init();
        setLayoutBackground(checkColor);
    }

    private void findViews() {
        headerView = View.inflate(mContent, R.layout.layout_choose_type, null);
        layoutContent = (LinearLayout) headerView.findViewById(R.id.layout_content);
        layoutPicture = (LinearLayout) headerView.findViewById(R.id.layout_picture);
        layoutDraw = (LinearLayout) headerView.findViewById(R.id.layout_draw);
        tvVoice = (LinearLayout) headerView.findViewById(R.id.tv_voice);
        rvContent = (RecyclerView) headerView.findViewById(R.id.rv_content);
        view = headerView.findViewById(R.id.view);
        viewDismiss = headerView.findViewById(R.id.view_dismiss);

        layoutPicture.setOnClickListener(this);
        layoutDraw.setOnClickListener(this);
        tvVoice.setOnClickListener(this);
        view.setOnClickListener(this);
        viewDismiss.setOnClickListener(this);
        setContentView(headerView);
    }

    private void init() {
        manager = new GridLayoutManager(mContent, 8);
        rvContent.setLayoutManager(manager);
        adapter = new NoticeTypeAdapter(checkColor);
        rvContent.setAdapter(adapter);

        adapter.setOnColorChooseListener(new NoticeTypeAdapter.OnColorChooseListener() {
            @Override
            public void OnChoose(String color) {
                setLayoutBackground(color);
                adapter.setCheckColor(color);
                adapter.notifyDataSetChanged();
                if (mOnWindowChooseListener != null) {
                    mOnWindowChooseListener.OnChooseColor(color);
                }
            }
        });
    }

    private void setLayoutBackground(String color) {
        layoutContent.setBackgroundColor(Color.parseColor(color));
    }

    public void showPopuWindow(View view) {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.PopupAnimation);
        setOutsideTouchable(true);

        showAtLocation(view, Gravity.TOP, 0, 0);
    }

    @Override
    public void onClick(View v) {
        if (v == layoutPicture) {
            mOnWindowChooseListener.OnChooseType(CHOOSE_PICTURE);
        } else if (v == layoutDraw) {
            mOnWindowChooseListener.OnChooseType(CHOOSE_DRAW);
        } else if (v == tvVoice) {
            mOnWindowChooseListener.OnChooseType(CHOOSE_VOICE);
        }
        dismiss();
    }

    private OnWindowChooseListener mOnWindowChooseListener;

    public void setOnWindowChooseListener(OnWindowChooseListener mOnWindowChooseListener) {
        this.mOnWindowChooseListener = mOnWindowChooseListener;
    }


    public interface OnWindowChooseListener {

        void OnChooseColor(String color);

        void OnChooseType(int type);

    }
}

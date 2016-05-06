package rkkeep.keep.help;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rey.material.app.Dialog;

import rkkeep.keep.R;
import rkkeep.keep.adapter.NoticeTypeLargeAdapter;

/**
 * Created by Au61 on 2016/5/6.
 */
public class ChangeColorDialog extends Dialog {

    private View headerView;
    private RecyclerView rvContent;
    private NoticeTypeLargeAdapter mAdapter;
    private GridLayoutManager layoutManger;

    public ChangeColorDialog(Context context) {
        super(context);
        initViews();
    }

    public ChangeColorDialog(Context context, int style) {
        super(context, style);
        initViews();
    }


    private void initViews() {
        headerView = View.inflate(getContext(), R.layout.layout_change_color, null);
        rvContent = (RecyclerView) headerView.findViewById(R.id.rv_content);
        layoutManger = new GridLayoutManager(getContext(), 4);
        mAdapter = new NoticeTypeLargeAdapter("");
        rvContent.setLayoutManager(layoutManger);
        rvContent.setAdapter(mAdapter);
        setContentView(headerView);

        mAdapter.setOnColorChooseListener(new NoticeTypeLargeAdapter.OnColorChooseListener() {
            @Override
            public void OnChoose(String color) {
                mAdapter.setCheckColor(color);
                mAdapter.notifyDataSetChanged();
                dismiss();
                if (mOnColorChooseListener != null) {
                    mOnColorChooseListener.OnChoose(color);
                }
            }
        });
    }

    private OnColorChooseListener mOnColorChooseListener;

    public void setOnColorChooseListener(OnColorChooseListener mOnColorChooseListener) {
        this.mOnColorChooseListener = mOnColorChooseListener;
    }

    public interface OnColorChooseListener {
        void OnChoose(String color);
    }
}

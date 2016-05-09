package rkkeep.keep.fragment;

import java.util.ArrayList;

import rkkeep.keep.R;
import rkkeep.keep.pojo.NoticeInfo;

/**
 * Created by Au61 on 2016/5/9.
 */
public class TiXingFragment extends JiShiFragment {

    @Override
    public int getInfoType() {
        return NoticeInfo.TIXING_TYPE;
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        tvTitleJishi.setText(R.string.nav_tixing);
        ibSearch.setBackgroundResource(R.drawable.btn_click_ripple_title_color_5f7c8a);
        ibLayout.setBackgroundResource(R.drawable.btn_click_ripple_title_color_5f7c8a);
        activity.getTitleBar().setBackgroundResource(R.color.color_5f7c8a);
    }

    @Override
    public void addNoticeInfo(NoticeInfo info) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        if (info.infoType != getInfoType()) {
            return;
        }
        mFooterType = 0;
        mData.add(0, info);
        mAdapter.notifyDataSetChanged();
        rvContent.getLayoutManager().scrollToPosition(0);
    }

    @Override
    protected String getEmptyString() {
        return activity.getString(R.string.no_tixing);
    }

    @Override
    protected int getEmptyResourse() {
        return R.drawable.ic_empty_reminder;
    }
}

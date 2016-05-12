package rkkeep.keep.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.uil.SpacesItemDecoration;
import cn.xmrk.rkandroid.widget.edittext.ClearEditText;
import cn.xmrk.rkandroid.widget.imageView.RoundImageView;
import rkkeep.keep.R;
import rkkeep.keep.adapter.MuilGridAdapter;
import rkkeep.keep.adapter.MuilListVoiceAdapter;
import rkkeep.keep.adapter.NoticeTypeAdapter;
import rkkeep.keep.adapter.listener.OnNoticeBaseViewClickListener;
import rkkeep.keep.adapter.viewholder.NoticeInfoBaseViewHolder;
import rkkeep.keep.db.NoticeInfoDbHelper;
import rkkeep.keep.help.ChangeColorDialog;
import rkkeep.keep.help.ColorHelper;
import rkkeep.keep.pojo.NoticeBaseInfo;
import rkkeep.keep.pojo.NoticeInfo;

/**
 * Created by Au61 on 2016/5/10.
 */
public class SearchNoticeInfoActivity extends BaseActivity implements View.OnClickListener {

    private static int EDIT_CODE = 11;

    private ClearEditText etSearch;
    private ImageButton ibText;
    private ImageButton ibVoice;
    private ImageButton ibPic;
    private ImageButton ibJishi;
    private ImageButton ibColorSet;
    private TextView tvEmpty;

    private RecyclerView rvColor;
    private RecyclerView rvContent;

    private NoticeTypeAdapter adapter;

    private SearchNoticeAdapter mAdapter;

    /**
     * 需要操作的noticeInfo，之后返回上一个页面需要进行修改的
     **/
    private List<NoticeInfo> mData;

    /**
     * 保存所有的处于drag状态下的holder，当为null的时候才可以进行点击事件
     **/
    protected List<NoticeBaseInfo> dragHolder;

    private ChangeColorDialog mDialog;

    private NoticeInfoDbHelper mNoticeInfoDbHelper;

    /**
     * 选中的时候使用
     **/
    protected View titleEditView;
    protected ImageButton ibColor;
    protected ImageButton ibDelete;
    protected TextView tvTitleEdit;
    protected RoundImageView ivRound;

    private boolean ibTextChecked;
    private boolean ibVoiceChecked;
    private boolean ibPicChecked;
    private boolean ibJishiChecked;
    private boolean ibColorSetChecked;

    /**
     * 默认为白色
     **/
    private String checkColor = "ffffffff";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach_notice);
        initData();
        findViews();
        setTitle();
        initColor();
        initRvContent();
    }

    private void initData() {
        dragHolder = new ArrayList<>();
        mData = (List<NoticeInfo>) getIntent().getExtras().get("data");
        mNoticeInfoDbHelper = new NoticeInfoDbHelper();
    }

    private void findViews() {
        ibText = (ImageButton) findViewById(R.id.ib_text);
        ibVoice = (ImageButton) findViewById(R.id.ib_voice);
        ibPic = (ImageButton) findViewById(R.id.ib_pic);
        ibJishi = (ImageButton) findViewById(R.id.ib_jishi);
        ibColorSet = (ImageButton) findViewById(R.id.ib_color);
        rvColor = (RecyclerView) findViewById(R.id.rv_color);
        rvContent = (RecyclerView) findViewById(R.id.rv_content);
        tvEmpty = (TextView) findViewById(R.id.tv_empty);
        ivRound = (RoundImageView) findViewById(R.id.iv_round);

        CommonUtil.setLongClick(ibText, "过滤出清单");
        CommonUtil.setLongClick(ibVoice, "过滤出含音频的记事");
        CommonUtil.setLongClick(ibPic, "过滤出含图片的记事");
        CommonUtil.setLongClick(ibJishi, "过滤出含有提醒的记事");
        CommonUtil.setLongClick(ibColorSet, "选择颜色过滤记事");

        ibText.setOnClickListener(this);
        ibVoice.setOnClickListener(this);
        ibPic.setOnClickListener(this);
        ibJishi.setOnClickListener(this);
        ibColorSet.setOnClickListener(this);
    }

    private void initColor() {
        GridLayoutManager manager = new GridLayoutManager(this, 8);
        rvColor.setLayoutManager(manager);
        adapter = new NoticeTypeAdapter("", false);
        rvColor.setAdapter(adapter);
        adapter.setOnColorChooseListener(new NoticeTypeAdapter.OnColorChooseListener() {
            @Override
            public void OnChoose(String color) {
                checkColor = color;
                adapter.setCheckColor(color);
                adapter.notifyDataSetChanged();

                ivRound.setImageDrawable(new ColorDrawable(Color.parseColor(checkColor)));
                rvColor.setVisibility(View.GONE);
                searchNoticeInfo();
            }
        });
    }

    private void initRvContent() {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvContent.setLayoutManager(manager);
        int spacesSize = CommonUtil.dip2px(3);
        manager = new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL);
        rvContent.addItemDecoration(new SpacesItemDecoration(spacesSize, spacesSize, spacesSize, spacesSize));
        mAdapter = new SearchNoticeAdapter();
        rvContent.setAdapter(mAdapter);
        showEmptyOrData();
    }

    protected void initEditTitle() {
        getTitlebar().removeAllViews();
        if (titleEditView == null) {
            titleEditView = getLayoutInflater().inflate(R.layout.title_jishi_edit, null);
            ibColor = (ImageButton) titleEditView.findViewById(R.id.ib_color);
            ibDelete = (ImageButton) titleEditView.findViewById(R.id.ib_delete);
            tvTitleEdit = (TextView) titleEditView.findViewById(R.id.tv_title);
            ibColor.setOnClickListener(this);
            ibDelete.setOnClickListener(this);
            CommonUtil.setLongClick(ibColor, getString(R.string.change_color));
            CommonUtil.setLongClick(ibDelete, getString(R.string.to_dustbin));
        }
        getTitlebar().addView(titleEditView);
        getTitlebar().setBackgroundResource(R.color.color_9b9b9b);
        getTitlebar().setNavigationIcon(R.drawable.ic_white_back);
        getTitlebar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回记事页面，将所有选中的设置为未选中的状态
                setNullDragHolder();
            }
        });

    }

    //将dragholder设置为空
    public void setNullDragHolder() {
        for (int i = 0; i < dragHolder.size(); i++) {
            dragHolder.get(i).info.isCheck = false;
        }
        dragHolder.clear();
        setTitle();
        mAdapter.notifyDataSetChanged();
    }

    private void initTitle() {
        getTitlebar().removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.title_activity_choose_address, null);
        getTitlebar().addView(view);
        getTitlebar().setBackgroundResource(R.color.bg_white);
        getTitlebar().setNavigationIcon(R.drawable.ic_material_arrow_left_dark);
        getTitlebar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        etSearch = (ClearEditText) view.findViewById(R.id.et_search);
        //设置不需要默认显示
        etSearch.setHint(R.string.search_jishi);
        //监听搜索输入框的输入，进行地址搜索
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchNoticeInfo();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setImageButtonCheck(ImageButton button, boolean checked) {
        if (checked) {
            button.setBackgroundResource(R.drawable.bg_search_checked);
        } else {
            button.setBackgroundResource(R.drawable.bg_search);
        }
    }

    @Override
    public void onBackPressed() {
        if (dragHolder.size() > 0) {
            setNullDragHolder();
        } else {
            Intent intent = new Intent();
            intent.putExtra("data", mData == null ? new ArrayList<NoticeInfo>() : (ArrayList) mData);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void onClick(View v) {
        if (v == ibText) {
            ibTextChecked = !ibTextChecked;
            setImageButtonCheck(ibText, ibTextChecked);
            searchNoticeInfo();
        } else if (v == ibVoice) {
            ibVoiceChecked = !ibVoiceChecked;
            setImageButtonCheck(ibVoice, ibVoiceChecked);
            searchNoticeInfo();
        } else if (v == ibPic) {
            ibPicChecked = !ibPicChecked;
            setImageButtonCheck(ibPic, ibPicChecked);
            searchNoticeInfo();
        } else if (v == ibJishi) {
            ibJishiChecked = !ibJishiChecked;
            setImageButtonCheck(ibJishi, ibJishiChecked);
            searchNoticeInfo();
        } else if (v == ibColorSet) {
            ibColorSetChecked = !ibColorSetChecked;
            setImageButtonCheck(ibColorSet, ibColorSetChecked);
            if (ibColorSetChecked) {
                adapter.setCheckColor(checkColor);
                adapter.notifyDataSetChanged();
                rvColor.setVisibility(View.VISIBLE);
            } else {
                ivRound.setImageResource(0);
                rvColor.setVisibility(View.GONE);
            }
            searchNoticeInfo();
        } else if (v == ibDelete) {//删除
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(rkkeep.keep.R.string.move_to_dustbin);
            dialog.setPositiveButton(cn.xmrk.rkandroid.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int size = mData.size();
                    NoticeInfo info = null;
                    for (int i = 0; i < dragHolder.size(); i++) {
                        info = dragHolder.get(i).info;
                        mNoticeInfoDbHelper.updateNoticeInfoType(info.infoId, NoticeInfo.NOMAL_TYPE_DUSTBIN);
                        mData.remove(info);
                    }
                    mAdapter.notifyItemRangeRemoved(0, size);
                    dragHolder.clear();
                    setTitle();
                    showEmptyOrData();
                    CommonUtil.showSnackToast(getString(rkkeep.keep.R.string.had_move_to_dustbin), rvContent);
                }
            });
            dialog.setNegativeButton(cn.xmrk.rkandroid.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else if (v == ibColor) {//更改颜色
            mDialog = new ChangeColorDialog(this);
            mDialog.show();
            mDialog.setOnColorChooseListener(new ChangeColorDialog.OnColorChooseListener() {
                @Override
                public void OnChoose(String color) {
                    NoticeInfo info = null;
                    for (int i = 0; i < dragHolder.size(); i++) {
                        info = dragHolder.get(i).info;
                        info.color = color;
                        info.isCheck = false;
                        mNoticeInfoDbHelper.saveNoticeInfo(info);
                    }
                    dragHolder.clear();
                    setTitle();
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

    }


    public class SearchNoticeAdapter extends RecyclerView.Adapter<NoticeInfoBaseViewHolder> {

        @Override
        public NoticeInfoBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NoticeInfoBaseViewHolder(View.inflate(parent.getContext(), R.layout.item_noticeinfo_base, null));
        }

        @Override
        public void onBindViewHolder(NoticeInfoBaseViewHolder viewHolder, int position) {
            final NoticeInfoBaseViewHolder holder = (NoticeInfoBaseViewHolder) viewHolder;
            final NoticeInfo info = mData.get(position);

            holder.setBaseInfo(info, position);
            holder.setLayoutTop();
            //表示收到viewholder的点击事件
            holder.setOnViewHolderClickListener(new OnNoticeBaseViewClickListener() {
                @Override
                public void OnViewHolderClick(NoticeBaseInfo baseInfo) {
                    if (dragHolder.size() == 0) {//当这个size为0的时候才可以是跳转
                        toAddNoticeInfoActivity(baseInfo.info);
                    } else {
                        setDragHolder(baseInfo);
                    }
                    setTitle();
                }

                @Override
                public void OnViewHolderLongClick(NoticeBaseInfo baseInfo) {
                    setDragHolder(baseInfo);
                    setTitle();
                }

                @Override
                public void OnViewHolderRemove(NoticeBaseInfo baseInfo) {

                }
            });
            //设置背景颜色
            holder.layoutBae.setBackgroundResource(ColorHelper.getCheckColorRound(info.color));
            //设置标题和内容
            holder.tvTitle.setText(info.title);
            holder.tvContent.setText(info.content);
            //设置布局形式
            holder.layoutNotice.setOrientation(LinearLayout.VERTICAL);
            //设置提醒时间
            if (info.remindTime == 0) {
                holder.tvNoticeTime.setVisibility(View.GONE);
            } else {
                holder.tvNoticeTime.setVisibility(View.VISIBLE);
                holder.tvNoticeTime.setText(CommonUtil.getAffineTimestampForGroupChat(info.remindTime));
            }
            //设置提醒地址
            if (info.addressInfo == null) {
                holder.tvNoticeAddress.setVisibility(View.GONE);
            } else {
                holder.tvNoticeAddress.setVisibility(View.VISIBLE);
                holder.tvNoticeAddress.setText(info.addressInfo.addressName);
            }
            holder.rvContent.setAdapter(new MuilGridAdapter(info.infos));
            //设置语音显示
            holder.lvVoiceContent.setAdapter(new MuilListVoiceAdapter(info.voiceInfos,true));

        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }

    private void toAddNoticeInfoActivity(NoticeInfo info) {
        Intent intent = new Intent(this, AddNoticeActivity.class);
        intent.putExtra("data", info);
        startActivityForResult(intent, EDIT_CODE);
    }

    protected void setDragHolder(NoticeBaseInfo baseInfo) {
        baseInfo.info.isCheck = !baseInfo.info.isCheck;
        baseInfo.holder.setLayoutTop();
        if (baseInfo.info.isCheck) {
            dragHolder.add(baseInfo);
        } else {
            dragHolder.remove(baseInfo);
        }
    }

    protected void setTitle() {
        if (dragHolder.size() == 0) {
            initTitle();
        } else if (dragHolder.size() == 1) {
            initEditTitle();
        }
        if (dragHolder.size() > 0) {
            tvTitleEdit.setText(dragHolder.size() + "");
        }
    }

    private void showEmptyOrData() {
        if (mData == null || mData.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvContent.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED && requestCode == EDIT_CODE) {//修改结束
            NoticeInfo info = (NoticeInfo) data.getExtras().get("data");
            if (mData != null && mData.size() > 0) {
                for (int i = 0; i < mData.size(); i++) {
                    if (info.infoId == mData.get(i).infoId) {
                        mData.remove(i);
                        mData.add(i, info);
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void searchNoticeInfo() {
        mData = mNoticeInfoDbHelper.getNoticeInfoList(etSearch.getText().toString(), ibVoiceChecked, ibPicChecked, ibColorSetChecked ? checkColor : null, !ibJishiChecked);
        mAdapter.notifyDataSetChanged();
        showEmptyOrData();
    }
}

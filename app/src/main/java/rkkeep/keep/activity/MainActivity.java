package rkkeep.keep.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.xmrk.rkandroid.activity.ChoicePicActivity;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.FileUtil;
import cn.xmrk.rkandroid.widget.imageView.RoundImageView;
import rkkeep.keep.R;
import rkkeep.keep.fragment.DustbinFragment;
import rkkeep.keep.fragment.JiShiFragment;
import rkkeep.keep.fragment.RecyclerViewFragment;
import rkkeep.keep.fragment.TiXingFragment;
import rkkeep.keep.help.PictureChooseHelper;
import rkkeep.keep.pojo.NoticeImgVoiceInfo;
import rkkeep.keep.pojo.NoticeInfo;
import rkkeep.keep.pojo.UserInfo;
import rkkeep.keep.util.UserInfoUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final int REQUEST_CHOICE_PORTRAIT = 1;

    /**
     * 分别为添加和修改
     **/
    public final int NOTICE_ADD = 10;
    public final int NOTICE_EDIT = 11;
    public final int NOTICE_EDIT_LIST = 12;

    /**
     * 侧滑的头部使用
     **/
    private View headerView;
    private RoundImageView ivHeader;
    private TextView tvTitle;
    private TextView tvContent;
    private Toolbar toolbar;
    //用户信息
    private UserInfo mUserInfo;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    //底部使用
    private FrameLayout layoutContent;

    private RelativeLayout layoutBottom;
    private TextView tvAddTextItem;
    private ImageButton ibKeepText;
    private ImageButton ibKeepEdit;
    private ImageButton ibKeepVoice;
    private ImageButton ibKeepCamera;

    private PictureChooseHelper mPictureChooseHelper;

    //各个页面的fragment
    private RecyclerViewFragment currentFragment;
    private RecyclerViewFragment mJiShiFragment;
    private RecyclerViewFragment mTiXingFragment;
    private RecyclerViewFragment mDustbinFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initHeaderView();
        setViews();
        initUserInfo();
        initFragment();
        setCurrentFragment(mJiShiFragment);
    }


    private void setCurrentFragment(RecyclerViewFragment fragment) {
        if (fragment == currentFragment) {
            return;
        }
        currentFragment = fragment;
        FragmentTransaction _ft = getSupportFragmentManager().beginTransaction();
        _ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        _ft.replace(R.id.layout_content, fragment);
        _ft.commit();

        if (currentFragment instanceof DustbinFragment) {//这里要隐藏底部的操作栏
            if (layoutBottom.getVisibility() == View.VISIBLE) {
                layoutBottom.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
                layoutBottom.setVisibility(View.GONE);
            }
        } else {
            if (layoutBottom.getVisibility() == View.GONE) {
                layoutBottom.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
                layoutBottom.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 加载fragment，主要的操作页面
     **/
    private void initFragment() {
        mJiShiFragment = new JiShiFragment();
        mTiXingFragment = new TiXingFragment();
        mDustbinFragment = new DustbinFragment();
    }

    private void initUserInfo() {
        mUserInfo = UserInfoUtil.getUserInfo();
        tvTitle.setText(mUserInfo.userName);
        tvContent.setText(mUserInfo.userIntro);
        if (mUserInfo.userPic != null) {
            ImageLoader.getInstance().displayImage("file:///" + mUserInfo.userPic, ivHeader);
        }
    }

    public void setViews() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    if (currentFragment instanceof JiShiFragment) {
                        JiShiFragment fragment = (JiShiFragment) currentFragment;
                        fragment.setNullDragHolder();
                    }
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void findViews() {
        layoutBottom = (RelativeLayout) findViewById(R.id.layout_bottom);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
    }


    public Toolbar getTitleBar() {
        return toolbar;
    }

    private void initHeaderView() {
        headerView = navigationView.getHeaderView(0);
        ivHeader = (RoundImageView) headerView.findViewById(R.id.img_head);
        tvTitle = (TextView) headerView.findViewById(R.id.tv_title);
        tvContent = (TextView) headerView.findViewById(R.id.tv_content);
        ivHeader.setOnClickListener(this);

        layoutContent = (FrameLayout) findViewById(R.id.layout_content);
        tvAddTextItem = (TextView) findViewById(R.id.tv_add_text_item);
        ibKeepText = (ImageButton) findViewById(R.id.ib_keep_text);
        ibKeepEdit = (ImageButton) findViewById(R.id.ib_keep_edit);
        ibKeepVoice = (ImageButton) findViewById(R.id.ib_keep_voice);
        ibKeepCamera = (ImageButton) findViewById(R.id.ib_keep_camera);

        CommonUtil.setLongClick(ibKeepText, getString(R.string.add));
        CommonUtil.setLongClick(ibKeepEdit, getString(R.string.add_draw));
        CommonUtil.setLongClick(ibKeepVoice, getString(R.string.add_voice));
        CommonUtil.setLongClick(ibKeepCamera, getString(R.string.add_picture));

        tvAddTextItem.setOnClickListener(this);
        ibKeepText.setOnClickListener(this);
        ibKeepEdit.setOnClickListener(this);
        ibKeepVoice.setOnClickListener(this);
        ibKeepCamera.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentFragment.canBackActivity()) {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_jishi://记事
                setCurrentFragment(mJiShiFragment);
                break;
            case R.id.nav_tixing://提醒
                setCurrentFragment(mTiXingFragment);
                break;
            case R.id.nav_huishouzhan://回收站
                setCurrentFragment(mDustbinFragment);
                break;
            case R.id.nav_setting://设置
                startActivity(SettingActivity.class);
                break;
            case R.id.nav_help://帮助与反馈
                startActivity(HelpActivity.class);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == ivHeader) {
            startActivityForResult(new Intent(this, ChoicePicActivity.class), REQUEST_CHOICE_PORTRAIT);
        } else if (v == tvAddTextItem) {//文本添加
            setNoticeInfoAndEdit();
        } else if (v == ibKeepText) {//文本添加
            setNoticeInfoAndEdit();
        } else if (v == ibKeepEdit) {//画图添加

        } else if (v == ibKeepVoice) {//录音添加

        } else if (v == ibKeepCamera) {//选择图片添加
            addPicture();
        }
    }

    private void addPicture() {
        if (mPictureChooseHelper == null) {
            mPictureChooseHelper = new PictureChooseHelper(this);
        }
        mPictureChooseHelper.setOnPictureGetListener(new PictureChooseHelper.OnPictureGetListener() {
            @Override
            public void OnPic(String path) {
                setNoticeInfoAndEdit(path);
            }
        });
        mPictureChooseHelper.showDialog();
    }

    //实例化一个noticeInfo，然后跳转下个页面进行编辑,普通
    private void setNoticeInfoAndEdit() {
        NoticeInfo mNoticeInfo = new NoticeInfo();
        mNoticeInfo.infos = new ArrayList<NoticeImgVoiceInfo>();
        toAddNoticeInfoActivity(mNoticeInfo);
    }

    //实例化一个noticeInfo，然后跳转下个页面进行编辑，图片
    private void setNoticeInfoAndEdit(String path) {
        NoticeInfo mNoticeInfo = new NoticeInfo();
        mNoticeInfo.infos = new ArrayList<NoticeImgVoiceInfo>();
        mNoticeInfo.infos.add(new NoticeImgVoiceInfo(path));
        toAddNoticeInfoActivity(mNoticeInfo);
    }

    //实例化一个noticeInfo，然后跳转下个页面进行编辑，语音
    private void setNoticeInfoAndEdit(String path, long length) {
        NoticeInfo mNoticeInfo = new NoticeInfo();
        mNoticeInfo.infos = new ArrayList<NoticeImgVoiceInfo>();
        mNoticeInfo.infos.add(new NoticeImgVoiceInfo(path, length));
        toAddNoticeInfoActivity(mNoticeInfo);
    }

    //跳转编辑页面
    public void toAddNoticeInfoActivity(NoticeInfo info) {
        //默认为添加的
        int code = 0;
        if (info.editTime == 0) {//如果修改时间为0的话表示为添加的，否则为修改的
            info.editTime = System.currentTimeMillis();
            code = NOTICE_ADD;
            if (currentFragment instanceof TiXingFragment) {//提醒的需要默认的添加时间
                info.remindTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
            }

        } else {
            code = NOTICE_EDIT;
        }
        Intent intent = new Intent(this, AddNoticeActivity.class);
        intent.putExtra("data", info);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CHOICE_PORTRAIT) {
            mUserInfo.userPic = FileUtil.uri2Path(data.getData());
            ImageLoader.getInstance().displayImage("file:///" + FileUtil.uri2Path(data.getData()), ivHeader);
            UserInfoUtil.setUserInfo(mUserInfo);
        }
        if (mPictureChooseHelper != null) {
            mPictureChooseHelper.onActivityResult(this, requestCode, resultCode, data);
        }
        if (resultCode == RESULT_OK && requestCode == NOTICE_ADD) {//这里表示的是添加的
            NoticeInfo info = (NoticeInfo) data.getExtras().get("data");
            RecyclerViewFragment mFragment = (RecyclerViewFragment) currentFragment;
            mFragment.addNoticeInfo(info);
        }
        if (resultCode == RESULT_OK && requestCode == NOTICE_EDIT) {//这里表示的是修改的
            NoticeInfo info = (NoticeInfo) data.getExtras().get("data");
            RecyclerViewFragment mFragment = (RecyclerViewFragment) currentFragment;
            mFragment.updateNoticeInfo(info);
        }

        if (resultCode == RESULT_OK && requestCode == NOTICE_EDIT_LIST) {//这里是修改的list
            List<NoticeInfo> infos = (List<NoticeInfo>) data.getExtras().get("data");
            if (infos != null && infos.size() > 0) {
                RecyclerViewFragment mFragment = (RecyclerViewFragment) currentFragment;
                NoticeInfo info = null;
                for (int i = 0; i < infos.size(); i++) {
                    info = infos.get(i);
                    mFragment.updateNoticeInfo(info);
                }
            }
        }
    }

    protected void startActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }

}

package rkkeep.keep.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.xmrk.rkandroid.activity.ChoicePicActivity;
import cn.xmrk.rkandroid.utils.FileUtil;
import cn.xmrk.rkandroid.widget.imageView.RoundImageView;
import rkkeep.keep.R;
import rkkeep.keep.pojo.UserInfo;
import rkkeep.keep.util.UserInfoUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final int REQUEST_CHOICE_PORTRAIT = 1;

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
    private TextView tvAddTextItem;
    private ImageButton ibKeepText;
    private ImageButton ibKeepEdit;
    private ImageButton ibKeepVoice;
    private ImageButton ibKeepCamera;

    //各个页面的fragment


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initHeaderView();
        setViews();
        initUserInfo();
        initFragment();
    }

    /**
     * 加载fragment，主要的操作页面
     **/
    private void initFragment() {


    }

    private void initUserInfo() {
        mUserInfo = UserInfoUtil.getUserInfo();
        tvTitle.setText(mUserInfo.userName);
        tvContent.setText(mUserInfo.userIntro);
        if (mUserInfo.userPic != null) {
            ImageLoader.getInstance().displayImage("file:///" + mUserInfo.userPic, ivHeader);
        }
    }

    private void setViews() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
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
        tvAddTextItem.setOnClickListener(this);
        ibKeepText.setOnClickListener(this);
        ibKeepEdit.setOnClickListener(this);
        ibKeepVoice.setOnClickListener(this);
        ibKeepCamera.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_jishi://记事

                break;
            case R.id.nav_tixing://提醒

                break;
            case R.id.nav_huishouzhan://回收站

                break;
            case R.id.nav_setting://设置
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.nav_help://帮助与反馈

                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == ivHeader) {
            startActivityForResult(new Intent(this, ChoicePicActivity.class), REQUEST_CHOICE_PORTRAIT);
        } else if (v == tvAddTextItem) {//文本添加
            startActivity(AddNoticeActivity.class);
        } else if (v == ibKeepText) {//文本添加
            startActivity(AddNoticeActivity.class);
        } else if (v == ibKeepEdit) {//画图添加

        } else if (v == ibKeepVoice) {//录音添加

        } else if (v == ibKeepCamera) {//选择图片添加

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHOICE_PORTRAIT) {
                mUserInfo.userPic = FileUtil.uri2Path(data.getData());
                ImageLoader.getInstance().displayImage("file:///" + FileUtil.uri2Path(data.getData()), ivHeader);
                UserInfoUtil.setUserInfo(mUserInfo);
            }
        }
    }

    protected void startActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }
}

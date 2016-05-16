package rkkeep.keep.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.RKUtil;
import cn.xmrk.rkandroid.widget.photoview.HackyViewPager;
import cn.xmrk.rkandroid.widget.photoview.PhotoView;
import rkkeep.keep.R;

/**
 * Created by Au61 on 2016/4/28.
 */
public class ShowImageActivity extends BaseActivity implements View.OnClickListener {

    public final int DRAW_PIC_CODE = 56;

    /**
     * 设置新的标题栏
     **/
    private View titleView;
    private TextView tvTitle;
    private ImageButton ibDraw;
    private ImageButton ibSend;
    private ImageButton ibDelete;


    //显示图片使用
    private List<String> urls;
    private int num;
    private HackyViewPager mPager;

    /**
     * 存储修改的position和图片路径
     **/
    private HashMap<Integer, String> changeMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showimage);
        initTitle();
        initData();
        initViewPager();
    }

    private void initData() {
        changeMap = new HashMap<>();

        urls = new ArrayList<String>();
        urls.addAll((ArrayList<String>) getIntent().getExtras().get("data"));
        num = getIntent().getExtras().getInt("num");
        mPager = (HackyViewPager) findViewById(R.id.view_pager);
    }

    private void initViewPager() {
        mPager.setAdapter(new ViewPagerAdapter());
        mPager.setCurrentItem(num);
        tvTitle.setText("第" + (num + 1) + "张，" + "共" + urls.size() + "张");
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                tvTitle.setText("第" + (arg0 + 1) + "张，" + "共" + urls.size() + "张");
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }


    public class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView image = new PhotoView(ShowImageActivity.this);
            RKUtil.displayFileImage(urls.get(position), image);
            container.addView(image, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object arg2) {
            container.removeView((View) arg2);
        }
    }

    private void initTitle() {
        titleView = getLayoutInflater().inflate(R.layout.layout_showimage_title, null);
        tvTitle = (TextView) titleView.findViewById(R.id.tv_title);
        ibDraw = (ImageButton) titleView.findViewById(R.id.ib_draw);
        ibSend = (ImageButton) titleView.findViewById(R.id.ib_send);
        ibDelete = (ImageButton) titleView.findViewById(R.id.ib_delete);

        ibDraw.setOnClickListener(this);
        ibSend.setOnClickListener(this);
        ibDelete.setOnClickListener(this);

        CommonUtil.setLongClick(ibDraw, getString(R.string.pic_draw));
        CommonUtil.setLongClick(ibSend, getString(R.string.pic_show));
        CommonUtil.setLongClick(ibDelete, getString(R.string.pic_delete));


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
        if (v == ibDraw) {
            //跳转绘图的activity
            Intent intent = new Intent(this, HandWritingActivity.class);
            intent.putExtra("data", urls.get(mPager.getCurrentItem()));
            startActivityForResult(intent, DRAW_PIC_CODE);
        } else if (v == ibSend) {//发送给别人
            Uri uri = Uri.parse("file://" + urls.get(mPager.getCurrentItem()));
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_STREAM, uri);
            it.setType("image/*");
            startActivityForResult(Intent.createChooser(it,
                    getString(R.string.share_my_pic)), 10);
        } else if (v == ibDelete) {//删除该张图片,直接返回上一页
            showDialogMessage(getString(R.string.sure_to_delete), null, getString(R.string.ok), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.putExtra("change", changeMap);
                    intent.putExtra("num", mPager.getCurrentItem());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }, null);
        }
    }

    @Override
    public void onBackPressed() {
        //这里表示的是返回的时候修改的，没有删除的时候num为-1
        Intent intent = new Intent();
        intent.putExtra("change", changeMap);
        intent.putExtra("num", -1);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == DRAW_PIC_CODE) {
            String path = data.getExtras().getString("data");
            int position = mPager.getCurrentItem();
            urls.remove(position);
            urls.add(position, path);
            //保存当前修改
            changeMap.put(position, path);
            //刷新当前展示，不知道为嘛，notify不起作用
            mPager.setAdapter(new ViewPagerAdapter());
            mPager.setCurrentItem(position);
        }
    }
}

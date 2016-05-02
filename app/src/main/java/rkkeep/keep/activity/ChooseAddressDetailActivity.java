package rkkeep.keep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import rkkeep.keep.R;
import rkkeep.keep.adapter.PoiNearByAdapter;

/**
 * Created by Administrator on 2016/5/2.
 */
public class ChooseAddressDetailActivity extends AppCompatActivity {


    /**
     * 上个页面的到poiinfo
     **/
    private PoiInfo mPoiInfo;

    /**
     * poi检索,周边搜索
     **/
    private PoiSearch mPoiSearch;


    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private ListView lvContent;
    private MapView mBaiduMap;
    private TextView tvName;
    private TextView tvAddress;

    private PoiNearByAdapter mAdapter;

    /**
     * 地图上选择的位置信息
     **/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address_detail);
        findViews();
        initTitle();
        initBaidu();
        initInfo();
        poiAddress(new LatLng(mPoiInfo.location.latitude, mPoiInfo.location.longitude));
    }

    private void initInfo() {
        tvName.setText(mPoiInfo.name);
        tvAddress.setText(mPoiInfo.address);

        mAdapter = new PoiNearByAdapter();
        lvContent.setAdapter(mAdapter);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击选择了地址进行返回
                Intent intent = new Intent();
                intent.putExtra("info", mAdapter.getDatas().get(position));
                setResult(RESULT_OK, intent);
            }
        });
    }

    private void findViews() {
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        lvContent = (ListView) findViewById(R.id.list);
        mBaiduMap = (MapView) findViewById(R.id.map_view);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        mPoiInfo = (PoiInfo) getIntent().getExtras().get("info");
    }

    private void initBaidu() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
    }

    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        @Override
        public void onGetPoiResult(PoiResult result) {
            //获取检索得到的结果,需要显示周边信息了
            tvAddress.setVisibility(View.VISIBLE);
            //TODO 在这里需要显示地图坐标位置的信息
            List<PoiInfo> infos = result.getAllPoi();
            mAdapter.reflush(infos);
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult result) {
        }
    };

    //这里进行的是poi的周边搜索搜索,通过经纬度进行搜索
    private void poiAddress(LatLng latLng) {
        //TODO 搜索的时候应该显示正在搜索
        tvAddress.setVisibility(View.GONE);
        tvName.setText(R.string.serach_address);
        mPoiSearch.searchNearby(new PoiNearbySearchOption().location(latLng).keyword("银行").pageNum(5));
    }

    private void initTitle() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundResource(R.color.bg_white);
        toolbar.setNavigationIcon(R.drawable.ic_material_arrow_left_dark);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //显示选择的地址
        getSupportActionBar().setTitle(mPoiInfo.address);
    }

}

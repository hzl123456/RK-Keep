package rkkeep.keep.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import rkkeep.keep.R;
import rkkeep.keep.adapter.PoiNearByAdapter;
import rkkeep.keep.help.LocationHelper;

/**
 * Created by Administrator on 2016/5/2.
 */
public class ChooseAddressDetailActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 上个页面的到poiinfo
     **/
    private PoiInfo mPoiInfo;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private ListView lvContent;
    private MapView mBaiduMapView;
    private TextView tvName;
    private TextView tvAddress;
    private ImageButton ibLocation;
    private RelativeLayout layoutAddressChoose;

    private PoiNearByAdapter mAdapter;

    /**
     * 地图上选择的位置信息
     **/
    private BaiduMap mBaiduMap;
    private boolean isFirst = true;

    /**
     * 定位信息
     **/
    private LocationHelper mLocationHelper;
    private BDLocation mBDLocation;
    private GeoCoder geoCoder;

    /**
     * 地图标注物体
     **/
    private Marker mMaker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address_detail);
        findViews();
        initLocation(false);
        initTitle();
        initBaidu();
        initInfo();
        showMarker(new LatLng(mPoiInfo.location.latitude, mPoiInfo.location.longitude));
        getGeoCoder(new LatLng(mPoiInfo.location.latitude, mPoiInfo.location.longitude));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(cn.xmrk.rkandroid.R.color.bg_title_bar));
        }
    }

    //设置地图的中心点
    private void setMapCenter(LatLng cenpt) {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt).zoom(mBaiduMap.getMaxZoomLevel() - 2)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    private void initLocation(final boolean isLocation) {
        if (mLocationHelper == null) {
            mLocationHelper = new LocationHelper();
        }
        mLocationHelper.startLocation();
        mLocationHelper.setOnPoiGetListener(new LocationHelper.OnPoiGetListener() {
            @Override
            public void onGet(BDLocation info) {
                mBDLocation = info;
                if (isLocation) {//表示定位
                    setMapCenter(new LatLng(mBDLocation.getLatitude(), mBDLocation.getLongitude()));
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationHelper != null) {
            mLocationHelper.stopLocation();
        }
        if (geoCoder != null) {
            geoCoder.destroy();
        }
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
                intent.putExtra("data", mAdapter.getDatas().get(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void findViews() {
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        lvContent = (ListView) findViewById(R.id.list);
        mBaiduMapView = (MapView) findViewById(R.id.map_view);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        ibLocation = (ImageButton) findViewById(R.id.ib_location);
        layoutAddressChoose = (RelativeLayout) findViewById(R.id.layout_address_choose);
        mPoiInfo = (PoiInfo) getIntent().getExtras().get("data");
        ibLocation.setOnClickListener(this);
        layoutAddressChoose.setOnClickListener(this);
    }

    //根据经纬度来绘制marker
    private void showMarker(LatLng latLng) {
        //首先移除已经存在的标注物
        if (mMaker != null) {
            mMaker.remove();
        }
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_address_location);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .icon(bitmap).animateType(MarkerOptions.MarkerAnimateType.grow);
        //在地图上添加Marker，并显示
        mMaker = (Marker) mBaiduMap.addOverlay(option);
    }

    private void initBaidu() {
        //定义百度基础地图
        mBaiduMap = mBaiduMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //显示地图上的poi地址
        mBaiduMap.showMapPoi(true);
        //设定中心点坐标
        setMapCenter(new LatLng(mPoiInfo.location.latitude, mPoiInfo.location.longitude));
        //隐藏缩放控件
        mBaiduMapView.showZoomControls(false);
        //隐藏比例尺
        mBaiduMapView.showScaleControl(false);
        //设置地图的点击事件
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //点击地图显示新的地点和它边上的poi
                showMarker(latLng);
                getGeoCoder(latLng);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                //点击地图显示新的地点和它边上的poi,在这里使用poi的名称
                isFirst = true;
                mPoiInfo.name = mapPoi.getName();
                showMarker(mapPoi.getPosition());
                getGeoCoder(mapPoi.getPosition());
                return false;
            }
        });
    }

    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
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
        getSupportActionBar().setTitle(mPoiInfo.name);
    }

    @Override
    public void onClick(View v) {
        if (v == ibLocation) {//定位
            if (mBDLocation != null) {
                LatLng latLng = new LatLng(mBDLocation.getLatitude(), mBDLocation.getLongitude());
                setMapCenter(latLng);
                showMarker(latLng);
                getGeoCoder(latLng);
            } else {
                initLocation(true);
            }
        } else if (v == layoutAddressChoose) {//选择改地址
            Intent intent = new Intent();
            intent.putExtra("data", mPoiInfo);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    //根据经纬度去获取信息以及周边的信息
    private void getGeoCoder(LatLng mCenterLatLng) {
        if (geoCoder == null) {
            geoCoder = GeoCoder.newInstance();
        }
        tvAddress.setVisibility(View.GONE);
        tvName.setText(R.string.serach_address);
        //发起反地理编码请求(经纬度->地址信息)
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mCenterLatLng));
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                //获取地址信息
                tvAddress.setVisibility(View.VISIBLE);
                String address = reverseGeoCodeResult.getAddress();
                //第一次调用使用原来的信息
                if (!isFirst) {
                    mPoiInfo.name = address;
                    mPoiInfo.address = address;
                    mPoiInfo.location = new LatLng(reverseGeoCodeResult.getLocation().latitude, reverseGeoCodeResult.getLocation().longitude);
                }
                isFirst = false;
                tvName.setText(mPoiInfo.name);
                tvAddress.setText(mPoiInfo.address);
                //获取检索得到的结果,需要显示周边信息了
                tvAddress.setVisibility(View.VISIBLE);
                List<PoiInfo> infos = reverseGeoCodeResult.getPoiList();
                mAdapter.reflush(infos);
            }
        });
    }
}

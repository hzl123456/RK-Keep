package rkkeep.keep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.List;

import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.utils.StringUtil;
import cn.xmrk.rkandroid.widget.edittext.ClearEditText;
import rkkeep.keep.R;
import rkkeep.keep.adapter.PoiAdapter;
import rkkeep.keep.help.LocationHelper;
import rkkeep.keep.pojo.AddressInfo;

/**
 * Created by Au61 on 2016/4/29.
 */
public class ChooseAddressActivity extends BaseActivity implements View.OnClickListener {

    private final int CHOOSE_ADDRESS_DETAIL = 10;

    private ClearEditText etSearch;
    private LocationHelper mLocationHelper;

    /**
     * poi检索
     **/
    private PoiSearch mPoiSearch;
    private String cityName;
    private ListView lvContent;
    private PoiAdapter mPoiAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);
        initTitleBar();
        initBaidu();
        initPoi();
    }

    private void initPoi() {
        lvContent = (ListView) findViewById(R.id.lv_content);
        mPoiAdapter = new PoiAdapter();
        lvContent.setAdapter(mPoiAdapter);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击选择了地址，跳转下一个页面选择详情
                Intent intent = new Intent(ChooseAddressActivity.this, ChooseAddressDetailActivity.class);
                intent.putExtra("data", mPoiAdapter.getDatas().get(position));
                startActivityForResult(intent, CHOOSE_ADDRESS_DETAIL);

            }
        });
    }

    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        @Override
        public void onGetPoiResult(PoiResult result) {
            //获取检索得到的结果
            List<PoiInfo> infos = result.getAllPoi();
            mPoiAdapter.reflush(infos);
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult result) {
            //获取Place详情页检索结果
        }
    };

    private void initBaidu() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        mLocationHelper = new LocationHelper();
        mLocationHelper.startLocation();

        mLocationHelper.setOnPoiGetListener(new LocationHelper.OnPoiGetListener() {
            @Override
            public void onGet(BDLocation location) {
                String city = location.getCity();
                cityName = city.endsWith("市") ? city.substring(0, city.length() - 1) : city;
            }
        });
    }

    //只能进行城市内的poi搜索，没有全局的搜索
    private void poiAddress(String text) {
        if (!StringUtil.isEmptyString(cityName)) {
            mPoiSearch.searchInCity((new PoiCitySearchOption())
                    .city(cityName)
                    .keyword(text));
        }
    }


    private void initTitleBar() {
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

        //监听搜索输入框的输入，进行地址搜索
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (StringUtil.isEmptyString(etSearch.getText().toString())) {
                    mPoiAdapter.reflush(null);
                } else {
                    poiAddress(etSearch.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }
        if (mLocationHelper != null) {
            mLocationHelper.stopLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED && requestCode == CHOOSE_ADDRESS_DETAIL) {
            //表示已经选择了正确的地址，这个时候返回地址的详情，然后给上个页面进行处理
            AddressInfo addressInfo = new AddressInfo();
            PoiInfo poiInfo = (PoiInfo) data.getExtras().get("data");
            addressInfo.addressName = poiInfo.name;
            addressInfo.addressIntro = poiInfo.address;
            addressInfo.latitude = poiInfo.location.latitude;
            addressInfo.longitude = poiInfo.location.longitude;
            Intent intent = new Intent();
            intent.putExtra("data", addressInfo);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}

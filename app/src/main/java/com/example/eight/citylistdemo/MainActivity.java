package com.example.eight.citylistdemo;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eight.citylistdemo.db.City;
import com.example.eight.citylistdemo.db.County;
import com.example.eight.citylistdemo.db.Province;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private static final String TAG = "MainActivity";
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince; // 选中省份
    private City selectedCity; // 选中城市
    private int currentLevel; // 当前层次

    private ProgressDialog progressDialog;
    private static Handler handler = new Handler();

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.city_list_view)
    ListView cityListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        title.setTextColor(Color.WHITE);

        adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, dataList);
        cityListView.setAdapter(adapter);
        initData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (currentLevel == LEVEL_COUNTY) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    queryProvinces();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                } else if (currentLevel == LEVEL_PROVINCE) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initDataBase() {
        List<Province> provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() != 34) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    showProgressDialog();
                    CityListUtils.handleCityListFromJSON(getApplicationContext());
                    closeProgressDialog();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            queryProvinces();
                        }
                    });
                }
            }).start();
        }
    }


    private void initData() {
        queryProvinces();
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String selectedCountyId = countyList.get(position).getCountyNo();
                    String selectedCountyName = countyList.get(position).getCountyName();
                    Snackbar.make(view, "选中了: " + selectedCountyName + selectedCountyId,
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 从数据库查询省/直辖市/自治区列表,更新 ListView ,查不到则解析 JSON 来获取
     */
    private void queryProvinces() {
        title.setText("请选择省份");
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() == 34) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            cityListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            DataSupport.deleteAll(Province.class);
            initDataBase();
        }

    }

    /**
     * 从数据库查询市列表,更新 ListView ,查不到则解析 JSON 来获取
     */
    private void queryCities() {
        title.setText(selectedProvince.getProvinceName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cityList = DataSupport.where("provinceId = ?",
                String.valueOf(selectedProvince.getProvinceName())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            cityListView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            DataSupport.deleteAll(City.class);
            initDataBase();
        }

    }

    /**
     * 从数据库查询县/区列表,更新 ListView ,查不到则解析 JSON 来获取
     */
    private void queryCounties() {
        title.setText(selectedCity.getCityName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        countyList = DataSupport.where("cityId = ?",
                String.valueOf(selectedCity.getCityName())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            cityListView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            DataSupport.deleteAll(County.class);
            initDataBase();
        }
    }

    private void showProgressDialog() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    String message = "正在初始化数据,请不要退出...";
                    SpannableString string = new SpannableString(message);
                    string.setSpan(new ForegroundColorSpan(Color.BLACK), 0, message.length(), 0);
                    progressDialog.setMessage(string);
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.show();
            }
        });
    }

    private void closeProgressDialog() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        });
    }

}

#  用 LitePal 库做一个简单城市选择器

## 准备

1. 在`build.garde(Module:app)`里面加入

```java
compile 'org.litepal.android:core:1.5.1'
```

​	还要在`AndroidManifest.xml`里加入一行`android:name="org.litepal.LitePalApplication"`

```xml
<application
    android:name="org.litepal.LitePalApplication"
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
             .../>
</application>
```

2. 创建存储的类：

```java
// Province.java
public class Province extends DataSupport {
    private String provinceName; // 中文省名
    private String provincePY; // 英文省名
}
// City.java
public class City extends DataSupport {
    private String cityName; // 中文市名
    private String provinceId; // 所属省名
}
// County.java
public class County extends DataSupport {
    private String countyName; // 中文县名
    private String countyNo; // 城市代码
    private String countyPY; // 英文县名
    private String cityId; // 所属市名
}
```

3. 配置 `litepal.xml` 文件。右击 `app/src/main` 目录`-> New -> Directory `,创建一个 `assets` 目录，然后

在 `assets` 目录下再新建一个 `litepal.xml` 文件，接着编辑 `litepal.xml` 文件中的内容，如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<litepal>
    <dbname value="city_db" ></dbname>
    <version value="1" ></version>
    <list>
        <mapping class="com.example.eight.citylistdemo.db.Province"></mapping>
        <mapping class="com.example.eight.citylistdemo.db.City"></mapping>
        <mapping class="com.example.eight.citylistdemo.db.County"></mapping>
    </list>
</litepal>
```



## 用 Gson 解析 json 数据并存储

```java
public static boolean handleCityListFromJSON(String jsonString) {    
	Gson gson = new Gson();
    // 解析省级列表
    List<JsonUtils> cityListUtilList = gson.fromJson(stringBuilder.toString(),
            new TypeToken<List<JsonUtils>>(){}.getType());
    for (JsonUtils cityListUtil : cityListUtilList){
        Province province = new Province();
        province.setProvinceName(cityListUtil.getProvinceName());
        province.setProvincePY(cityListUtil.getProvincePY());
        province.save();

        // 解析市级列表
        List<JsonUtils.CityBean> cityBeenList = cityListUtil.getCity();
        for (JsonUtils.CityBean cityBean : cityBeenList) {
            City city = new City();
            city.setCityName(cityBean.getCityName());
            city.setProvinceId(cityListUtil.getProvinceName());
            city.save();

            // 解析县级列表
            List<JsonUtils.CityBean.CountyBean> countyBeanList = cityBean.getCounty();
            for (JsonUtils.CityBean.CountyBean countyBean : countyBeanList) {
                County county = new County();
                county.setCountyName(countyBean.getCountyName());
                county.setCountyPY(countyBean.getCountyPY());
                county.setCountyNo(countyBean.getCountyNo());
                county.setCityId(cityBean.getCityName());
                county.save();
            }
        }
    }
    return true;
}
```



## 城市选择器实现

main_activity.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.example.eight.citylistdemo.MainActivity"
    android:orientation="vertical">

    <android.support.v7.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/colorPrimary">

        <TextView
            android:id="@+id/title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:gravity="center"
            android:textSize="18sp"/>
    </android.support.v7.widget.Toolbar>

    <ListView
        android:id="@+id/city_list_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</LinearLayout>
```

MainActivity.java

```java
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
```

全部源码：[GitHub](https://github.com/bazhancong/CityListDemo)

参考：郭霖 《第二行代码》

## 效果图

![](https://raw.githubusercontent.com/bazhancong/CityListDemo/master/screenshot/0.png)
![](https://raw.githubusercontent.com/bazhancong/CityListDemo/master/screenshot/1.png)
![](https://raw.githubusercontent.com/bazhancong/CityListDemo/master/screenshot/2.png)
![](https://raw.githubusercontent.com/bazhancong/CityListDemo/master/screenshot/3.png)
![](https://raw.githubusercontent.com/bazhancong/CityListDemo/master/screenshot/4.png)
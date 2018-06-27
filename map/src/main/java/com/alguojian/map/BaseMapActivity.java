package com.alguojian.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;
import com.amap.api.maps.model.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BaseMapActivity extends AppCompatActivity {

    public MapView mapView;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    public AMapLocationClientOption mAMapLocationClientOption = null;

    private AMap mMap;
    private Marker mMarker;
    private Circle mCircle;

    private boolean flag;

    private ArrayList<MarkerViewBean> mMarkerViewBeans = new ArrayList<>();

    /**
     * 用于管理所有的覆盖物
     */
    private ArrayList<Marker> mMarkers=new ArrayList<>();

    public static void start(Context context) {
        Intent starter = new Intent(context, BaseMapActivity.class);
        context.startActivity(starter);
    }

    private void initView() {
        mapView = findViewById(R.id.mapView);
    }

    private void init() {

        UiSettings uiSettings = mMap.getUiSettings();

        //隐藏高德logo
        uiSettings.setLogoBottomMargin(-50);

        //添加指南针
        uiSettings.setCompassEnabled(false);

        //隐藏缩放按钮
        uiSettings.setZoomControlsEnabled(false);

        //设置地图是否可以手势滑动
        uiSettings.setScrollGesturesEnabled(true);

        // 设置地图是否可以手势缩放大小
        uiSettings.setZoomGesturesEnabled(true);

        //设置地图是否可以倾斜
        uiSettings.setTiltGesturesEnabled(false);

        // 设置地图是否可以旋转
        uiSettings.setRotateGesturesEnabled(false);

        //显示比例尺
        uiSettings.setScaleControlsEnabled(true);

        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //连续定位、且将视角移动到地图中心点，
        //定位点依照设备方向旋转，并且会跟随设备移动。
        //1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        MyLocationStyle myLocationStyle = new MyLocationStyle();

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);

        //设置定位蓝点的Style
        mMap.setMyLocationStyle(myLocationStyle);

        //显示定位蓝点
        myLocationStyle.showMyLocation(false);

        //自定义定位蓝点icon
//        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.list_green_place);
//        myLocationStyle.myLocationIcon(mCurrentMarker);

        //设置锚点和定位图标关联坐标
//        myLocationStyle.anchor(0.5f, 0.5f);

//        myLocationStyle.strokeColor(0xff3090ff);
//        myLocationStyle.radiusFillColor(0x1c3090ff);

        //定位边框宽度
//        myLocationStyle.strokeWidth(2f);


        //设置默认定位按钮是否显示，非必需设置。
//        uiSettings.setMyLocationButtonEnabled(true);

        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
//        mMap.setMyLocationEnabled(true);

    }

    /**
     * 定位的回调
     */
    private void locationListener() {

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(aMapLocation -> {


            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {

                    LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    // 定位成功后把地图移动到当前可视区域内
                    if (mMarker != null) mMarker.destroy();
                    if (mCircle != null) mCircle.remove();

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    // 自定义定位成功后的小圆点
                    mMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.list_icon_place)));

                    // 自定义定位成功后绘制圆形
                    mCircle = mMap.addCircle(new CircleOptions().center(latLng).radius(500)
                            .fillColor(0x1c3090ff).strokeColor(0xff3090ff)
                            .strokeWidth(3f));
                } else {

                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.d("asdfghjkl", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }

        });

    }

    private void initListener() {


        mMap.setOnMarkerClickListener(marker -> {

            System.out.println("------点击覆盖物");

            return true;
        });

        mMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

                getMarkerBean();
                System.out.println("------------当前缩放比例是" + mMap.getCameraPosition().zoom);
            }
        });


    }

    /**
     * 移动到制定位置到屏幕中心点，放大级别是13，对应的是1000米
     *
     * @param latLng
     */
    public void moveToLocation(LatLng latLng) {

        //移动地图到中心点，经纬度，缩放级别3-19，俯视角0-45，偏航角
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(49.977290, 126.337000), 13, 0, 0));

        //AMap类中提供，带有移动过程的动画
        mMap.animateCamera(mCameraUpdate, 200, null);
    }

    /**
     * 绘制前先要获得绘制view的数据集合
     */
    protected void getMarkerBean() {
        //获取当前地图级别下比例尺所表示的距离大小
        float mapLevel = mMap.getCameraPosition().zoom;

        //判断是否大于1000的缩放距离
        if (mapLevel < 13) {
            if (!flag) {
                mMarkerViewBeans.clear();

                for (Marker marker : mMarkers) {
                    marker.remove();
                }
                mMarkerViewBeans.add(new MarkerViewBean(39.66919821012404, 116.59878014527642, 1, 1));
                mMarkerViewBeans.add(new MarkerViewBean(39.66838821029065, 116.59861688875051, 2, 2));
                mMarkerViewBeans.add(new MarkerViewBean(39.669242316835344, 116.59570191538805, 3, 3));
                setMarkerView(mMarkerViewBeans, 0);
            }
            flag = true;

        } else {
            if (flag) {

                for (Marker marker : mMarkers) {
                    marker.remove();
                }

                mMarkerViewBeans.clear();
                mMarkerViewBeans.add(new MarkerViewBean(39.66919821012404, 116.59878014527642, 1, 1));
                mMarkerViewBeans.add(new MarkerViewBean(39.66838821029065, 116.59861688875051, 2, 2));
                mMarkerViewBeans.add(new MarkerViewBean(39.669242316835344, 116.59570191538805, 3, 3));
                setMarkerView(mMarkerViewBeans, 1);
            }
            flag = false;
        }
    }

    /**
     * 绘制view的覆盖物
     */
    protected void setMarkerView(List<MarkerViewBean> list, int arr) {

        System.out.println("-------开始绘制覆盖物");

        for (MarkerViewBean markerViewBean : list) {

            BitmapDescriptor bdC;

            if (0 == arr) {
                View inflate = null;
                switch (markerViewBean.getNum()) {
                    case 1:
                        inflate = getLayoutInflater().inflate(R.layout.one_circle_view, null);
                        break;
                    case 2:
                        inflate = getLayoutInflater().inflate(R.layout.two_circle_view, null);
                        break;
                    case 3:
                        inflate = getLayoutInflater().inflate(R.layout.three_circle_view, null);
                        break;
                    default:
                        break;
                }
                bdC = BitmapDescriptorFactory.fromView(inflate);
            }else {

                int drawableId=0;
                switch (markerViewBean.getStatus()) {
                    case 1:
                        drawableId = R.drawable.list_green_place;
                        break;
                    case 2:
                        drawableId = R.drawable.list_orange_place;
                        break;
                    case 3:
                        drawableId = R.drawable.list_red_place;
                        break;
                    default:
                        break;
                }

                bdC=BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),drawableId));
            }

            getMarkView(markerViewBean, bdC);


        }
    }

    /**
     * 设置覆盖物对象
     * @param markerViewBean
     * @param bdC
     */
    private void getMarkView(MarkerViewBean markerViewBean, BitmapDescriptor bdC) {
        LatLng ll = new LatLng(markerViewBean.getLatitude(), markerViewBean.getLongitude());

        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(ll);
        markerOption.title("杭州市").snippet("杭州市：" + markerViewBean.getLatitude() + "," + markerViewBean.getLongitude());

        markerOption.draggable(true);//设置Marker可拖动

        markerOption.anchor(0.5f, 0.5f);

        markerOption.alpha(0.8f);

        markerOption.icon(bdC);
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果

        Marker marker = mMap.addMarker(markerOption);

        Animation animation = new AlphaAnimation(0f,1.0f);
        long duration = 1000L;
        animation.setDuration(duration);
        animation.setInterpolator(new DecelerateInterpolator());
        marker.setAnimation(animation);
        marker.startAnimation();

        marker.showInfoWindow();
        mMarkers.add(marker);

    }


    /**
     * 绘制marker-window
     */
    public void setInfoWindow(){

        mMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                View view = LayoutInflater.from(BaseMapActivity.this).inflate(R.layout.customer_details,null);
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_map);
        initView();
        mapView.onCreate(savedInstanceState);
        mMap = mapView.getMap();
        init();
        locationListener();
        initListener();
        mAMapLocationClientOption = new AMapLocationClientOption();
        mAMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mAMapLocationClientOption.setOnceLocation(true);
        //设置是否返回地址信息（默认返回地址信息）
        mAMapLocationClientOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mAMapLocationClientOption);
        mLocationClient.stopLocation();
        setInfoWindow();
        findViewById(R.id.button).setOnClickListener(view -> mLocationClient.startLocation());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
        mapView.onDestroy();
    }
}

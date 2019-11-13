package com.alguojian.map

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.AlphaAnimation
import java.util.*

open class BaseMapActivity : AppCompatActivity() {

    lateinit var mapView: MapView
    //声明AMapLocationClient类对象
    var mLocationClient: AMapLocationClient? = null

    var mAMapLocationClientOption: AMapLocationClientOption? = null

    private var mMap: AMap? = null
    private var mMarker: Marker? = null
    private var mCircle: Circle? = null

    private var flag: Boolean = false

    private val mMarkerViewBeans = ArrayList<MarkerViewBean>()

    /**
     * 用于管理所有的覆盖物
     */
    private val mMarkers = ArrayList<Marker>()


    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_base_map)
        initView()
        mapView.onCreate(savedInstanceState)
        mMap = mapView.map
        init()
        locationListener()
        initListener()
        mAMapLocationClientOption = AMapLocationClientOption()
        mAMapLocationClientOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mAMapLocationClientOption!!.isOnceLocation = true
        //设置是否返回地址信息（默认返回地址信息）
        mAMapLocationClientOption!!.isNeedAddress = true
        mLocationClient!!.setLocationOption(mAMapLocationClientOption)
        mLocationClient!!.stopLocation()
        setInfoWindow()
        findViewById<View>(R.id.button).setOnClickListener { mLocationClient!!.startLocation() }

    }


    private fun initView() {
        mapView = findViewById(R.id.mapView)
    }

    private fun init() {

        val uiSettings = mMap!!.uiSettings

        //隐藏高德logo
        uiSettings.setLogoBottomMargin(-50)

        //添加指南针
        uiSettings.isCompassEnabled = false

        //隐藏缩放按钮
        uiSettings.isZoomControlsEnabled = false

        //设置地图是否可以手势滑动
        uiSettings.isScrollGesturesEnabled = true

        // 设置地图是否可以手势缩放大小
        uiSettings.isZoomGesturesEnabled = true

        //设置地图是否可以倾斜
        uiSettings.isTiltGesturesEnabled = false

        // 设置地图是否可以旋转
        uiSettings.isRotateGesturesEnabled = false

        //显示比例尺
        uiSettings.isScaleControlsEnabled = true

        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //连续定位、且将视角移动到地图中心点，
        //定位点依照设备方向旋转，并且会跟随设备移动。
        //1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        val myLocationStyle = MyLocationStyle()

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)

        //设置定位蓝点的Style
        mMap!!.myLocationStyle = myLocationStyle

        //显示定位蓝点
        myLocationStyle.showMyLocation(false)

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
    private fun locationListener() {

        //初始化定位
        mLocationClient = AMapLocationClient(applicationContext)
        //设置定位回调监听
        mLocationClient!!.setLocationListener { aMapLocation ->

            if (aMapLocation != null) {
                if (aMapLocation.errorCode == 0) {

                    val latLng = LatLng(aMapLocation.latitude, aMapLocation.longitude)
                    // 定位成功后把地图移动到当前可视区域内
                    if (mMarker != null) mMarker!!.destroy()
                    if (mCircle != null) mCircle!!.remove()

                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    // 自定义定位成功后的小圆点
                    mMarker = mMap!!.addMarker(MarkerOptions().position(latLng)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.list_icon_place)))

                    // 自定义定位成功后绘制圆形
                    mCircle = mMap!!.addCircle(CircleOptions().center(latLng).radius(500.0)
                            .fillColor(0x1c3090ff).strokeColor(-0xcf6f01)
                            .strokeWidth(3f))
                } else {

                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.d(TAG, "location Error, ErrCode:"
                            + aMapLocation.errorCode + ", errInfo:"
                            + aMapLocation.errorInfo)
                }
            }
        }
    }

    private fun initListener() {

        mMap!!.setOnMarkerClickListener { marker ->

            Log.d(TAG, "------点击覆盖物")

            true
        }

        mMap!!.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {
            override fun onCameraChange(cameraPosition: CameraPosition) {
            }

            override fun onCameraChangeFinish(cameraPosition: CameraPosition) {

                getMarkerBean()
                Log.d(TAG, "------------当前缩放比例是" + mMap!!.cameraPosition.zoom)
            }
        })


    }

    /**
     * 移动到制定位置到屏幕中心点，放大级别是13，对应的是1000米
     *
     * @param latLng
     */
    fun moveToLocation(latLng: LatLng) {

        //移动地图到中心点，经纬度，缩放级别3-19，俯视角0-45，偏航角
        val mCameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition(LatLng(49.977290, 126.337000), 13f, 0f, 0f))

        //AMap类中提供，带有移动过程的动画
        mMap!!.animateCamera(mCameraUpdate, 200, null)
    }

    /**
     * 绘制前先要获得绘制view的数据集合
     */
    protected fun getMarkerBean() {
        //获取当前地图级别下比例尺所表示的距离大小
        val mapLevel = mMap!!.cameraPosition.zoom

        //判断是否大于1000的缩放距离
        if (mapLevel < 13) {
            if (!flag) {
                mMarkerViewBeans.clear()

                for (marker in mMarkers) {
                    marker.remove()
                }
                mMarkerViewBeans.add(MarkerViewBean(39.66919821012404, 116.59878014527642, 1, 1))
                mMarkerViewBeans.add(MarkerViewBean(39.66838821029065, 116.59861688875051, 2, 2))
                mMarkerViewBeans.add(MarkerViewBean(39.669242316835344, 116.59570191538805, 3, 3))
                setMarkerView(mMarkerViewBeans, 0)
            }
            flag = true

        } else {
            if (flag) {

                for (marker in mMarkers) {
                    marker.remove()
                }

                mMarkerViewBeans.clear()
                mMarkerViewBeans.add(MarkerViewBean(39.66919821012404, 116.59878014527642, 1, 1))
                mMarkerViewBeans.add(MarkerViewBean(39.66838821029065, 116.59861688875051, 2, 2))
                mMarkerViewBeans.add(MarkerViewBean(39.669242316835344, 116.59570191538805, 3, 3))
                setMarkerView(mMarkerViewBeans, 1)
            }
            flag = false
        }
    }

    /**
     * 绘制view的覆盖物
     */
    private fun setMarkerView(list: List<MarkerViewBean>, arr: Int) {

        Log.d(TAG, "-------开始绘制覆盖物")

        for (markerViewBean in list) {

            val bdC: BitmapDescriptor

            if (0 == arr) {
                var inflate: View? = null
                when (markerViewBean.num) {
                    1 -> inflate = layoutInflater.inflate(R.layout.one_circle_view, null)
                    2 -> inflate = layoutInflater.inflate(R.layout.two_circle_view, null)
                    3 -> inflate = layoutInflater.inflate(R.layout.three_circle_view, null)
                    else -> {
                    }
                }
                bdC = BitmapDescriptorFactory.fromView(inflate)
            } else {

                var drawableId = 0
                when (markerViewBean.status) {
                    1 -> drawableId = R.drawable.list_green_place
                    2 -> drawableId = R.drawable.list_orange_place
                    3 -> drawableId = R.drawable.list_red_place
                    else -> {
                    }
                }

                bdC = BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(resources, drawableId))
            }

            getMarkView(markerViewBean, bdC)


        }
    }

    /**
     * 设置覆盖物对象
     * @param markerViewBean
     * @param bdC
     */
    private fun getMarkView(markerViewBean: MarkerViewBean, bdC: BitmapDescriptor?) {
        val ll = LatLng(markerViewBean.latitude, markerViewBean.longitude)

        val markerOption = MarkerOptions()
        markerOption.position(ll)
        markerOption.title("杭州市").snippet("杭州市：" + markerViewBean.latitude + "," + markerViewBean.longitude)

        markerOption.draggable(true)//设置Marker可拖动

        markerOption.anchor(0.5f, 0.5f)

        markerOption.alpha(0.8f)

        markerOption.icon(bdC)
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.isFlat = true//设置marker平贴地图效果

        val marker = mMap!!.addMarker(markerOption)

        val animation = AlphaAnimation(0f, 1.0f)
        val duration = 1000L
        animation.setDuration(duration)
        animation.setInterpolator(DecelerateInterpolator())
        marker.setAnimation(animation)
        marker.startAnimation()

        marker.showInfoWindow()
        mMarkers.add(marker)

    }


    /**
     * 绘制marker-window
     */
    fun setInfoWindow() {

        mMap!!.setInfoWindowAdapter(object : AMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View {

                return LayoutInflater.from(this@BaseMapActivity).inflate(R.layout.customer_details, null)
            }

            override fun getInfoContents(marker: Marker): View? {
                return null
            }
        })


    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationClient!!.stopLocation()
        mLocationClient!!.onDestroy()
        mapView.onDestroy()
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, BaseMapActivity::class.java)
            context.startActivity(starter)
        }

        const val TAG = "alguojian"
    }
}

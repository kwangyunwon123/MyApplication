package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
//import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        PlacesListener{


    private GoogleMap mMap;
    private Marker currentMarker = null;
    private Geocoder geocoder;
    private ImageButton button;
    private EditText editText;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    List<Marker> previous_marker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.map_main);
        editText = (EditText) findViewById(R.id.editText);
        button=(ImageButton)findViewById(R.id.map_button);
        previous_marker = new ArrayList<Marker>();

        button.setOnClickListener(new View.OnClickListener() { // 장소검색 버튼 활성화

            @Override
            public void onClick(View v) {
                showPlaceInformation(currentPosition);
            }
        });


        mLayout = findViewById(R.id.layout_main);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (noman.googleplaces.Place place : places) {

                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            , place.getLongitude());

                    String markerSnippet = getCurrentAddress(latLng);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(markerSnippet);
                    Marker item = mMap.addMarker(markerOptions);
                    previous_marker.add(item);

                }

                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);

            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;
        geocoder = new Geocoder(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions mOptions = new MarkerOptions();
                // 마커 타이틀
                mOptions.title("마커 좌표");
                Double latitude = point.latitude; // 위도
                Double longitude = point.longitude; // 경도
                // 마커의 스니펫(간단한 텍스트) 설정
                mOptions.snippet(latitude.toString() + ", " + longitude.toString());
                // LatLng: 위도 경도 쌍을 나타냄
                mOptions.position(new LatLng(latitude, longitude));
                // 마커(핀) 추가
                googleMap.addMarker(mOptions);
            }
        });

        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                String str=editText.getText().toString();
                List<Address> addressList = null;
                try {
                    // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                    addressList = geocoder.getFromLocationName(
                            str, // 주소
                            10); // 최대 검색 결과 개수
                }
                catch (IOException e) {
                    e.printStackTrace();
            }

                System.out.println(addressList.get(0).toString());
                // 콤마를 기준으로 split
                String[]splitStr = addressList.get(0).toString().split(",");
                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                System.out.println(address);

                String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
                System.out.println(latitude);
                System.out.println(longitude);

                // 좌표(위도, 경도) 생성
                LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                // 마커 생성
                MarkerOptions mOptions2 = new MarkerOptions();
                mOptions2.title("search result");
                mOptions2.snippet(address);
                mOptions2.position(point);
                // 마커 추가
                mMap.addMarker(mOptions2);
                // 해당 좌표로 화면 줌
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));
            }
        });




        // 1. 마커 옵션 설정 (만드는 과정)
        MarkerOptions makerOptions = new MarkerOptions();

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker1);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 125, 125, false);
        makerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));


        double[] we1 = { 37.599976,37.264666,37.267780,37.322521,37.599976,37.278700,37.148555,37.386190,37.302155,37.407632
                ,37.296123,37.279345,37.067289,37.432403,37.443185,37.544522,37.359432,37.390076,37.485397,37.513322 ,
                37.567731,37.559293,37.557916,37.501932,37.562543,37.540342,37.538868,37.570478,37.576683,37.472696,37.493631,
                37.454896,37.507322,37.511728,36.773082,36.798395,36.694511,36.984766,36.601871,36.898025,36.349926,37.322645,
                35.959117 ,35.960859,37.143416,37.350436,35.842929, 37.301451,35.820171,36.127252,35.815538,37.318763,35.798328,36.068483
                ,37.336278,37.399058,35.978845,37.484966,37.482622,37.486763,37.478921,37.504612,37.550695,37.517929,37.562004,37.572026,37.507086
                ,36.563678,37.516807,37.495902,37.485770,37.577926,37.394380,37.554631,37.583096,37.613451,37.611962,37.649280,37.655796,37.689485
                ,37.754062,37.740364,37.621389,37.830309,37.643723,37.819939 ,35.193559,35.491904,37.872957,35.176096,35.190443,37.442411,36.038283
                ,35.239041,34.955433,35.180802,35.221904,35.158218,35.245177,35.543147,35.230397,34.856412,35.154400,35.169258,34.801823,33.514165
                ,33.499747,33.488646,34.951128,35.209093};

        double[] gyu1 = {127.130120,127.033339,127.002046,127.095518,127.044352,127.044053,127.074814,127.125411,127.010128,
                127.259827,126.993159,127.446393,127.063430,127.129859,127.140016,126.968105,126.930755,126.952214,127.122218,
                127.100133,127.008338,126.944893,126.936754,127.026856,126.985631,127.067819,127.127172,126.984678,127.030953,126.919300
                ,127.016003,126.901667,126.959063,126.944437,126.450527,127.118353,126.832891,126.922008,126.665659,126.634652,126.597264,127.095676,
                126.974264,126.957557,128.213066,127.110791,127.126365,126.865092,127.144467,128.335698,127.106462,126.837202,127.117092,
                128.346027,126.811231,126.922886,126.709062,127.035033,126.941467,126.982648,126.952641,127.005703,127.144298,127.021852
                ,127.040298,127.072970,126.890219,128.729932,126.906145,126.843049,126.811930,127.032865,126.650863,128.729932,126.999960,127.065210,
                127.029920,127.076947,127.013518,127.882117,127.065410,127.048588,126.720871,127.148692,126.627009,127.092454,128.083223,
                128.752194,127.744420,126.912693,126.824777,129.164445,129.365442,128.582624,127.484726,128.559107,128.685141,128.698770
                ,128.903686,129.260382,129.088236,128.428255,129.059434,129.176632,126.424600,126.525428,126.529084,126.489929,127.519084,128.707110};

        String[] name1 = {"렌즈미 용인김량장점","렌즈미 수원인계점","렌즈미 수원로데오점","렌즈미 수지구청역점","렌즈미 병점점","렌즈미 아주대점"
                ,"렌즈미 오산점","렌즈미 분당서현점","렌즈미 북수원점","렌즈미 경기광주점","렌즈미 수원정자점","렌즈미 이천점","렌즈미 송탄점","렌즈미 성남모란점"
                ,"렌즈미 성남점","렌즈미 숙대점","렌즈미 산본점","렌즈미 범계점","렌즈미 문정점","렌즈미 잠실역 지하상가점","렌즈미 동대문점","렌즈미 이대점",
                "렌즈미 신촌점","렌즈미 강남CGV점","렌즈미 명동로데오점","렌즈미 건대로데오거리점","렌즈미 천호점","렌즈미 종로점","렌즈미 성신여대점",
                "렌즈미 난곡점","렌즈미 교대점","렌즈미 시흥사거리점","렌즈미 중앙대점","렌즈미 노량진점","렌즈미 서산점","렌즈미 천안쌍용점","렌즈미 예산점"
                ,"렌즈미 평택안중점","렌즈미 홍성점","렌즈미 당진점","렌즈미 보령점","렌즈미 수지구청역점","렌즈미 익산롯데마트점","렌즈미 원광대점" ,"렌즈미 제천점",
                "렌즈미 미금점","렌즈미 전북대점","렌즈미 상록수점","렌즈미 전주객사점","렌즈미 원평점","렌즈미 전주신시가지점","렌즈미 안산중앙점",
                "렌즈미 전주삼천점","렌즈미 북삼점","렌즈미 안산선부점","렌즈미 안양1번가점","렌즈미 군산수송점","렌즈미 양재점","렌즈미 낙성대점",
                "렌즈미 이수점","렌즈미 서울대입구점","렌즈미 고속터미널점","렌즈미 명일역점","렌즈미 가로수길점" ,"렌즈미 한양대점","렌즈미 장안점","렌즈미 신도림점"
                ,"렌즈미 안동점","렌즈미 영등포점","렌즈미 오류역점","렌즈미 역곡점","렌즈미 제기역점","렌즈미 송도점","렌즈미 홍대점","렌즈미 대학로점","렌즈미 석계역점",
                "렌즈미 미아사거리점","렌즈미 중계은행사거리점","렌즈미 우이점","렌즈미 홍천점","렌즈미 의정부 민락점","렌즈미 의정부 행복로점","렌즈미 김포점"
                ,"렌즈미 포천송우리점","렌즈미 김포구래점","렌즈미 양주옥정점","렌즈미 진주점","렌즈미 밀양점","렌즈미 강원대점","렌즈미 전남대점","렌즈미 수완점",
                "렌즈미 삼척점","렌즈미 포항점","렌즈미 마산점","렌즈미 순천점","렌즈미 경남대점" ,"렌즈미 창원상남점","렌즈미 진해점","렌즈미 김해인제대점",
                "렌즈미 울산대점","렌즈미 부산대점","렌즈미 통영점","렌즈미 부산서면점","렌즈미 부산해운대점","렌즈미 목포하당점","렌즈미 제주점","렌즈미 제주시청점"
                ,"렌즈미 신제주점","렌즈미 순천연향점","렌즈미 창원대방점" };

        for(int i=0; i< name1.length ; i++){
            makerOptions // LatLng에 대한 어레이를 만들어서 이용할 수도 있다.
                    .position(new LatLng(we1[i] , gyu1[i]))
                    .title(name1[i]); // 타이틀.
            mMap.addMarker(makerOptions);
        }

        BitmapDrawable bitmapdraw1=(BitmapDrawable)getResources().getDrawable(R.drawable.marker2);
        Bitmap b1=bitmapdraw1.getBitmap();
        Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, 125, 125, false);
        makerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker1));

        double[] we = { 37.234818,37.199381,37.253295,37.213925,37.498473,37.209713,37.385897,37.320754,37.351240,37.411192,37.322507,37.557418
                ,37.390924,37.483465,37.498095,37.145520,37.556179,37.265510,37.517703,37.476602,37.440378,37.378071,37.537038,37.540349,37.301961,
                37.480252,37.512557,37.615282,36.996068,37.570848,37.398863,37.484000,37.506366,37.503327,37.318610,37.487467,37.598172,37.517845,
                37.559974,37.500853,37.638658,37.753958,37.823482,35.099500,34.752908,37.451677,34.954151,36.107749,33.485764,35.540632,
                35.147361,37.879549,36.336071,35.960383,35.815590,37.350557,36.781873,35.135858,35.819930,37.740165,36.328030,35.190985,36.350215
                ,35.220994,35.176368,35.245288,37.504272,35.194915,35.910383,36.122938,36.824169,34.936055,37.138325,36.350213,36.630014};

        double[] gyu = {127.203349,127.097011,127.075823,127.042362,127.027519,127.060683,127.124988,127.112087,127.109502,127.126807,127.094109
                ,126.936704,126.953285,126.928547,127.026390,127.070103,126.923433,127.000340,127.074659,126.981633,127.144985, 127.113277,127.139893,
                127.070489,127.007724,126.952922,126.943762,127.064819,127.105340,126.987366, 126.922500,126.901746,127.024321,127.051232,126.838007
                , 126.980848,127.090583,126.906168,127.041644,127.026828,127.025866,128.896183,127.049640,129.029285,127.704998,126.656947,127.485021,128.421679,
                126.487499,129.337064,126.914603,127.727552,127.447285,126.967121,127.106430,127.947849,127.000777,129.100954,127.145352,127.048595,127.428143
                ,126.825081,127.376028,128.683623,126.913368,129.215821,126.752347,128.083186,128.814896,128.114303,128.624847,127.694722, 128.210944,
                126.592553,127.505510};

        String[] name = {"오렌즈 용인김량장점" , "오렌즈 동탄영천점","오렌즈 영통점","오렌즈 병점점","오렌즈 비젼센터강남점","오렌즈 동탄점","오렌즈 서현점",
                "오렌즈 용인보정점","오렌즈 미금점","오렌즈 야탑점","오렌즈 수지점","오렌즈 신촌점", "오렌즈 범계점","오렌즈 강남점","오렌즈 신림점","오렌즈 오산점",
                "오렌즈 홍대입구역","오렌즈 AK수원점","오렌즈 잠실지하상가점","오렌즈 사당역점","오렌즈 성남점","오렌즈 분당수내역점","오렌즈 길동역점",
                "오렌즈 건대입구점","오렌즈 북수원점","오렌즈 서울대입구역점","오렌즈 노량진점","오렌즈 석계역점","오렌즈 평택소사벌점","오렌즈 종로점","오렌즈 안양점",
                "오렌즈 구로디지털점","오렌즈 신논현점","오렌즈 선릉점","오렌즈 안산점","오렌즈 이수점","오렌즈 상봉점","오렌즈 영등포점","오렌즈 한양대점",
                "오렌즈 강남시티점","오렌즈 수유점","오렌즈 강릉점","오렌즈 덕계점","오렌즈 남포점","오렌즈 여수여서점","오렌즈 인하대점","오렌즈 순천점",
                "오렌즈 구미인동점","오렌즈 신라점","오렌즈 울산삼산점","오렌즈 광주점","오렌즈 춘천점","오렌즈 우송대점","오렌즈 익산점","오렌즈 전주신시가지점",
                "오렌즈 원주점","오렌즈 온양점","오렌즈 경상대점","오렌즈 전주점","오렌즈 의정부점","오렌즈 대전점","오렌즈 광주수완점","오렌즈 둔산점",
                "오렌즈 창원점","오렌즈 전남대점","오렌즈 부산기장점","오렌즈 상동점","오렌즈 진주점","오렌즈 경산하양점","오렌즈 김천역점","오렌즈 영주점","오렌즈 광양점",
                "오렌즈 제천점","오렌즈 충남보령점","오렌즈 청주금천점"};

        for(int i=0; i<name.length ; i++){
            makerOptions // LatLng에 대한 어레이를 만들어서 이용할 수도 있다.
                    .position(new LatLng(we[i] , gyu[i]))
                    .title(name[i]); // 타이틀.
            mMap.addMarker(makerOptions);
        }

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();



        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MapActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }


        }

    };


    public void showPlaceInformation(LatLng location)
    {
        mMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyBPQ3H3z6celuTVXJnqPUSlBhUik7S1LuY")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(500) //500 미터 내에서 검색
                //.type(PlaceType.RESTAURANT) //음식점
                .build()
                .execute();
    }

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }




    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        //mMap.moveCamera(cameraUpdate);

    }


    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }
}

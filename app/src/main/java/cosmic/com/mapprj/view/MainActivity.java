package cosmic.com.mapprj.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cosmic.com.mapprj.R;
import cosmic.com.mapprj.contract.MainContract;
import cosmic.com.mapprj.model.Office;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        MainContract.view {

    GoogleMap mMap;
    BottomNavigationView bottomNavigationView;
    final static String TAG = "MainActivity";

    private final static int PERMISSIONS_REQUEST_CODE = 100;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int UPDATE_INTERVAL_MS2 = 10000; //10초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    boolean needRequest = false;

    private Marker currentMarker = null;
    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.


    Location mCurrentLocatiion;
    static LatLng currentPosition;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout;
    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소
    Office office;

    List<Office> dataList2;
    static ArrayList<Office> officeDataList;

    DataActivity dataActivity;
    static HashMap<String,Double>sortHashMap; //거리순으로 정렬된 맵


    private FragmentManager fragmentManager = getSupportFragmentManager();

    DataActivity dataFragment;
    NewsFragment newsFragment;
    RxFragment rxFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        mLayout = findViewById( R.id.layout_main );
        bottomNavigationView = findViewById( R.id.bottomNaviView );

        locationSetting();


        dataFragment = new DataActivity();
        newsFragment = new NewsFragment();
        rxFragment = new RxFragment();

        locationRequest = new LocationRequest()
                .setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY )
                .setInterval( UPDATE_INTERVAL_MS )
                .setFastestInterval( FASTEST_UPDATE_INTERVAL_MS );

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest( locationRequest );

        //위치권한 창
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( this );

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

//        getFireBaseData();

        MultiThread multiThread=new MultiThread();
        multiThread.start();
//        sortListDistance();  //스레드로 이사


        bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                switch(menuItem.getItemId()){
                    case R.id.mapViewItem:
                        //나머지 프래그먼트를 다 없애는것도 방법이겟다.
                        getSupportFragmentManager().beginTransaction().remove( dataFragment ).commit();
                        getSupportFragmentManager().beginTransaction().remove( rxFragment ).commit();
//                        getSupportFragmentManager().popBackStack();
                        break;
                    case R.id.listViewItem:
                       getSupportFragmentManager().beginTransaction().replace( R.id.map,dataFragment ).
                        addToBackStack(null).commit();
                        break;
                    case R.id.testItem:
                        getSupportFragmentManager().beginTransaction().replace(R.id.map, rxFragment)
                                .addToBackStack(null).commit();
                        break;
                }

                return false;
            }
        } );

    }


    private void locationSetting() {
        Log.d( TAG,"로케이션세팅" );
        locationRequest = new LocationRequest()
                .setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY )
                .setInterval( UPDATE_INTERVAL_MS )
                .setFastestInterval( FASTEST_UPDATE_INTERVAL_MS );

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest( locationRequest );

        //위치권한 창
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( this );


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d( TAG,"생명주기-온세이브" );
        super.onSaveInstanceState( outState );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState( savedInstanceState );

        String getInstance=savedInstanceState.getString( "temLocation" );
        Log.d( TAG,"겟인스턴스값:"+getInstance );

    }

    private void requestLocation(){
        locationRequest = new LocationRequest()
                .setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY )
                .setInterval( UPDATE_INTERVAL_MS )
                .setFastestInterval( FASTEST_UPDATE_INTERVAL_MS );

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest( locationRequest );
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void sortListDistance(){

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        dataActivity = new DataActivity();
        rootRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Gson gson = new Gson();
                sortHashMap=new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        office = snapshot1.getValue( Office.class );
                        String a = office.name;
                        String b = office.address;
                        String c = office.call;
                        String d = office.geopoint;
                        String e = office.image;
                        String f = office.url;

                        int idx = d.indexOf( "," );
                        double LatitudeString = Double.parseDouble( d.substring( 0, idx ) );
                        double LongitudeString = Double.parseDouble( d.substring( d.lastIndexOf( "," ) + 1 ) );

                        Log.d( TAG,"확인:"+ LatitudeString);
                        Log.d( TAG,"확인:"+ LongitudeString);
                        double curLat=location.getLatitude();
                        double curLon=location.getLongitude();

                        Log.d( TAG,"확인:"+ curLat);
                        Log.d( TAG,"확인"+curLon );

                        //marker
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position( new LatLng( LatitudeString, LongitudeString ) )
                                .title( a )
                                .snippet( b);
                        mMap.addMarker( markerOptions );

                        Double resultDistance=dataActivity.getDistance( curLat,curLon , LatitudeString,LongitudeString);
                        Double resultDistance2= resultDistance*0.001;
                        Double distanceDemi= Double.valueOf( String.format( "%.1f",resultDistance2) );
                        Log.d( TAG,"겟디스턴스!"+office.name+"--"+distanceDemi );
//                        //소수점 자르기
//                        Double distanceDemi= Double.valueOf( String.format( "%.1f",resultDistance) );
//                        Log.d(TAG,"소수점확인용:"+distanceDemi);
//                        double distance =distanceDemi*0.01;
//                        Log.d( TAG,"디스턴스값확인:"+distance );

                        sortHashMap.put( a,distanceDemi );
                    }

                    Log.d( TAG,"해시:"+sortHashMap.size() ); //해시로 다 다음

                }

                Object obj=gson.fromJson( String.valueOf( office ),Office.class );
                if(obj==null){
                    Toast.makeText( getApplicationContext(),"객체화 안됨",Toast.LENGTH_SHORT ).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        Log.d( TAG,"생명주기-온포즈" );


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d( TAG,"생명주기-온리쥼" );

//        if(location ==null){
//            Log.d( TAG,"생명주기-온리쥼 로케이션 널아님" );
//            location.set( null );
//        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d( TAG, "onMapReady :" );
        mMap = googleMap;

        check();
        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        mMap.getUiSettings().setMyLocationButtonEnabled( true );
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15 ) );
        mMap.setOnMapClickListener( new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :" + latLng );


            }

        } );

        mMap.setOnMarkerClickListener( this );

        mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        } );

        mMap.setOnInfoWindowClickListener( this );

//        mMap.setInfoWindowAdapter( new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                Toast.makeText( getApplicationContext(),"윈도우",Toast.LENGTH_SHORT ).show();
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                return null;
//            }
//        } );


    }




    @Override
    public boolean onMarkerClick(Marker marker) {
//        Toast.makeText( getApplicationContext(), marker.getTitle(), Toast.LENGTH_SHORT ).show();


//        return true;
        return false;// false 하면 스니펫보임
    }


    private void check() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION );
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_COARSE_LOCATION );


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale( this, REQUIRED_PERMISSIONS[0] )) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make( mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE ).setAction( "확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MainActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE );
                    }
                } ).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE );
            }

        }

    }


    LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult( locationResult );

            Log.d( TAG,"로케이션 콜백호출됨" );
            //포커스를 현재 위치로 돌아오는 것을 막음 (유효했음)
            mFusedLocationClient.removeLocationUpdates( this );

            List<Location> locationList = locationResult.getLocations();

            Log.d( TAG,"로케이션리스트사이즈: "+locationList.size() );
            if (locationList.size() > 0) {
                location = locationList.get( locationList.size() - 1 );
                //location = locationList.get(0);

                currentPosition = new LatLng( location.getLatitude(), location.getLongitude() );


                String markerTitle = getCurrentAddress( currentPosition );
                String markerSnippet = "위도:" + String.valueOf( location.getLatitude() )
                        + " 경도:" + String.valueOf( location.getLongitude() );

                Log.d( TAG, "onLocationResult : " + markerSnippet );


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation( location, markerTitle, markerSnippet );
                Log.d( TAG, "현재위치마커 작동" );

                mCurrentLocatiion = location;

            } else {
                return;
            }


        }

    };


    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d( TAG, "startLocationUpdates : call showDialogForLocationServiceSetting" );
            showDialogForLocationServiceSetting();
        } else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission( this,
                    Manifest.permission.ACCESS_FINE_LOCATION );
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission( this,
                    Manifest.permission.ACCESS_COARSE_LOCATION );


            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

                Log.d( TAG, "startLocationUpdates : 퍼미션 안가지고 있음" );
                return;
            }


            Log.d( TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates" );

            mFusedLocationClient.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper() );

            if (checkPermission())
                mMap.setMyLocationEnabled( true );

        }

    }


    @Override
    protected void onStart() {
        super.onStart();


        Log.d( TAG, "onStart" );


        if (checkPermission()) {

            Log.d( TAG, "onStart : call mFusedLocationClient.requestLocationUpdates" );
            mFusedLocationClient.requestLocationUpdates( locationRequest, locationCallback, null );

            if (mMap != null)
                mMap.setMyLocationEnabled( true );

        }


    }


    @Override
    protected void onStop() {
        Toast.makeText( getApplicationContext(),"온스탑",Toast.LENGTH_SHORT ).show();
        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d( TAG, "onStop : call stopLocationUpdates" );
            mFusedLocationClient.removeLocationUpdates( locationCallback );
        }
    }


    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder( this, Locale.getDefault() );

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1 );
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText( this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG ).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText( this, "잘못된 GPS 좌표", Toast.LENGTH_LONG ).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText( this, "주소 미발견", Toast.LENGTH_LONG ).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get( 0 );
            return address.getAddressLine( 0 ).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );

        return locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )
                || locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER );
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng( location.getLatitude(), location.getLongitude() );

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position( currentLatLng );
        markerOptions.title( markerTitle );
        markerOptions.snippet( markerSnippet );
        markerOptions.draggable( true );


        currentMarker = mMap.addMarker( markerOptions );

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng( currentLatLng );
        mMap.moveCamera( cameraUpdate );

    }


    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng( 37.56, 126.97 );
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position( DEFAULT_LOCATION );
        markerOptions.title( markerTitle );
        markerOptions.snippet( markerSnippet );
        markerOptions.draggable( true );
        markerOptions.icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_RED ) );
        currentMarker = mMap.addMarker( markerOptions );

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( DEFAULT_LOCATION, 15 );
        mMap.moveCamera( cameraUpdate );

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION );
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_COARSE_LOCATION );


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
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

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale( this, REQUIRED_PERMISSIONS[0] )
                        || ActivityCompat.shouldShowRequestPermissionRationale( this, REQUIRED_PERMISSIONS[1] )) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make( mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE ).setAction( "확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    } ).show();

                } else {


                    return;
//                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
//                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
//                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View view) {
//
 //                            finish();
//                        }
//                    }).show();
                }
            }

        }
    }

    private long time= 0;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }else {
            //백키누르면 무조건 디폴트(맵뷰)나오게 설정함
            if (System.currentTimeMillis() - time >= 2000) {
                time = System.currentTimeMillis();
                getSupportFragmentManager().beginTransaction().remove( dataFragment ).commit();
                getSupportFragmentManager().beginTransaction().remove( newsFragment ).commit();

            } else if (System.currentTimeMillis() - time < 2000) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressedListener() {
        Toast.makeText( getApplicationContext(),"토스트",Toast.LENGTH_SHORT).show();
        getSupportFragmentManager().popBackStack();


    }

    @Override
    public void showToast(String msg) {
        Toast.makeText( getApplicationContext(),msg ,Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void closeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit( 0 );
        Log.d( TAG,"생명주기-온디스토리이"+location.getLatitude() );
        //아니면 onSave로 넘겨줘? 일단 피니쉬 시키는걸로.
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
        builder.setTitle( "위치 서비스 비활성화" );
        builder.setMessage( "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?" );
        builder.setCancelable( true );
        builder.setPositiveButton( "설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                startActivityForResult( callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE );
            }
        } );
        builder.setNegativeButton( "취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        } );
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d( TAG, "onActivityResult : GPS 활성화 되있음" );


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

    private void getFireBaseData() {
//        dataList = new ArrayList<>();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Gson gson = new Gson();
                dataList2 = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int valueCount = (int) snapshot.getChildrenCount();
                    Log.d(  TAG, "카운트: " + valueCount ); //데이터 카운트 OK!
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Log.d( TAG, "겟밸류:" + snapshot1.getValue() );//완전 json 형태로 개별적으로 분리됨
                        //so..
//                        String jsonString = "{'id':'jekalmin','name':'Min','age':26,'address':'Seoul'}";
                        office = snapshot1.getValue( Office.class );
                        String a = office.name;
                        String b = office.address;
                        String c = office.call;
                        String d = office.geopoint;
                        String e = office.image;
                        String f = office.url;

                        int idx = d.indexOf( "," );
                        double LatitudeString = Double.parseDouble( d.substring( 0, idx ) );
                        double LongitudeString = Double.parseDouble( d.substring( d.lastIndexOf( "," ) + 1 ) );
                        //marker
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position( new LatLng( LatitudeString, LongitudeString ) )
                                .title( a )
                                .snippet( b);
                        mMap.addMarker( markerOptions );

//                        mMap.setOnMarkerCl ickListener( new GoogleMap.OnMarkerClickListener() {
//                            @Override
//                            public boolean onMarkerClick(Marker marker) {
//                                Toast.makeText( getApplicationContext(),"클릭됨:",Toast.LENGTH_SHORT ).show();
//                                return false;
//                            }
//                        } );


                    }



                }

                //결국엔 Gson으로 데이터 객체화 필요했음.
                Object obj=gson.fromJson( String.valueOf( office ),Office.class );
                if(obj==null){
                    Toast.makeText( getApplicationContext(),"객체화 안됨",Toast.LENGTH_SHORT ).show();
                }
//                dataList2.add( office ); //애초에 dataList2에 안들어간거...


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        InfoFragment infoFragment = new InfoFragment();
        getSupportFragmentManager().beginTransaction().replace( R.id.container,infoFragment).commit();

        Bundle bundle = new Bundle();
        bundle.putString( "tossTitle" , marker.getTitle());
        bundle.putString( "tossEtc",marker.getSnippet() );
        String check=marker.getTitle();
        Log.d( TAG,"체크:"+ check);

        infoFragment.setArguments( bundle );

    }

    public class MultiThread extends Thread{
        @Override
        public void run() {
            super.run();

            sortListDistance();

        }
    }
}

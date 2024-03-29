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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cosmic.com.mapprj.R;
import cosmic.com.mapprj.contract.MainContract;
import cosmic.com.mapprj.model.Office;

import static cosmic.com.mapprj.view.InfoFragment.isInfoFragment;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        MainContract.view {

    GoogleMap mMap;
    final static String TAG = "MainActivity";

    private final static int PERMISSIONS_REQUEST_CODE = 100;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;
    boolean needRequest = false;
    private Marker currentMarker = null;
    Location mCurrentLocatiion;
    static LatLng currentPosition;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소
    Office office;

    DataFragment dataActivity;
    static HashMap<String, Double> sortHashMap; //거리순으로 정렬된 맵

    DataFragment dataFragment;
    InfoFragment infoFragment;

    @BindView(R.id.layout_main)
    View mLayout;

    @BindView(R.id.bottomNaviView)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        ButterKnife.bind( this );

        locationSetting();

        dataFragment = new DataFragment();
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


        Thread thread = new MultiThread();
        thread.start();

        bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(isInfoFragment==true) closeFragment();//화면전환 전에 띄워놓은 프래그먼트지우기

                switch (menuItem.getItemId()) {
                    case R.id.mapViewItem:
                        getSupportFragmentManager().beginTransaction().remove( dataFragment ).commit();
                        break;
                    case R.id.listViewItem:
                            getSupportFragmentManager().beginTransaction().replace( R.id.map, dataFragment ).
                                    addToBackStack( null ).commit();
                            break;
                    case R.id.blogItem:
//                        Intent intent = new Intent( MainActivity.this, SearchActivity.class );
                        Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                        startActivity( intent );
                        break;
                }

                return false;
            }
        } );

    }

    private void locationSetting() {
        locationRequest = new LocationRequest()
                .setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY )
                .setInterval( UPDATE_INTERVAL_MS )
                .setFastestInterval( FASTEST_UPDATE_INTERVAL_MS );

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();
        builder.addLocationRequest( locationRequest );
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( this );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState( outState );
    }

    public void sortListDistance() {

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        dataActivity = new DataFragment();
        rootRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sortHashMap = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        office = snapshot1.getValue( Office.class );
                        String a = office.name;
                        String b = office.address;
                        String d = office.geopoint;

                        int idx = d.indexOf( "," );
                        double LatitudeString = Double.parseDouble( d.substring( 0, idx ) );
                        double LongitudeString = Double.parseDouble( d.substring( d.lastIndexOf( "," ) + 1 ) );
                        double curLat = location.getLatitude();
                        double curLon = location.getLongitude();

                        //marker
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position( new LatLng( LatitudeString, LongitudeString ) )
                                .title( a )
                                .snippet( b );
                        mMap.addMarker( markerOptions );

                        Double resultDistance = dataActivity.getDistance( curLat, curLon, LatitudeString, LongitudeString );
                        Double resultDistance2 = resultDistance * 0.001;
                        Double distanceDemi = Double.valueOf( String.format( "%.1f", resultDistance2 ) );
                        Log.d( TAG, "겟디스턴스!" + office.name + "--" + distanceDemi );

                        sortHashMap.put( a, distanceDemi );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d( TAG, databaseError.toString() );
            }
        } );


    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition( 0, 0 );
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        check();
        setDefaultLocation();

        mMap.getUiSettings().setMyLocationButtonEnabled( true );
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15 ) );
        mMap.setOnMapClickListener( new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                closeFragment();
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

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
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
            //포커스를 현재 위치로 돌아오는 것을 막음 (유효했음)
            mFusedLocationClient.removeLocationUpdates( this );

            List<Location> locationList = locationResult.getLocations();

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
        if (checkPermission()) {
            mFusedLocationClient.requestLocationUpdates( locationRequest, locationCallback, null );
            if (mMap != null)
                mMap.setMyLocationEnabled( true );

        }

    }


    @Override
    protected void onStop() {
        super.onStop();

        if (mFusedLocationClient != null) {
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
            showToast( "네트워크가 연결되어있지 않습니다." );
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            showToast( "잘못된 GPS 좌표" );
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            showToast( "주소 미발견" );
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

            boolean check_result = true;

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
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                }
            }

        }
    }

    private long time = 0;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) { //메인에서 백 종료시
            super.onBackPressed();
        } else {
            //프래그먼트 생성 후 종료시
            if (System.currentTimeMillis() - time >= 2000) {
                time = System.currentTimeMillis();
                getSupportFragmentManager().beginTransaction().remove( dataFragment ).commit();
            } else if (System.currentTimeMillis() - time < 2000) { //연속 2번 백키 종료
                finish();
            }
        }
    }


    @Override
    public void showToast(String msg) {
        Toast.makeText( getApplicationContext(), msg, Toast.LENGTH_LONG ).show();
    }

    public void closeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().remove( infoFragment ).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit( 0 );
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

    @Override
    public void onInfoWindowClick(Marker marker) {

        infoFragment = new InfoFragment();
        getSupportFragmentManager().beginTransaction().replace( R.id.container, infoFragment ).addToBackStack( null ).commit();

        Bundle bundle = new Bundle();
        bundle.putString( "tossTitle", marker.getTitle() );
        bundle.putString( "tossEtc", marker.getSnippet() );
        String check = marker.getTitle();
        Log.d( TAG, "체크:" + check );

        infoFragment.setArguments( bundle );

        isInfoFragment=true;
    }

    public class MultiThread extends Thread {
        @Override
        public void run() {
            sortListDistance();
        }
    }
}

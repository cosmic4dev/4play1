package cosmic.com.mapprj.view;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cosmic.com.mapprj.R;
import cosmic.com.mapprj.adapter.DataAdapter;
import cosmic.com.mapprj.contract.MainContract;
import cosmic.com.mapprj.model.CalcuDistance;
import cosmic.com.mapprj.model.Office;

import static cosmic.com.mapprj.view.MainActivity.sortHashMap;

public class DataActivity extends Fragment implements DataAdapter.ClickListener,
        BottomNavigationView.OnNavigationItemSelectedListener{

    final static String TAG = "데이터액티비티";
    RecyclerView recyclerView;
    DataAdapter dataAdapter;
    MainActivity mainActivity;
    BottomNavigationView bottomNaviView;
    Office office;
    List<Office> dataList;
    List<Office>dataList2;
    Toolbar toolbar;
    static ArrayList<CalcuDistance>testList;

    Double resultDistance;

    private Location location;
    HashMap<String,Double>sortHashMap2;

    MainContract.presenter presenter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate( R.layout.activity_data,container,false );

        recyclerView = rootView.findViewById( R.id.recyclerView );
        recyclerView.setHasFixedSize( true );

        getFireBaseDataAndSetRecyclerView();
        setSortList(sortHashMap);

        return rootView;
    }

    private void sendToAdapter(List<Office> dataList) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false );
            recyclerView.setLayoutManager( linearLayoutManager );
            DataAdapter adapter = new DataAdapter(getContext(), dataList );
            recyclerView.setAdapter( adapter );
            adapter.setOnClickListener( this );

    }


    public void getFireBaseDataAndSetRecyclerView() {
        Log.d( TAG,"파이어베이스호출" );
//        dataList = new ArrayList<>();//#1

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        dataList = new ArrayList<>();
        //sort?
        rootRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        office = snapshot1.getValue( Office.class );
                        String a = office.name;
                        String b = office.address;
                        String c = office.call;
                        String d = office.geopoint;
                        String e = office.image;
                        String f = office.url;
                        double g= office.distance;

                        int idx = d.indexOf( "," );
                        double LatitudeString = Double.parseDouble( d.substring( 0, idx ) );
                        double LongitudeString = Double.parseDouble( d.substring( d.lastIndexOf( "," ) + 1 ) );

                        //찾아본바로 dataSnapshot은 해시맵?
                        Map<String,Object> map= (Map<String, Object>) snapshot1.getValue();

                        //gson 변환 필요함. (https://hashcode.co.kr/questions/3149/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C%EC%97%90%EC%84%9C-object-%ED%83%80%EC%9E%85%EC%9D%98%EC%9E%90%EB%A3%8C%EB%A5%BC-list-%EB%A1%9C-%EB%B3%80%ED%99%98%ED%95%98%EB%8A%94-%EA%B2%83%EC%9D%B4-%EA%B6%81%EA%B8%88%ED%95%A9%EB%8B%88%EB%8B%A4)
                        //object 타입을 리스트로 변환시킴
                        GenericTypeIndicator<List<Office>> t = new GenericTypeIndicator<List<Office>>() {};
                        dataList= snapshot.getValue(t);
//                        Log.d( TAG,"dataList::"+dataList.size());//??
                        Log.d( TAG,"officeList::"+dataList.size() );

                        testList=calculator(office.name,LatitudeString,LongitudeString);
                        String ga= (String) map.get( office.name );
                        Log.d( TAG,"ga::"+ga );


                    }


                }
                sendToAdapter(dataList);
                Log.d( TAG,"dataList!!"+dataList.size() );

                Log.d( TAG, "테스트사이즈2:"+testList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        } );
//        Log.d( TAG, "테스트사이즈3:"+testList.size());
//        return dataList;

    }

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.main,menu );
        return true;
    }*/

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optionId=item.getItemId();

        if (optionId == R.id.order1) {
//            resortList(1);
            Log.d( TAG,"옵션버튼 타이밍" );
//                    Intent intent = new Intent( this, SecondActivity.class );
//                    intent.putExtra( "optionValue", 1 );
//                    startActivity( intent );
            setSortList(sortHashMap);//main쪽 해시
                    Toast.makeText( getApplicationContext(), "1", Toast.LENGTH_SHORT ).show();

        } else if (optionId == R.id.order2) {
//            resortList( 2 );
//                    Intent intent = new Intent( this, SecondActivity.class );
//                    intent.putExtra( "optionValue", 2 );
//                    startActivity( intent );
                    Toast.makeText( getApplicationContext(), "2", Toast.LENGTH_SHORT ).show();
        }
        return super.onOptionsItemSelected( item );
    }
*/


    private ArrayList<CalcuDistance> calculator(String name, double targetLat, double targetLon){
        double curLat=mainActivity.currentPosition.latitude;
        double curLon=mainActivity.currentPosition.longitude;


        resultDistance=getDistance( curLat,curLon , targetLat,targetLon);


        Log.d( TAG,"겟디스턴스 호출#" );
        Log.d( TAG,"최종 거리값 결과-"+name+" : "+ resultDistance);


        testList= new ArrayList<>();
        testList.add( new CalcuDistance( name,resultDistance ) );


        return testList;
        //여기서 값으로 정렬된 리스트를 만들어주면 오케이

//        sendSortMethod(name,resultDistance);



    }

    public double getDistance(double lat1 , double lng1 , double lat2 , double lng2 ){
        Log.d( TAG,"계산들어옴" );
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    private void setSortList(HashMap sortHashMap) {

        Log.d( TAG,"setSort시작" );


        Iterator iterator= sortByValue( sortHashMap ).iterator();
        while(iterator.hasNext()){
            String temp =(String) iterator.next();
            Log.d( TAG,"hash정렬:-"+temp );

            callSortedData(temp);
        }

    }

    private void callSortedData(String temp) { //distance 인자까지 받아와야한다.
        dataList2 = new ArrayList<>();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        Query query=rootRef.child( "coworkspaceInfo" ).( temp );
//        Log.d( TAG,"쿼리:" +query);
        dataList2 = new ArrayList<>();


        //#2
        //주요했던 쿼리
        Query query=rootRef.child( "coworkspaceInfo" ).orderByChild( "name" ).equalTo( temp );

        query.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Office office=postSnapshot.getValue(Office.class);
                    Log.d( TAG,"정렬된리스트:"+office.name ); //디비순서대로 넘어오는중.
                    Log.d( TAG,"정렬된리스트:"+office.address ); //디비순서대로 넘어오는중.
                    Log.d( TAG,"정렬된리스트:"+office.call ); //디비순서대로 넘어오는중.
                    Log.d( TAG,"정렬된리스트:"+office.geopoint ); //디비순서대로 넘어오는중.


                    //sortHash값가져오기
                    double value=sortHashMap.get( office.name );//name을 키로 값을 찾음
                    Log.d( TAG,"$$:"+value );//맞음
                    office.distance = value; //이게되면 오케이
                    dataList2.add(office);
                }

                //찾아본바로 dataSnapshot은 해시맵? __여기선 office 객체에 정보가
                //담겨있어서 GenericTypeIndicator 를 사용하지않고 바로 리스트에 add함
                Log.d( TAG,"dataList2::"+dataList2.size());
                sendToAdapter(dataList2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    public  List sortByValue(final HashMap map){
        List<String>list =new ArrayList<>();
        list.addAll( map.keySet() );

        Collections.sort( list,new Comparator(){

            @Override
            public int compare(Object o1, Object o2) {
                Object obj1=map.get(o1);
                Object obj2=map.get(o2);
                return ((Comparable)obj1).compareTo( obj2 );
            }
        } );
        return list;
    }


    @Override
    public void onItemClicked(int position) {
        String getTitle=dataList2.get( position ).getName();
        Intent intent = new Intent(getContext(),DetailActivity.class);
        intent.putExtra( "spaceName",getTitle );
        startActivity( intent );
    }

    private void sortListDistance(){

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Gson gson = new Gson();
                sortHashMap2=new HashMap<>();

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

                        Double resultDistance=getDistance( curLat,curLon , LatitudeString,LongitudeString);
                        Double resultDistance2= resultDistance*0.001;
                        Double distanceDemi= Double.valueOf( String.format( "%.1f",resultDistance2) );
                        Log.d( TAG,"겟디스턴스!"+office.name+"--"+distanceDemi );

                        sortHashMap2.put( a,distanceDemi );
                    }

                    Log.d( TAG,"해시2:"+sortHashMap2.size() ); //해시로 다 다음

                }

                Object obj=gson.fromJson( String.valueOf( office ),Office.class );
                if(obj==null){
                    Toast.makeText( getContext(),"객체화 안됨",Toast.LENGTH_SHORT ).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.isCheckable();
        return false;
    }






}

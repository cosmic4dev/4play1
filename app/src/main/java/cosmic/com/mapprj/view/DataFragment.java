package cosmic.com.mapprj.view;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cosmic.com.mapprj.R;
import cosmic.com.mapprj.adapter.DataAdapter;
import cosmic.com.mapprj.model.CalcuDistance;
import cosmic.com.mapprj.model.Office;

import static cosmic.com.mapprj.view.MainActivity.sortHashMap;

public class DataFragment extends Fragment implements DataAdapter.ClickListener,
        BottomNavigationView.OnNavigationItemSelectedListener{

    final static String TAG = "데이터프래그먼트";
    RecyclerView recyclerView;
    MainActivity mainActivity;
    Office office;
    List<Office> dataList;
    List<Office>dataList2;
    static ArrayList<CalcuDistance>testList;
    Double resultDistance;
//    private Location location;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate( R.layout.activity_data,container,false );

        recyclerView = rootView.findViewById( R.id.recyclerView );
        recyclerView.setHasFixedSize( true );

        getFireBaseData();

        BackgroundTask backgroundTask=new BackgroundTask();
        backgroundTask.execute( );

        return rootView;
    }

    private void sendToAdapter(List<Office> dataList) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false );
            recyclerView.setLayoutManager( linearLayoutManager );
            DataAdapter adapter = new DataAdapter(getContext(), dataList );
            recyclerView.setAdapter( adapter );
            adapter.setOnClickListener( this );

    }


    public void getFireBaseData() {

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        dataList = new ArrayList<>();
        //sort?
        rootRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        office = snapshot1.getValue( Office.class );
                        String d = office.geopoint;

                        int idx = d.indexOf( "," );
                        double LatitudeString = Double.parseDouble( d.substring( 0, idx ) );
                        double LongitudeString = Double.parseDouble( d.substring( d.lastIndexOf( "," ) + 1 ) );

                        //찾아본바로 dataSnapshot은 해시맵?
                        Map<String,Object> map= (Map<String, Object>) snapshot1.getValue();

                        //gson 변환 필요함. (https://hashcode.co.kr/questions/3149/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C%EC%97%90%EC%84%9C-object-%ED%83%80%EC%9E%85%EC%9D%98%EC%9E%90%EB%A3%8C%EB%A5%BC-list-%EB%A1%9C-%EB%B3%80%ED%99%98%ED%95%98%EB%8A%94-%EA%B2%83%EC%9D%B4-%EA%B6%81%EA%B8%88%ED%95%A9%EB%8B%88%EB%8B%A4)
                        //object 타입을 리스트로 변환시킴
                        GenericTypeIndicator<List<Office>> t = new GenericTypeIndicator<List<Office>>() {};
                        dataList= snapshot.getValue(t);

                        testList=calculator(office.name,LatitudeString,LongitudeString);

                    }
                }
                sendToAdapter(dataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        } );
    }

    private ArrayList<CalcuDistance> calculator(String name, double targetLat, double targetLon){
        double curLat=mainActivity.currentPosition.latitude;
        double curLon=mainActivity.currentPosition.longitude;

        resultDistance=getDistance( curLat,curLon , targetLat,targetLon);
        Log.d( TAG,"최종 거리값 결과-"+name+" : "+ resultDistance);
        testList= new ArrayList<>();
        testList.add( new CalcuDistance( name,resultDistance ) );

        return testList;
    }

    public double getDistance(double lat1 , double lng1 , double lat2 , double lng2 ){

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

        Iterator iterator= sortByValue( sortHashMap ).iterator();
        while(iterator.hasNext()){
            String temp =(String) iterator.next();
            Log.d( TAG,"hash정렬:-"+temp );
            callSortedData(temp);
        }

    }

    private void callSortedData(String temp) { //distance 인자까지 받아와야한다.

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        dataList2 = new ArrayList<>();

        //주요했던 쿼리
        Query query=rootRef.child( "coworkspaceInfo" ).orderByChild( "name" ).equalTo( temp );
        query.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Office office=postSnapshot.getValue(Office.class);

                    //sortHash값가져오기
                    double value=sortHashMap.get( office.name );//name을 키로 값을 찾음
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.isCheckable();
        return false;
    }


    class BackgroundTask extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... integers) {
            setSortList( sortHashMap );
            return false;
        }
    }



}

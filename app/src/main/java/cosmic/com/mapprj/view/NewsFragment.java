package cosmic.com.mapprj.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cosmic.com.mapprj.R;
import cosmic.com.mapprj.adapter.DataAdapter;
import cosmic.com.mapprj.adapter.RandomDataAdapter;
import cosmic.com.mapprj.model.Office;
import cosmic.com.mapprj.presenter.NewsPresenter;

public class NewsFragment extends Fragment {

    final static String TAG = "뉴스프래그먼트";
    RecyclerView recyclerViewGrid;
    DataAdapter adapter;
    GridLayoutManager layoutManager;
    Office office;

    private List<Office>listGrid;
    NewsPresenter newsPresenter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup)inflater.inflate( R.layout.activity_grid,container,false);
        getFireBaseData();

        newsPresenter=new NewsPresenter();
        callNews();

        recyclerViewGrid=rootView.findViewById( R.id.recyclerViewGrid );
        return rootView;
    }

    private void callNews() {
        newsPresenter.CallNews();
    }

    private void getFireBaseData() {
//        dataList = new ArrayList<>();
            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

            rootRef.addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Gson gson = new Gson();
                    listGrid= new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        int valueCount = (int) snapshot.getChildrenCount();
                        Log.d(  TAG, "카운트: " + valueCount ); //데이터 카운트 OK!
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Log.d( TAG, "겟밸류:" + snapshot1.getValue() );//완전 json 형태로 개별적으로 분리됨
                            office = snapshot1.getValue( Office.class );
                            String a = office.name;
                            String b = office.address;
                            String c = office.call;
                            String d = office.geopoint;
                            String e = office.image;
                            String f = office.url;


                            GenericTypeIndicator<List<Office>> t = new GenericTypeIndicator<List<Office>>() {};
                            listGrid= snapshot.getValue(t);

                        }




                    }
                    sendToAdapter( listGrid );
                    Log.d( TAG,"listGrid!!"+listGrid.size() );

                    //결국엔 Gson으로 데이터 객체화 필요했음.
                    Object obj=gson.fromJson( String.valueOf( office ), Office.class );
                    if(obj==null){
//                        Toast.makeText( getApplicationContext(),"객체화 안됨",Toast.LENGTH_SHORT ).show();
                    }
//                dataList2.add( office ); //애초에 dataList2에 안들어간거...


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );


        }

    private void sendToAdapter(List<Office> dataList) {
//        layoutManager = new GridLayoutManager(getContext(), 6);
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                int gridPosition = position %5;
//                switch (gridPosition) {
//                    case 0:
//                    case 1:
//                    case 2:
//                        return 2;
//                    case 3:
//                    case 4:
//                        return 3;
//
//
//                }
//                return 0;
//            }
//        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false );

        recyclerViewGrid.setLayoutManager( linearLayoutManager );
        RandomDataAdapter adapter = new RandomDataAdapter(getContext(), dataList );
        recyclerViewGrid.setAdapter( adapter );
//        adapter.setOnClickListener( this );

    }



}

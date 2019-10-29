package cosmic.com.mapprj.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import cosmic.com.mapprj.R;


public class InfoFragment extends Fragment {
    final static String TAG = "Fragment ";

    TextView tv_fragInfo1,tv_fragInfo2,tv_fragInfo3;
    ImageButton backBtn;
    Button detailBtn;
    static boolean isFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate( R.layout.fragment_info,container,false );
        tv_fragInfo1=rootView.findViewById( R.id.tv_fragInfo1 );
        tv_fragInfo2=rootView.findViewById( R.id.tv_fragInfo2 );
        backBtn=rootView.findViewById( R.id.backButton );



        final String getTitle=getArguments().getString( "tossTitle" );
        final String getSnippet=getArguments().getString( "tossEtc" );

        tv_fragInfo1.setText(getTitle  );
        tv_fragInfo2.setText( getSnippet );


        backBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFragment();
            }
        } );

        detailBtn=rootView.findViewById( R.id.detailBtn );
        detailBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(),DetailActivity.class);
                intent.putExtra( "spaceName",getTitle );
                startActivity( intent );

            }
        } );

        return rootView;

    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment( childFragment );
        Toast.makeText( getContext(),"온타치",Toast.LENGTH_SHORT ).show();
        isFragment = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isFragment=false;
    }

    public  void deleteFragment() {
        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove( InfoFragment.this ).commit();
        fragmentManager.popBackStack();
        Log.d( TAG,"프레그백" );
    }

}

package cosmic.com.mapprj.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import cosmic.com.mapprj.R;


public class InfoFragment extends Fragment {
    final static String TAG = "Fragment ";

    @BindView( R.id.tv_fragInfo1 )
            TextView tv_fragInfo1;
    @BindView( R.id.tv_fragInfo2 )
            TextView tv_fragInfo2;
    @BindView( R.id.backButton )
            ImageButton backBtn;
    @BindView( R.id.detailBtn )
    Button detailBtn;
    static boolean isFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate( R.layout.fragment_info,container,false );
        ButterKnife.bind( this,rootView );

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
//        Log.d( TAG,"온어태치프래그" );
//        isFragment = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        isFragment=false;
    }

    public  void deleteFragment() {
        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove( InfoFragment.this ).addToBackStack( null ).commit();
        fragmentManager.popBackStack();
    }


}

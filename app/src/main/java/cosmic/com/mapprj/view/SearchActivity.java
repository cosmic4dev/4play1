package cosmic.com.mapprj.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import cosmic.com.mapprj.R;
import cosmic.com.mapprj.adapter.SearchListAdapter;
import cosmic.com.mapprj.contract.SearchContract;
import cosmic.com.mapprj.model.BlogList;
import cosmic.com.mapprj.presenter.SearchPresenter;
import io.reactivex.annotations.Nullable;

public class SearchActivity extends AppCompatActivity implements SearchContract.view {

    final static String TAG ="써치액티비티";
    @BindView( R.id.inputText )
    EditText editText;
    @BindView( R.id.recyclerView_search )
    RecyclerView recyclerView_search;
    @Nullable
    @BindView( R.id.titleView )
    TextView titleView;
    @Nullable
    @BindView( R.id.descriptionView )
    TextView descriptionView;
    @Nullable
    @BindView( R.id.linkView )
    TextView linkView;
    @BindView( R.id.searchButton )
    Button searchButton;

    SearchPresenter presenter;
    SearchListAdapter searchListAdapter;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_search );
        ButterKnife.bind( this );
        editText.setImeOptions( EditorInfo.IME_ACTION_SEARCH );
        editText.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId){
                    case EditorInfo.IME_ACTION_DONE:
                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        break;
                    case EditorInfo.IME_ACTION_PREVIOUS:
                        break;
                    case EditorInfo.IME_ACTION_SEARCH:
                        // 검색 버튼
                        String getText=editText.getText().toString();
                        editText.setText( null );
                        presenter.requestBlogList( getText,getApplicationContext() );
                        break;
                    case EditorInfo.IME_ACTION_SEND:
                        break;
                    default:
                        break;
                }
                return false;
            }
        } );

        presenter=new SearchPresenter( this);
        recyclerView_search.setHasFixedSize( true );
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager( getApplicationContext(),RecyclerView.VERTICAL,false );
        recyclerView_search.setLayoutManager( layoutManager );

        searchButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText==null){
                    showToast( "검색어를 입력해주세요." );
                }
                String searchWord=editText.getText().toString();
                editText.setText( null );

                //volley
//                presenter.requestBlogList( getText,getApplication() );

                //rxJava 미완성 완성
                presenter.requestBlogListData( searchWord,getApplication());
                closeKeyboard();

            }
        } );


    }



//    @Override
//    public void recieveDataList(@NotNull List<BlogInfo> dataList) {
//        LinearLayoutManager layoutManager = new LinearLayoutManager( this,LinearLayoutManager.VERTICAL,false );
//        recyclerView_search.setLayoutManager( layoutManager );
//        recyclerView_search.setItemAnimator( new DefaultItemAnimator() );
//        searchListAdapter=new SearchListAdapter((ArrayList<BlogInfo>)dataList);
//
//        recyclerView_search.setAdapter( searchListAdapter );
//        searchListAdapter.notifyDataSetChanged();
//    }


    @Override
    public void showToast( String msg) {
        Toast.makeText( getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        Log.d(TAG,"ddfdd");
    }

    @Override
    public void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow( editText.getWindowToken(),0);
    }

    @Override
    public void dataToview(@NotNull BlogList dataList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager( getApplicationContext(),LinearLayoutManager.VERTICAL,false );
        recyclerView_search.setLayoutManager( layoutManager );
        recyclerView_search.setItemAnimator( new DefaultItemAnimator() );
        searchListAdapter=new SearchListAdapter(getApplicationContext(),  dataList );

        recyclerView_search.setAdapter( searchListAdapter );
        searchListAdapter.notifyDataSetChanged();
    }
}

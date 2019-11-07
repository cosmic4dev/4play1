package cosmic.com.mapprj.view;

import android.os.Bundle;
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

    @BindView(R.id.inputText)
    EditText editText;
    @BindView(R.id.recyclerView_search)
    RecyclerView recyclerView_search;
    @Nullable
    @BindView(R.id.titleView)
    TextView titleView;
    @Nullable
    @BindView(R.id.descriptionView)
    TextView descriptionView;
    @Nullable
    @BindView(R.id.linkView)
    TextView linkView;
    @BindView(R.id.searchButton)
    Button searchButton;

    SearchPresenter presenter;
    SearchListAdapter searchListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_search );
        ButterKnife.bind( this );


        editText.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchBlogAction();
                    return true;
                } else {
                    return false;
                }
            }
        } );

        presenter = new SearchPresenter( this );
        recyclerView_search.setHasFixedSize( true );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getApplicationContext(), RecyclerView.VERTICAL, false );
        recyclerView_search.setLayoutManager( layoutManager );

        searchButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBlogAction();
            }
        } );
    }

    private void searchBlogAction() {
        if (editText == null) {
            showToast( "검색어를 입력해주세요." );
        }
        String searchWord = editText.getText().toString();
        editText.setText( null );
        presenter.requestBlogListData( searchWord, getApplication() );
        closeKeyboard();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText( getApplicationContext(), msg, Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService( INPUT_METHOD_SERVICE );
        imm.hideSoftInputFromWindow( editText.getWindowToken(), 0 );
    }

    @Override
    public void dataToview(@NotNull BlogList dataList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager( getApplicationContext(), LinearLayoutManager.VERTICAL, false );
        recyclerView_search.setLayoutManager( layoutManager );
        recyclerView_search.setItemAnimator( new DefaultItemAnimator() );
        searchListAdapter = new SearchListAdapter( getApplicationContext(), dataList );
        recyclerView_search.setAdapter( searchListAdapter );
        searchListAdapter.notifyDataSetChanged();
    }
}

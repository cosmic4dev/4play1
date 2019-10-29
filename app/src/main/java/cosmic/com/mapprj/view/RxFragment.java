package cosmic.com.mapprj.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cosmic.com.mapprj.R;
import cosmic.com.mapprj.Retrofit.RetroAPI;
import cosmic.com.mapprj.Retrofit.RetrofitClient;
import cosmic.com.mapprj.adapter.NewsAdapter;
import cosmic.com.mapprj.model.NewsData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RxFragment extends Fragment {

    RetroAPI retroAPI;
    RecyclerView recycler_news;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate( R.layout.fragment_rx, container, false );

        Retrofit retrofit = RetrofitClient.getInstance();
        retroAPI = retrofit.create( RetroAPI.class );

        recycler_news = rootView.findViewById( R.id.recycler_news );
        recycler_news.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false );
        recycler_news.setLayoutManager( linearLayoutManager );

        fetchData();

        return rootView;
    }

    private void fetchData() {
        compositeDisposable.add( retroAPI.getPosts()
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new Consumer<List<NewsData>>(){
                    @Override
                    public void accept(List<NewsData> posts) throws Exception {

                        NewsAdapter adapter = new NewsAdapter(getContext(), posts );
                        recycler_news.setAdapter( adapter );
                    }
                } ));
    }
}
